package com.fx.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transfer row from the fxdb `transfer` table.
 */
public record Transfer(
        Long id,
        Integer fromAccount,
        Integer toAccount,
        BigDecimal amount,
        String currencyCode,
        LocalDateTime executedAt,
        String status
) {
}
