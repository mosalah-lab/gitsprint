package com.fx.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StatsRepository repo;

    @Test
    void returnsMarketStats() throws Exception {
        when(repo.fetch()).thenReturn(new MarketStats(200L, "EUR", "2026-01-12"));

        mvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransfers").value(200))
                .andExpect(jsonPath("$.busiestCurrency").exists())
                .andExpect(jsonPath("$.latestRateDate").value("2026-01-12"));
    }
}
