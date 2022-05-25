package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.service.AccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private AccountsService accountService;

    @PostMapping(value = "/transfer", consumes = {"application/json"})
    public ResponseEntity transferAmount(@RequestBody @Valid TransferRequest request) throws Exception {
        try {
            accountService.initiateTransfer(request);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Fail to transfer balances, please check with system administrator.");
            throw e;
        }
    }

}
