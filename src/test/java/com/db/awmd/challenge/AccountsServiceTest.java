package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    //change code to create two account
    @Before
    public void init() {
        getAccountList().forEach(account -> accountsService.createAccount(account));
    }

    @Test
    public void addAccount() throws Exception {
        assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(getAccountList().get(0).getBalance());
    }

    @Test
    public void addAccount_failsOnDuplicateId() throws Exception {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }

    }

    @Test
    public void transferRequestTest() throws Exception {
        Assert.assertTrue(accountsService.initiateTransfer(getTransactionRequest()));
    }

    @Test
    public void transferAmountOverDraftFailureTest() {
        try {
            TransferRequest transferRequest = getTransactionRequest();
            transferRequest.setAmount(new BigDecimal(15000));
            accountsService.initiateTransfer(transferRequest);
        } catch (Exception ex) {
            assertNotNull(ex.getMessage());
        }
    }

    private List<Account> getAccountList() {
        Account fromAccout = new Account("Id-123");
        fromAccout.setBalance(new BigDecimal(10000));
        Account toAccout = new Account("Id-1234");
        toAccout.setBalance(new BigDecimal(100));
        List<Account> accountList = new ArrayList<>();
        accountList.add(fromAccout);
        accountList.add(toAccout);
        return accountList;
    }

    private TransferRequest getTransactionRequest() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountFromId("Id-123");
        transferRequest.setAccountToId("Id-1234");
        transferRequest.setAmount(new BigDecimal(100));
        return transferRequest;
    }
}
