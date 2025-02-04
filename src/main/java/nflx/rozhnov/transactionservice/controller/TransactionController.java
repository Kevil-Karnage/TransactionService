package nflx.rozhnov.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import nflx.rozhnov.transactionservice.dto.request.TransactionGetRq;
import nflx.rozhnov.transactionservice.dto.request.TransactionNewRq;
import nflx.rozhnov.transactionservice.dto.response.TransactionNewRs;
import nflx.rozhnov.transactionservice.dto.response.TransactionRs;
import nflx.rozhnov.transactionservice.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transactions", produces = APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/new")
    public TransactionNewRs createNew(@RequestBody TransactionNewRq rq) {
        return service.createNewTransaction(rq);
    }

    @PostMapping("/get")
    public TransactionRs getById(@RequestBody TransactionGetRq rq) {
        return service.getTransactionById(rq);
    }
}
