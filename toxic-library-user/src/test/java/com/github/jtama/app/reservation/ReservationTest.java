package com.github.jtama.app.reservation;

import com.github.jtama.app.hostels.Hostel;
import com.github.jtama.app.rocket.Rocket;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReservationTest {

    @Test
    public void testGettersAndSetters() {
        Reservation reservation = new Reservation();
        assertNull(reservation.getUserName());
        assertEquals(0, reservation.getMonth());
        assertNull(reservation.getHostel());
        assertNull(reservation.getRocket());

        Reservation reservation2 = new Reservation("John", 5);
        assertEquals("John", reservation2.getUserName());
        assertEquals(5, reservation2.getMonth());

        Hostel hostel = new Hostel();
        hostel.setName("Hostel1");
        reservation.setHostel(hostel);
        assertEquals(hostel, reservation.getHostel());

        Rocket rocket = new Rocket();
        rocket.setName("Rocket1");
        reservation.setRocket(rocket);
        assertEquals(rocket, reservation.getRocket());

        reservation.setUserName("Alice");
        assertEquals("Alice", reservation.getUserName());

        reservation.setMonth(10);
        assertEquals(10, reservation.getMonth());
    }

    @Test
    public void testExistsByUserNameAndMonthAndHostelName() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            mock.when(() -> PanacheEntityBase.count(anyString(), any(Object[].class))).thenReturn(1L);
            assertTrue(Reservation.existsByUserNameAndMonthAndHostelName("John", 5, "Hostel1"));

            mock.when(() -> PanacheEntityBase.count(anyString(), any(Object[].class))).thenReturn(0L);
            assertFalse(Reservation.existsByUserNameAndMonthAndHostelName("John", 5, "Hostel1"));
        }
    }

    @Test
    public void testExistsByMonthAndRocketName() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            mock.when(() -> PanacheEntityBase.count(anyString(), any(Object[].class))).thenReturn(1L);
            assertTrue(Reservation.existsByMonthAndRocketName(5, "Rocket1"));

            mock.when(() -> PanacheEntityBase.count(anyString(), any(Object[].class))).thenReturn(0L);
            assertFalse(Reservation.existsByMonthAndRocketName(5, "Rocket1"));
        }
    }

    @Test
    public void testFindByUserNameAndMonthAndHostelIsNotNull() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            PanacheQuery query = mock(PanacheQuery.class);
            Reservation expected = new Reservation("John", 5);
            when(query.firstResultOptional()).thenReturn(Optional.of(expected));
            
            mock.when(() -> PanacheEntityBase.find(anyString(), any(Object[].class))).thenReturn(query);
            
            Optional<Reservation> result = Reservation.findByUserNameAndMonthAndHostelIsNotNull("John", 5);
            assertTrue(result.isPresent());
            assertEquals(expected, result.get());
        }
    }
}
