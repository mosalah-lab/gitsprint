package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests for the Rates endpoints (01-rates-listing, 02-pair-lookup). They mock
 * RateRepository, so they need no database and run in CI.
 */
@WebMvcTest(RateController.class)
class RateControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @Test
    void returnsLatestRatesWithEurUsdCheckpoint() throws Exception {
        when(repo.findLatest()).thenReturn(List.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].base").value("EUR"))
                .andExpect(jsonPath("$[0].quote").value("USD"))
                .andExpect(jsonPath("$[0].rate").value(1.0818))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-12"));
    }

    @Test
    void returnsEmptyArrayNeverA500WhenDbIsEmpty() throws Exception {
        when(repo.findLatest()).thenReturn(List.of());

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsOnePairsRate() throws Exception {
        when(repo.findLatestForPair("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.parse("2026-01-12"))));

        mvc.perform(get("/api/rates/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(1.0818));
    }

    @Test
    void unknownPairReturns404WithJsonError() throws Exception {
        when(repo.findLatestForPair("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/rates/EUR/XXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
