package com.insurance.policyholder.infrastructure.adapter.output.event;

import com.insurance.policyholder.domain.event.DomainEvent;
import com.insurance.policyholder.domain.event.PolicyHolderCreated;
import com.insurance.policyholder.domain.event.PolicyHolderUpdated;
import com.insurance.policyholder.domain.event.PolicyAdded;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.DomainEventJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.repository.DomainEventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventStoreAdapter Tests")
class EventStoreAdapterTest {

    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String MOBILE_PHONE = "0912345678";
    private static final String EMAIL = "john@example.com";
    private static final String AGGREGATE_TYPE = "PolicyHolder";

    @Mock
    private DomainEventJpaRepository jpaRepository;

    @Captor
    private ArgumentCaptor<DomainEventJpaEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<List<DomainEventJpaEntity>> entityListCaptor;

    private EventStoreAdapter adapter;
    private PolicyHolder policyHolder;

    @BeforeEach
    void setUp() {
        adapter = new EventStoreAdapter(jpaRepository);
        policyHolder = PolicyHolder.reconstitute(
                PolicyHolderId.of(POLICY_HOLDER_ID),
                NationalId.of(NATIONAL_ID),
                PersonalInfo.of(NAME, Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of(MOBILE_PHONE, EMAIL),
                Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                PolicyHolderStatus.ACTIVE,
                1L
        );
    }

    private PolicyHolderCreated createPolicyHolderCreatedEvent() {
        return new PolicyHolderCreated(
                POLICY_HOLDER_ID,
                NATIONAL_ID,
                NAME,
                "MALE",
                LocalDate.of(1990, 1, 15),
                MOBILE_PHONE,
                EMAIL,
                "Taipei Xinyi Test Street"
        );
    }

    private PolicyHolderUpdated createPolicyHolderUpdatedEvent() {
        return new PolicyHolderUpdated(policyHolder);
    }

    private PolicyAdded createPolicyAddedEvent() {
        Policy policy = Policy.create(
                PolicyType.LIFE,
                Money.twd(10000),
                Money.twd(1000000),
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );
        return new PolicyAdded(POLICY_HOLDER_ID, policy);
    }

    @Nested
    @DisplayName("Save Single Event Tests")
    class SaveSingleEventTests {

        @Test
        @DisplayName("should save PolicyHolderCreated event")
        void shouldSavePolicyHolderCreatedEvent() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.save(event);

            // Then
            verify(jpaRepository).save(entityCaptor.capture());
            DomainEventJpaEntity captured = entityCaptor.getValue();
            assertEquals(event.getEventId(), captured.getEventId());
            assertEquals(POLICY_HOLDER_ID, captured.getAggregateId());
            assertEquals(AGGREGATE_TYPE, captured.getAggregateType());
            assertEquals(PolicyHolderCreated.class.getName(), captured.getEventType());
            assertNotNull(captured.getPayload());
            assertTrue(captured.getPayload().contains("John Doe"));
        }

        @Test
        @DisplayName("should save PolicyHolderUpdated event")
        void shouldSavePolicyHolderUpdatedEvent() {
            // Given
            PolicyHolderUpdated event = createPolicyHolderUpdatedEvent();

            // When
            adapter.save(event);

            // Then
            verify(jpaRepository).save(entityCaptor.capture());
            DomainEventJpaEntity captured = entityCaptor.getValue();
            assertEquals(PolicyHolderUpdated.class.getName(), captured.getEventType());
            assertTrue(captured.getPayload().contains("0912345678"));
        }

        @Test
        @DisplayName("should save PolicyAdded event")
        void shouldSavePolicyAddedEvent() {
            // Given
            PolicyAdded event = createPolicyAddedEvent();

            // When
            adapter.save(event);

            // Then
            verify(jpaRepository).save(entityCaptor.capture());
            DomainEventJpaEntity captured = entityCaptor.getValue();
            assertEquals(PolicyAdded.class.getName(), captured.getEventType());
            assertTrue(captured.getPayload().contains("LIFE"));
        }

        @Test
        @DisplayName("should serialize event payload as JSON")
        void shouldSerializeEventPayloadAsJson() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.save(event);

            // Then
            verify(jpaRepository).save(entityCaptor.capture());
            String payload = entityCaptor.getValue().getPayload();
            assertTrue(payload.startsWith("{"));
            assertTrue(payload.endsWith("}"));
            assertTrue(payload.contains("\"aggregateId\":\"" + POLICY_HOLDER_ID + "\""));
        }

        @Test
        @DisplayName("should preserve event timestamp")
        void shouldPreserveEventTimestamp() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.save(event);

            // Then
            verify(jpaRepository).save(entityCaptor.capture());
            assertEquals(event.getOccurredOn(), entityCaptor.getValue().getOccurredOn());
        }
    }

    @Nested
    @DisplayName("Save Multiple Events Tests")
    class SaveAllEventsTests {

        @Test
        @DisplayName("should save all events")
        void shouldSaveAllEvents() {
            // Given
            List<DomainEvent> events = Arrays.asList(
                    createPolicyHolderCreatedEvent(),
                    createPolicyHolderUpdatedEvent(),
                    createPolicyAddedEvent()
            );

            // When
            adapter.saveAll(events);

            // Then
            verify(jpaRepository).saveAll(entityListCaptor.capture());
            List<DomainEventJpaEntity> captured = entityListCaptor.getValue();
            assertEquals(3, captured.size());
        }

        @Test
        @DisplayName("should save single event in list")
        void shouldSaveSingleEventInList() {
            // Given
            List<DomainEvent> events = Collections.singletonList(createPolicyHolderCreatedEvent());

            // When
            adapter.saveAll(events);

            // Then
            verify(jpaRepository).saveAll(entityListCaptor.capture());
            assertEquals(1, entityListCaptor.getValue().size());
        }

        @Test
        @DisplayName("should convert each event to entity")
        void shouldConvertEachEventToEntity() {
            // Given
            PolicyHolderCreated createdEvent = createPolicyHolderCreatedEvent();
            PolicyAdded addedEvent = createPolicyAddedEvent();
            List<DomainEvent> events = Arrays.asList(createdEvent, addedEvent);

            // When
            adapter.saveAll(events);

            // Then
            verify(jpaRepository).saveAll(entityListCaptor.capture());
            List<DomainEventJpaEntity> captured = entityListCaptor.getValue();

            assertEquals(createdEvent.getEventId(), captured.get(0).getEventId());
            assertEquals(PolicyHolderCreated.class.getName(), captured.get(0).getEventType());

            assertEquals(addedEvent.getEventId(), captured.get(1).getEventId());
            assertEquals(PolicyAdded.class.getName(), captured.get(1).getEventType());
        }
    }

    @Nested
    @DisplayName("Find By Aggregate ID Tests")
    class FindByAggregateIdTests {

        @Test
        @DisplayName("should call repository with correct aggregate ID")
        void shouldCallRepositoryWithCorrectAggregateId() {
            // Given
            when(jpaRepository.findByAggregateIdOrderByOccurredOnAsc(POLICY_HOLDER_ID))
                    .thenReturn(Collections.emptyList());

            // When
            adapter.findByAggregateId(POLICY_HOLDER_ID);

            // Then
            verify(jpaRepository).findByAggregateIdOrderByOccurredOnAsc(POLICY_HOLDER_ID);
        }

        @Test
        @DisplayName("should return empty list when no events found")
        void shouldReturnEmptyListWhenNoEventsFound() {
            // Given
            when(jpaRepository.findByAggregateIdOrderByOccurredOnAsc("NON_EXISTENT"))
                    .thenReturn(Collections.emptyList());

            // When
            List<DomainEvent> result = adapter.findByAggregateId("NON_EXISTENT");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Find By Aggregate Type Tests")
    class FindByAggregateTypeTests {

        @Test
        @DisplayName("should call repository with correct aggregate type")
        void shouldCallRepositoryWithCorrectAggregateType() {
            // Given
            when(jpaRepository.findByAggregateTypeOrderByOccurredOnAsc(AGGREGATE_TYPE))
                    .thenReturn(Collections.emptyList());

            // When
            adapter.findByAggregateType(AGGREGATE_TYPE);

            // Then
            verify(jpaRepository).findByAggregateTypeOrderByOccurredOnAsc(AGGREGATE_TYPE);
        }

        @Test
        @DisplayName("should return empty list for non-existent aggregate type")
        void shouldReturnEmptyListForNonExistentAggregateType() {
            // Given
            when(jpaRepository.findByAggregateTypeOrderByOccurredOnAsc("NonExistent"))
                    .thenReturn(Collections.emptyList());

            // When
            List<DomainEvent> result = adapter.findByAggregateType("NonExistent");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Find By Event Type Tests")
    class FindByEventTypeTests {

        @Test
        @DisplayName("should call repository with correct event type")
        void shouldCallRepositoryWithCorrectEventType() {
            // Given
            String eventType = PolicyHolderCreated.class.getName();
            when(jpaRepository.findByEventTypeOrderByOccurredOnAsc(eventType))
                    .thenReturn(Collections.emptyList());

            // When
            adapter.findByEventType(eventType);

            // Then
            verify(jpaRepository).findByEventTypeOrderByOccurredOnAsc(eventType);
        }

        @Test
        @DisplayName("should return empty list for non-existent event type")
        void shouldReturnEmptyListForNonExistentEventType() {
            // Given
            when(jpaRepository.findByEventTypeOrderByOccurredOnAsc("com.example.NonExistentEvent"))
                    .thenReturn(Collections.emptyList());

            // When
            List<DomainEvent> result = adapter.findByEventType("com.example.NonExistentEvent");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Deserialization Error Handling Tests")
    class DeserializationErrorTests {

        @Test
        @DisplayName("should throw exception for invalid event type class")
        void shouldThrowExceptionForInvalidEventTypeClass() {
            // Given
            DomainEventJpaEntity entity = new DomainEventJpaEntity(
                    "event-id",
                    POLICY_HOLDER_ID,
                    AGGREGATE_TYPE,
                    "com.invalid.NonExistentEventClass",
                    "{}",
                    LocalDateTime.now()
            );
            when(jpaRepository.findByAggregateIdOrderByOccurredOnAsc(POLICY_HOLDER_ID))
                    .thenReturn(Collections.singletonList(entity));

            // When & Then
            assertThrows(RuntimeException.class, () -> adapter.findByAggregateId(POLICY_HOLDER_ID));
        }

        @Test
        @DisplayName("should throw exception for invalid JSON payload")
        void shouldThrowExceptionForInvalidJsonPayload() {
            // Given
            DomainEventJpaEntity entity = new DomainEventJpaEntity(
                    "event-id",
                    POLICY_HOLDER_ID,
                    AGGREGATE_TYPE,
                    PolicyHolderCreated.class.getName(),
                    "invalid json",
                    LocalDateTime.now()
            );
            when(jpaRepository.findByAggregateIdOrderByOccurredOnAsc(POLICY_HOLDER_ID))
                    .thenReturn(Collections.singletonList(entity));

            // When & Then
            assertThrows(RuntimeException.class, () -> adapter.findByAggregateId(POLICY_HOLDER_ID));
        }
    }
}
