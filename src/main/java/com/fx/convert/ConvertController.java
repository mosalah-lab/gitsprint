package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import com.fx.transfer.Transfer;
import com.fx.transfer.TransferRepository;
import com.fx.web.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * GET /api/convert?base=EUR&amp;quote=USD&amp;amount=100
 * -> {amount, rate, converted, fee, total}, using the latest rate and the retail fee tiers.
 */
@RestController
public class ConvertController {

    private final RateRepository rates;
    private final TransferRepository transfers;

    public ConvertController(RateRepository rates, TransferRepository transfers) {
        this.rates = rates;
        this.transfers = transfers;
    }

    @GetMapping("/api/convert")
    public ConversionResult convert(@RequestParam String base,
                                     @RequestParam String quote,
                                     @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        Rate latest = rates.findLatestForPair(b, q)
                .orElseThrow(() -> new NotFoundException("No rate found for " + b + "/" + q));

        BigDecimal rate = latest.rate();
        BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = FeeCalculator.feeFor(amount);
        BigDecimal total = converted.subtract(fee);

        Transfer transfer = new Transfer(
            null,
            1,
            2,
            converted,
            q,
            LocalDateTime.now(),
            "COMPLETED"
        );
        transfers.add(transfer);

        return new ConversionResult(amount, rate, converted, fee, total);
    }
}
