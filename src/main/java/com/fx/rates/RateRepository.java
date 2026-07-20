package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access over the seeded `fx_rate` history table (JdbcTemplate — no JPA, matching the
 * sample slice). `fx_rate` stores one row per (base, quote, rate_date); "the rate" for a pair
 * is always the row with the most recent rate_date.
 */
@Repository
public class RateRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Rate> MAPPER = (rs, rowNum) -> new Rate(
            rs.getString("base_code"),
            rs.getString("quote_code"),
            rs.getBigDecimal("rate"),
            rs.getDate("rate_date").toLocalDate());

    public RateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Exactly one row per pair — the one with the most recent rate_date. */
    public List<Rate> findLatest() {
        return jdbc.query("""
                SELECT r.base_code, r.quote_code, r.rate, r.rate_date
                FROM fx_rate r
                INNER JOIN (
                    SELECT base_code, quote_code, MAX(rate_date) AS max_date
                    FROM fx_rate
                    GROUP BY base_code, quote_code
                ) latest
                  ON r.base_code = latest.base_code
                 AND r.quote_code = latest.quote_code
                 AND r.rate_date = latest.max_date
                ORDER BY r.base_code, r.quote_code
                """, MAPPER);
    }

    /** The most recent rate for one specific pair, if it exists. */
    public Optional<Rate> findLatestForPair(String base, String quote) {
        return jdbc.query("""
                SELECT base_code, quote_code, rate, rate_date
                FROM fx_rate
                WHERE base_code = ? AND quote_code = ?
                ORDER BY rate_date DESC
                LIMIT 1
                """, MAPPER, base, quote)
                .stream()
                .findFirst();
    }
}
