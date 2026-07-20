package com.fx.rates;

import java.math.BigDecimal;

/**
 * One row of rate history for a currency pair.
 * Contract: { "rate": 1.0818, "rateDate": "2026-01-12" }
 */
public record RateHistory(BigDecimal rate, String rateDate) {}
