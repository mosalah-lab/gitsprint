package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * INTEGRATION TEST (`./mvnw verify`, needs Docker) — real MySQL, real seed, real SQL. This is
 * the "latest per pair" query, which is worth checking against a real DB (a GROUP BY + join).
 * Skipped (not failed) if Docker isn't reachable locally; CI always has Docker.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RateRepositoryIT {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("fxdb")
            .withCopyFileToContainer(
                    MountableFile.forHostPath("ops/fxdb-seed.sql"),
                    "/docker-entrypoint-initdb.d/01-seed.sql");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    RateRepository repo;

    @Test
    void findsExactlyOneLatestRowPerSeededPair() {
        // The seed has 10 distinct (base, quote) pairs, each with 3 dated rows of history.
        assertThat(repo.findLatest()).hasSize(10);
    }

    @Test
    void eurUsdLatestMatchesTheCheckpoint() {
        assertThat(repo.findLatestForPair("EUR", "USD"))
                .isPresent()
                .get()
                .satisfies(r -> {
                    assertThat(r.rate()).isEqualByComparingTo(new BigDecimal("1.0818"));
                    assertThat(r.rateDate().toString()).isEqualTo("2026-01-12");
                });
    }

    @Test
    void unknownPairIsEmpty() {
        assertThat(repo.findLatestForPair("EUR", "XXX")).isEmpty();
    }
}
