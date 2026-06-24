package com.github.jtama.app.rocket;

import com.github.jtama.app.reservation.Reservation;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.ws.rs.core.Response;
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
public class RocketControllerTest {

    @Mock
    private RocketReservationService rocketReservationService;

    @InjectMocks
    private RocketController rocketController;

    private Rocket rocket;
    private Reservation reservation;

    @Before
    public void setUp() {
        rocket = new Rocket();
        rocket.setName("falcon-9");
        rocket.setType(RocketType.LUXURY);

        reservation = new Reservation();
        reservation.setUserName("test-user");
        reservation.setMonth(6);
        reservation.setRocket(rocket);
    }

    @Test
    public void testGetAll() {
        try (MockedStatic<PanacheEntityBase> panacheMock = mockStatic(PanacheEntityBase.class)) {
            List<PanacheEntityBase> list = new ArrayList<>();
            list.add(rocket);
            panacheMock.when(PanacheEntityBase::listAll).thenReturn(list);

            List<Rocket> result = rocketController.getAll();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("falcon-9", result.get(0).getName());
        }
    }

    @Test
    public void testCreate() {
        try (MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {
            rocketMock.when(() -> Rocket.persistIfNotExists(any(Rocket.class))).thenReturn(rocket);

            Response response = rocketController.create(rocket);
            assertNotNull(response);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertEquals(rocket, response.getEntity());
            rocketMock.verify(() -> Rocket.persistIfNotExists(rocket));
        }
    }

    @Test
    public void testBook() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test-user");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        when(rocketReservationService.book("falcon-9", 6, "test-user")).thenReturn(reservation);

        Reservation result = rocketController.book("falcon-9", 6, securityContext);
        assertNotNull(result);
        assertEquals("test-user", result.getUserName());
        assertEquals("falcon-9", result.getRocket().getName());
        assertEquals(6, result.getMonth());
    }
}
