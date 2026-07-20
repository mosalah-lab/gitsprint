package com.fx.convert;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the retail fee tiers (README §7 — "this feature has real logic, it needs the
 * most tests"). Covers each tier edge and the min-fee floor, including the checkpoints.
 */
class FeeCalculatorTest {

    @Test
    void checkpointAmount5000FeeIs25() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("5000"))).isEqualByComparingTo("25.00");
    }

    @Test
    void checkpointAmount100FeeIs1AtTheFloor() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("100"))).isEqualByComparingTo("1.00");
    }

    @Test
    void belowTier1MaxUsesRetailOnePercent() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("999"))).isEqualByComparingTo("9.99");
    }

    @Test
    void tier2LowerEdgeAt1000UsesHalfPercent() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("1000"))).isEqualByComparingTo("5.00");
    }

    @Test
    void tier2UpperEdgeAt9999UsesHalfPercent() {
        // 9999 * 0.5% = 49.995, HALF_UP rounds to 50.00
        assertThat(FeeCalculator.feeFor(new BigDecimal("9999"))).isEqualByComparingTo("50.00");
    }

    @Test
    void tier3LowerEdgeAt10000UsesQuarterPercent() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("10000"))).isEqualByComparingTo("25.00");
    }

    @Test
    void verySmallAmountHitsTheMinFeeFloor() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("1"))).isEqualByComparingTo("1.00");
    }
}
