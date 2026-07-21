package com.fx.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TransferRepository repo;

    @Test
    void recordsATransfer() throws Exception {
        when(repo.add(any())).thenReturn(1);

        mvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccount":1,"toAccount":2,"amount":100.50,"currencyCode":"USD"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(repo).add(any());
    }

    @Test
    void listsTransfersNewestFirst() throws Exception {
        when(repo.findAllNewestFirst()).thenReturn(List.of(
                new Transfer(101L, 1, 2, new BigDecimal("25.00"), "USD",
                        LocalDateTime.parse("2026-01-12T15:00:00"), "COMPLETED"),
                new Transfer(100L, 1, 2, new BigDecimal("20.00"), "USD",
                        LocalDateTime.parse("2026-01-12T14:00:00"), "COMPLETED")
        ));

        mvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[1].id").value(100));
    }
}
