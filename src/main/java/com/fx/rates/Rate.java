package com.fx.rates;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The latest rate for one currency pair. `fx_rate` holds history (many dates per pair);
 * this is always the single most-recent row for (base, quote).
 */
public record Rate(String base, String quote, BigDecimal rate, LocalDate rateDate) {
}
