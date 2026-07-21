package com.fx.stats;

/**
 * DTO returned by GET /api/stats.
 */
public record MarketStats(long totalTransfers, String busiestCurrency, String latestRateDate) {}
