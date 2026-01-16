package com.insurance.policyholder.infrastructure.adapter.output.event;

import com.insurance.policyholder.application.port.output.EventStore;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DomainEventPublisherAdapter Tests")
class DomainEventPublisherAdapterTest {

    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String MOBILE_PHONE = "0912345678";
    private static final String EMAIL = "john@example.com";

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private EventStore eventStore;

    @Captor
    private ArgumentCaptor<DomainEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventListCaptor;

    private DomainEventPublisherAdapter adapter;
    private PolicyHolder policyHolder;

    @BeforeEach
    void setUp() {
        adapter = new DomainEventPublisherAdapter(applicationEventPublisher, eventStore);
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
    @DisplayName("Single Event Publishing Tests")
    class PublishSingleEventTests {

        @Test
        @DisplayName("should save event to event store and publish to application event publisher")
        void shouldSaveAndPublishEvent() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.publish(event);

            // Then
            verify(eventStore).save(event);
            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        @DisplayName("should save to event store before publishing")
        void shouldSaveBeforePublishing() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.publish(event);

            // Then
            var inOrder = inOrder(eventStore, applicationEventPublisher);
            inOrder.verify(eventStore).save(event);
            inOrder.verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        @DisplayName("should publish PolicyHolderCreated event")
        void shouldPublishPolicyHolderCreatedEvent() {
            // Given
            PolicyHolderCreated event = createPolicyHolderCreatedEvent();

            // When
            adapter.publish(event);

            // Then
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
            DomainEvent captured = eventCaptor.getValue();
            assertInstanceOf(PolicyHolderCreated.class, captured);
            assertEquals(POLICY_HOLDER_ID, captured.getAggregateId());
        }

        @Test
        @DisplayName("should publish PolicyHolderUpdated event")
        void shouldPublishPolicyHolderUpdatedEvent() {
            // Given
            PolicyHolderUpdated event = createPolicyHolderUpdatedEvent();

            // When
            adapter.publish(event);

            // Then
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
            DomainEvent captured = eventCaptor.getValue();
            assertInstanceOf(PolicyHolderUpdated.class, captured);
        }

        @Test
        @DisplayName("should publish PolicyAdded event")
        void shouldPublishPolicyAddedEvent() {
            // Given
            PolicyAdded event = createPolicyAddedEvent();

            // When
            adapter.publish(event);

            // Then
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
            DomainEvent captured = eventCaptor.getValue();
            assertInstanceOf(PolicyAdded.class, captured);
        }
    }

    @Nested
    @DisplayName("Multiple Events Publishing Tests")
    class PublishAllEventsTests {

        @Test
        @DisplayName("should save all events to event store and publish each")
        void shouldSaveAllAndPublishEach() {
            // Given
            List<DomainEvent> events = Arrays.asList(
                    createPolicyHolderCreatedEvent(),
                    createPolicyHolderUpdatedEvent()
            );

            // When
            adapter.publishAll(events);

            // Then
            verify(eventStore).saveAll(events);
            verify(applicationEventPublisher, times(2)).publishEvent(any(DomainEvent.class));
        }

        @Test
        @DisplayName("should not publish when events list is null")
        void shouldNotPublishWhenEventsListIsNull() {
            // When
            adapter.publishAll(null);

            // Then
            verify(eventStore, never()).saveAll(any());
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("should not publish when events list is empty")
        void shouldNotPublishWhenEventsListIsEmpty() {
            // When
            adapter.publishAll(Collections.emptyList());

            // Then
            verify(eventStore, never()).saveAll(any());
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("should batch save before publishing events")
        void shouldBatchSaveBeforePublishing() {
            // Given
            List<DomainEvent> events = Arrays.asList(
                    createPolicyHolderCreatedEvent(),
                    createPolicyAddedEvent()
            );

            // When
            adapter.publishAll(events);

            // Then
            var inOrder = inOrder(eventStore, applicationEventPublisher);
            inOrder.verify(eventStore).saveAll(events);
            inOrder.verify(applicationEventPublisher, times(2)).publishEvent(any(DomainEvent.class));
        }

        @Test
        @DisplayName("should publish each event in order")
        void shouldPublishEachEventInOrder() {
            // Given
            PolicyHolderCreated createdEvent = createPolicyHolderCreatedEvent();
            PolicyHolderUpdated updatedEvent = createPolicyHolderUpdatedEvent();
            PolicyAdded addedEvent = createPolicyAddedEvent();
            List<DomainEvent> events = Arrays.asList(createdEvent, updatedEvent, addedEvent);

            // When
            adapter.publishAll(events);

            // Then
            verify(applicationEventPublisher, times(3)).publishEvent(eventCaptor.capture());
            List<DomainEvent> capturedEvents = eventCaptor.getAllValues();
            assertEquals(3, capturedEvents.size());
            assertSame(createdEvent, capturedEvents.get(0));
            assertSame(updatedEvent, capturedEvents.get(1));
            assertSame(addedEvent, capturedEvents.get(2));
        }

        @Test
        @DisplayName("should publish single event in list")
        void shouldPublishSingleEventInList() {
            // Given
            List<DomainEvent> events = Collections.singletonList(createPolicyHolderCreatedEvent());

            // When
            adapter.publishAll(events);

            // Then
            verify(eventStore).saveAll(events);
            verify(applicationEventPublisher, times(1)).publishEvent(any(DomainEvent.class));
        }
    }
}
