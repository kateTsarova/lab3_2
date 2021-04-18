package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    private Clock clock;

    private Order order;
    private Instant startTime;
    private Instant testTime;

    @BeforeEach
    void setUp() throws Exception {
        order = new Order(clock);
        startTime = Instant.now();
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void whenInvalidHours_cancelOrderState() {
        testTime = startTime.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(clock.instant()).thenReturn(startTime).thenReturn(testTime);

        order.submit();
        try {
            order.confirm();
        } catch (OrderExpiredException e) {
            assertEquals(Order.State.CANCELLED, order.getOrderState());
        }
    }

    @Test
    void whenInvalidHours_throwException() {
        testTime = startTime.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(clock.instant()).thenReturn(startTime).thenReturn(testTime);

        order.submit();

        assertThrows(OrderExpiredException.class, () -> order.confirm());
    }

    @Test
    void whenValidHours_confirmOrderState() {
        testTime = startTime.plus(Order.VALID_PERIOD_HOURS - 1, ChronoUnit.HOURS);

        when(clock.instant()).thenReturn(startTime).thenReturn(testTime);

        order.submit();
        order.confirm();

        assertEquals(Order.State.CONFIRMED, order.getOrderState());
    }

    @Test
    void whenValidHours_exceptionNotThrown() {
        testTime = startTime.plus(Order.VALID_PERIOD_HOURS - 1, ChronoUnit.HOURS);

        when(clock.instant()).thenReturn(startTime).thenReturn(testTime);

        order.submit();

        assertDoesNotThrow(() -> order.confirm());
    }
}
