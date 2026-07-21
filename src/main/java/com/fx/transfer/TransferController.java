package com.fx.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transfers API: write one transfer and list transfer history newest-first.
 */
@RestController
public class TransferController {

    private final TransferRepository repo;

    public TransferController(TransferRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/api/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer add(@RequestBody Transfer request) {
        if (request == null
                || request.fromAccount() == null || request.fromAccount() <= 0
                || request.toAccount() == null || request.toAccount() <= 0
                || request.amount() == null || request.amount().signum() <= 0
                || request.currencyCode() == null || request.currencyCode().isBlank()) {
            throw new IllegalArgumentException(
                    "fromAccount, toAccount, amount (> 0), and currencyCode are required");
        }

        Transfer transfer = new Transfer(
                null,
                request.fromAccount(),
                request.toAccount(),
                request.amount(),
                request.currencyCode().toUpperCase(),
                LocalDateTime.now(),
                "COMPLETED"
        );
        repo.add(transfer);
        return transfer;
    }

    @GetMapping("/api/transfers")
    public List<Transfer> all() {
        return repo.findAllNewestFirst();
    }
}
