package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

        // Reset the existing accounts before each test.
        accountsService.getAccountsRepository().clearAccounts();
        getAccountList().forEach(account -> accountsService.createAccount(account));
    }

    @Test
    public void transferAmountTest() throws Exception {
        this.mockMvc.perform(post("/v1/transaction/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\": \"Id-123\",\"accountToId\": \"Id-1234\",\"amount\": \"100\"}"))
                .andExpect(status().isAccepted());
    }

    private List<Account> getAccountList() {
        Account fromAccout = new Account("Id-123");
        fromAccout.setBalance(new BigDecimal(10000));
        Account toAccout = new Account("Id-1234");
        toAccout.setBalance(new BigDecimal(100));
        List<Account> accoutList = new ArrayList<>();
        accoutList.add(fromAccout);
        accoutList.add(toAccout);
        return accoutList;
    }
}
