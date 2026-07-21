package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RateHistoryController {

    private final RateHistoryRepository repo;

    public RateHistoryController(RateHistoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/rates/{base}/{quote}/history")
    public List<RateHistory> history(@PathVariable String base, @PathVariable String quote) {
        return repo.findHistory(base.toUpperCase(), quote.toUpperCase());
    }
}
