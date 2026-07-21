package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import com.fx.transfer.TransferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Web-slice tests for GET /api/convert (03-conversion-calculator). Mocks RateRepository. */
@WebMvcTest(ConvertController.class)
class ConvertControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @MockBean
    TransferRepository transfers;

    @Test
    void convertsUsingTheLatestRateCheckpoint() throws Exception {
        when(repo.findLatestForPair("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));
        when(transfers.add(any())).thenReturn(1);

        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.converted").value(108.18))
                .andExpect(jsonPath("$.fee").value(1.00))
                .andExpect(jsonPath("$.total").value(107.18));

        verify(transfers).add(any());
    }

    @Test
    void amountAtOrBelowZeroReturns400() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void missingAmountParamReturns400WithJsonError() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void nonNumericAmountReturns400WithJsonError() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void unknownPairReturns404() throws Exception {
        when(repo.findLatestForPair("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "XXX").param("amount", "100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
