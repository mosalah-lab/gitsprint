package com.fx.convert;

import java.math.BigDecimal;

/** The response shape for GET /api/convert: {amount, rate, converted, fee, total}. */
public record ConversionResult(BigDecimal amount, BigDecimal rate, BigDecimal converted,
                                BigDecimal fee, BigDecimal total) {
}
