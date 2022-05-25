package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.CustomerInfo;
import com.db.awmd.challenge.domain.TransactionInfo;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.OverDraftException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;


    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }


    private final Random number = new Random(123L);

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    /**
     * this method is start fund transferring process from one account to another account.
     *
     * @param transferRequest
     */
    public boolean initiateTransfer(TransferRequest transferRequest) throws Exception {
        try {
            Account fromAccount = getAccountByAccountId(transferRequest.getAccountFromId());
            Account toAccount = getAccountByAccountId(transferRequest.getAccountToId());
            depositAmount(fromAccount, toAccount, transferRequest.getAmount());
        } catch (Exception e) {
            throw new Exception("error occurred while transferring amount from one account to another. ");// Reset interrupted status
        }
        return true;
    }

    /**
     * following method is use to transfer fund from one account to another.
     *
     * @param amount
     * @param fromAccount
     * @param toAccount
     */
    public void depositAmount(Account fromAccount, Account toAccount, BigDecimal amount) throws Exception {
        while (true) {
            if (fromAccount.getLock().tryLock()) {
                try {
                    if (toAccount.getLock().tryLock()) {
                        try {
                            if (fromAccount.getBalance().compareTo(amount) < 0) {
                                throw new OverDraftException("Account with id:" + fromAccount.getAccountId() + " does not have enough balance to transfer.");
                            }
                            BigDecimal fromAccountBalance = fromAccount.getBalance().subtract(amount);
                            fromAccount.setBalance(fromAccountBalance);
                            toAccount.setBalance(toAccount.getBalance().add(amount.abs()));
                            //write code to updates both account into the DB
                            //build transaction info object and also send notification to customer
                            emailNotificationService.notifyAboutTransfer(toAccount, buildTransactionInfo(toAccount));
                            emailNotificationService.notifyAboutTransfer(fromAccount, buildTransactionInfo(fromAccount));
                            break;
                        } finally {
                            toAccount.getLock().unlock();
                        }
                    }
                } finally {
                    fromAccount.getLock().unlock();
                }
            }
            int n = number.nextInt(1000);
            int TIME = 1000 + n; // 1 second + random delay to prevent livelock
            Thread.sleep(TIME);
        }
    }

    private Account getAccountByAccountId(String accountId) {
        return Optional.ofNullable(accountsRepository.getAccount(accountId))
                .orElseThrow(() -> new AccountNotExistException("Account with id:" + accountId + " does not exist."));

    }

    private String buildTransactionInfo(Account account) throws JsonProcessingException {
        TransactionInfo transactionInfo = new TransactionInfo();
        CustomerInfo customerInfo = new CustomerInfo();
        //build customer/account holder details from account id.
        customerInfo.setCustomerId(account.getAccountId());
        customerInfo.setCustomerId(String.valueOf(number.nextInt()));
        transactionInfo.setCustomerInfo(customerInfo);
        transactionInfo.setTransferAmount(account.getBalance());
        return new ObjectMapper().writeValueAsString(transactionInfo);
    }

}
