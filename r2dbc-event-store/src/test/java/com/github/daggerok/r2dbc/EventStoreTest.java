package com.github.daggerok.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Log4j2
@SpringBootTest
@DisplayName("An event store tests")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventStoreTest {

  @Autowired
  EventStore eventStore;

  @BeforeEach
  void setUp() {
    eventStore.deleteAll().subscribe(log::info);
  }

  @Test
  void should_save_event() {
    // given
    var aggregateId = UUID.randomUUID();
    var expireAt = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
    // then
    StepVerifier.create(eventStore.save(VisitorRegisteredEvent.of(aggregateId, "A test", expireAt)))
                .consumeNextWith(log::info)
                .verifyComplete();
  }

  @Test
  void should_find_all() {
    // given
    StepVerifier.create(eventStore.save(PassCardDeliveredEvent.of(UUID.randomUUID())))
                .consumeNextWith(log::info)
                .verifyComplete();
    // then
    StepVerifier.create(eventStore.findAll()) // .consumeNextWith(log::info)
                .consumeNextWith(log::info)
                .verifyComplete();
  }

  @Test
  void should_find_by_aggregate_id() {
    StepVerifier.create(eventStore.findByAggregateIdOrderByAggregateIdAsc(UUID.fromString("0-0-0-0-1")))
                // .consumeNextWith(log::info)
                .verifyComplete();
  }
}
