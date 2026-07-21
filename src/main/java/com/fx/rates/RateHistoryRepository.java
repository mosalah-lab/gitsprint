package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RateHistoryRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<RateHistory> MAPPER = (rs, rowNum) -> new RateHistory(
            rs.getBigDecimal("rate"),
            rs.getString("rate_date"));

    public RateHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<RateHistory> findHistory(String base, String quote) {
        return jdbc.query(
                "SELECT rate, rate_date FROM fx_rate WHERE base_code = ? AND quote_code = ? ORDER BY rate_date ASC",
                MAPPER, base, quote);
    }
}
