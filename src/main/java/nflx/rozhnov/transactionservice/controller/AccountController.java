package nflx.rozhnov.transactionservice.controller;

import nflx.rozhnov.transactionservice.dto.response.TransactionHistoryRs;
import nflx.rozhnov.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/account", produces = APPLICATION_JSON_VALUE)
public class AccountController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/history/{id}")
    public TransactionHistoryRs getAccountTransactions(@PathVariable("id") long accountId) {
        return transactionService.getAccountTransactions(accountId);
    }
}
