package com.fx.stats;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final StatsRepository repo;

    public StatsController(StatsRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/stats")
    public MarketStats stats() {
        return repo.fetch();
    }
}
