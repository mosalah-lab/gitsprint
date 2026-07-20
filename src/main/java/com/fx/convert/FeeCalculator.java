package com.fx.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Retail fee tiers, applied to the amount being converted:
 * &lt;1000 -> 1.0%, 1000-9999 -> 0.5%, &ge;10000 -> 0.25%, with a 1.00 minimum fee.
 */
public final class FeeCalculator {

    private static final BigDecimal TIER_2_MIN = new BigDecimal("1000");
    private static final BigDecimal TIER_3_MIN = new BigDecimal("10000");
    private static final BigDecimal TIER_1_RATE = new BigDecimal("0.01");
    private static final BigDecimal TIER_2_RATE = new BigDecimal("0.005");
    private static final BigDecimal TIER_3_RATE = new BigDecimal("0.0025");
    private static final BigDecimal MIN_FEE = new BigDecimal("1.00");

    private FeeCalculator() {
    }

    public static BigDecimal feeFor(BigDecimal amount) {
        BigDecimal rate = amount.compareTo(TIER_2_MIN) < 0 ? TIER_1_RATE
                : amount.compareTo(TIER_3_MIN) < 0 ? TIER_2_RATE
                : TIER_3_RATE;
        BigDecimal fee = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return fee.max(MIN_FEE);
    }
}
