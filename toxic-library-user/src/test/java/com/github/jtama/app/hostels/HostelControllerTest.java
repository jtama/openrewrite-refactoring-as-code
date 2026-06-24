package com.github.jtama.app.hostels;

import com.github.jtama.app.reservation.Reservation;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HostelControllerTest {

    @Mock
    private HostelReservationService hostelReservationService;

    @InjectMocks
    private HostelController hostelController;

    private Hostel hostel;
    private Reservation reservation;

    @Before
    public void setUp() {
        hostel = new Hostel();
        hostel.setName("hostel-test");

        reservation = new Reservation();
        reservation.setUserName("test-user");
        reservation.setMonth(6);
        reservation.setHostel(hostel);
    }

    @Test
    public void testGetAll() {
        try (MockedStatic<PanacheEntityBase> panacheMock = mockStatic(PanacheEntityBase.class)) {
            List<PanacheEntityBase> list = new ArrayList<>();
            list.add(hostel);
            panacheMock.when(PanacheEntityBase::listAll).thenReturn(list);

            List<Hostel> result = hostelController.getAll();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("hostel-test", result.get(0).getName());
        }
    }

    @Test
    public void testCreate() {
        try (MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {
            hostelMock.when(() -> Hostel.persistIfNotExists(any(Hostel.class))).thenReturn(hostel);

            Hostel result = hostelController.create(hostel);
            assertNotNull(result);
            assertEquals("hostel-test", result.getName());
            hostelMock.verify(() -> Hostel.persistIfNotExists(hostel));
        }
    }

    @Test
    public void testBook() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test-user");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        when(hostelReservationService.book("hostel-test", 6, "test-user")).thenReturn(reservation);

        Reservation result = hostelController.book("hostel-test", 6, securityContext);
        assertNotNull(result);
        assertEquals("test-user", result.getUserName());
        assertEquals("hostel-test", result.getHostel().getName());
        assertEquals(6, result.getMonth());
    }
}
