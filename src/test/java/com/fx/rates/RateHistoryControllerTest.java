package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateHistoryController.class)
class RateHistoryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateHistoryRepository repo;

    @Test
    void returnsHistoryOldestToNewest() throws Exception {
        when(repo.findHistory("EUR", "USD")).thenReturn(List.of(
                new RateHistory(new BigDecimal("1.0812"), "2026-01-10"),
                new RateHistory(new BigDecimal("1.0815"), "2026-01-11"),
                new RateHistory(new BigDecimal("1.0818"), "2026-01-12")));

        mvc.perform(get("/api/rates/EUR/USD/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-10"))
                .andExpect(jsonPath("$[2].rate").value(1.0818))
                .andExpect(jsonPath("$[2].rateDate").value("2026-01-12"));
    }

    @Test
    void unknownPairReturnsEmptyArray() throws Exception {
        when(repo.findHistory("XYZ", "ABC")).thenReturn(List.of());

        mvc.perform(get("/api/rates/XYZ/ABC/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
