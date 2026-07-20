package com.fx.rates;

import com.fx.web.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * GET /api/rates              -> 200 + every pair's latest rate (never a 500 on an empty DB).
 * GET /api/rates/{base}/{quote} -> 200 + one rate, or 404 + JSON {error} if the pair is unknown.
 */
@RestController
public class RateController {

    private final RateRepository repo;

    public RateController(RateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/rates")
    public List<Rate> all() {
        return repo.findLatest();
    }

    @GetMapping("/api/rates/{base}/{quote}")
    public Rate one(@PathVariable String base, @PathVariable String quote) {
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        return repo.findLatestForPair(b, q)
                .orElseThrow(() -> new NotFoundException("No rate found for " + b + "/" + q));
    }
}
