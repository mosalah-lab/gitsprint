package com.fx.stats;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatsRepository {

    private final JdbcTemplate jdbc;

    public StatsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public MarketStats fetch() {
        long totalTransfers = jdbc.queryForObject(
                "SELECT COUNT(*) FROM transfer", Long.class);

        String busiestCurrency = jdbc.queryForObject(
                "SELECT currency_code FROM transfer GROUP BY currency_code ORDER BY COUNT(*) DESC LIMIT 1",
                String.class);

        String latestRateDate = jdbc.queryForObject(
                "SELECT MAX(rate_date) FROM fx_rate", String.class);

        return new MarketStats(totalTransfers, busiestCurrency, latestRateDate);
    }
}
