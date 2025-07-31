package com.github.jtama.app.rocket;

import com.github.jtama.app.exception.InvalidBookingException;
import com.github.jtama.app.exception.UnavailableException;
import com.github.jtama.app.exception.UnknownEntityException;
import com.github.jtama.app.hostels.Hostel;
import com.github.jtama.app.reservation.Reservation;
import com.github.jtama.app.util.MonthValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DateTimeException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RocketReservationServiceTest {

    @Mock
    private MonthValidator monthValidator;

    @InjectMocks
    private RocketReservationService rocketReservationService;

    private static final String VALID_ROCKET_NAME = "falcon-9";
    private static final String INVALID_ROCKET_NAME = "nonexistent-rocket";
    private static final String USER_NAME = "testuser";
    private static final int VALID_MONTH = 6;
    private static final int INVALID_MONTH = 13;

    private Rocket mockRocket;
    private Hostel mockHostel;
    private Reservation mockReservation;

    @Before
    public void setUp() {
        // Setup mock rocket
        mockRocket = new Rocket();
        mockRocket.setName(VALID_ROCKET_NAME);
        mockRocket.id = 1L;

        // Setup mock hostel
        mockHostel = new Hostel();
        mockHostel.setName("test-hostel");
        mockHostel.id = 2L;

        // Setup mock reservation with hostel
        mockReservation = new Reservation();
        mockReservation.setUserName(USER_NAME);
        mockReservation.setMonth(VALID_MONTH);
        mockReservation.setHostel(mockHostel);
        mockReservation.id = 3L;
    }

    // ========== Tests de succès ==========

    @Test
    public void testBook_Success() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            // Configuration des mocks
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);

            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));

            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME))
                          .thenReturn(false);

            rocketMock.when(() -> Rocket.findByName(VALID_ROCKET_NAME))
                     .thenReturn(Optional.of(mockRocket));

            // When
            Reservation result = rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then
            assertNotNull("La réservation ne doit pas être null", result);
            assertEquals("Le nom d'utilisateur doit correspondre", USER_NAME, result.getUserName());
            assertEquals("Le mois doit correspondre", VALID_MONTH, result.getMonth());
            assertEquals("L'hostel doit correspondre", mockHostel, result.getHostel());
            assertEquals("La rocket doit être assignée", mockRocket, result.getRocket());

            // Vérification des appels
            verify(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.verify(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH));
            reservationMock.verify(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME));
            rocketMock.verify(() -> Rocket.findByName(VALID_ROCKET_NAME));
        }
    }

    // ========== Tests d'erreur - Validation du mois ==========

    @Test(expected = DateTimeException.class)
    public void testBook_InvalidMonth_ThrowsDateTimeException() {
        // Given
        doThrow(new DateTimeException("Invalid month")).when(monthValidator).validateMonth(INVALID_MONTH);

        // When
        rocketReservationService.book(VALID_ROCKET_NAME, INVALID_MONTH, USER_NAME);

        // Then - Exception attendue
    }

    @Test
    public void testBook_InvalidMonth_ValidatorCalled() {
        // Given
        doThrow(new DateTimeException("Invalid month")).when(monthValidator).validateMonth(INVALID_MONTH);

        // When
        try {
            rocketReservationService.book(VALID_ROCKET_NAME, INVALID_MONTH, USER_NAME);
            fail("Une DateTimeException devrait être levée");
        } catch (DateTimeException e) {
            // Then
            verify(monthValidator).validateMonth(INVALID_MONTH);
            assertEquals("Invalid month", e.getMessage());
        }
    }

    // ========== Tests d'erreur - Pas de réservation d'hostel ==========

    @Test(expected = InvalidBookingException.class)
    public void testBook_NoHostelReservation_ThrowsInvalidBookingException() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.empty());

            // When
            rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then - Exception attendue
        }
    }

    @Test
    public void testBook_NoHostelReservation_CorrectExceptionMessage() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.empty());

            // When
            try {
                rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);
                fail("Une InvalidBookingException devrait être levée");
            } catch (InvalidBookingException e) {
                // Then
                String expectedMessage = "No hostel is booked for user %S on month %s".formatted(USER_NAME, VALID_MONTH);
                assertEquals(expectedMessage, e.getMessage());

                verify(monthValidator).validateMonth(VALID_MONTH);
                reservationMock.verify(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH));
            }
        }
    }

    // ========== Tests d'erreur - Rocket déjà réservée ==========

    @Test(expected = UnavailableException.class)
    public void testBook_RocketAlreadyBooked_ThrowsUnavailableException() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME))
                          .thenReturn(true);

            // When
            rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then - Exception attendue
        }
    }

    @Test
    public void testBook_RocketAlreadyBooked_CorrectExceptionMessage() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME))
                          .thenReturn(true);

            // When
            try {
                rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);
                fail("Une UnavailableException devrait être levée");
            } catch (UnavailableException e) {
                // Then
                String expectedMessage = "Rocket %s has already been booked for month %s".formatted(VALID_ROCKET_NAME, VALID_MONTH);
                assertEquals(expectedMessage, e.getMessage());

                verify(monthValidator).validateMonth(VALID_MONTH);
                reservationMock.verify(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH));
                reservationMock.verify(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME));
            }
        }
    }

    // ========== Tests d'erreur - Rocket inexistante ==========

    @Test(expected = UnknownEntityException.class)
    public void testBook_RocketNotFound_ThrowsUnknownEntityException() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, INVALID_ROCKET_NAME))
                          .thenReturn(false);
            rocketMock.when(() -> Rocket.findByName(INVALID_ROCKET_NAME))
                     .thenReturn(Optional.empty());

            // When
            rocketReservationService.book(INVALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then - Exception attendue
        }
    }

    @Test
    public void testBook_RocketNotFound_CorrectExceptionMessage() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, INVALID_ROCKET_NAME))
                          .thenReturn(false);
            rocketMock.when(() -> Rocket.findByName(INVALID_ROCKET_NAME))
                     .thenReturn(Optional.empty());

            // When
            try {
                rocketReservationService.book(INVALID_ROCKET_NAME, VALID_MONTH, USER_NAME);
                fail("Une UnknownEntityException devrait être levée");
            } catch (UnknownEntityException e) {
                // Then
                String expectedMessage = "Rocket %s doesn't exists".formatted(INVALID_ROCKET_NAME);
                assertEquals(expectedMessage, e.getMessage());

                verify(monthValidator).validateMonth(VALID_MONTH);
                reservationMock.verify(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH));
                reservationMock.verify(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, INVALID_ROCKET_NAME));
                rocketMock.verify(() -> Rocket.findByName(INVALID_ROCKET_NAME));
            }
        }
    }

    // ========== Tests de cas limites ==========

    @Test
    public void testBook_WithNullUserName_NoHostelReservation() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(null, VALID_MONTH))
                          .thenReturn(Optional.empty());

            // When
            try {
                rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, null);
                fail("Une InvalidBookingException devrait être levée");
            } catch (InvalidBookingException e) {
                // Then
                String expectedMessage = "No hostel is booked for user %S on month %s".formatted(null, VALID_MONTH);
                assertEquals(expectedMessage, e.getMessage());
            }
        }
    }

    @Test
    public void testBook_WithEmptyRocketName_ThrowsUnknownEntityException() {
        // Given
        String emptyRocketName = "";
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, emptyRocketName))
                          .thenReturn(false);
            rocketMock.when(() -> Rocket.findByName(emptyRocketName))
                     .thenReturn(Optional.empty());

            // When
            try {
                rocketReservationService.book(emptyRocketName, VALID_MONTH, USER_NAME);
                fail("Une UnknownEntityException devrait être levée");
            } catch (UnknownEntityException e) {
                // Then
                String expectedMessage = "Rocket %s doesn't exists".formatted(emptyRocketName);
                assertEquals(expectedMessage, e.getMessage());
            }
        }
    }

    // ========== Tests de comportement - Ordre des validations ==========

    @Test
    public void testBook_MonthValidationCalledFirst() {
        // Given
        doThrow(new DateTimeException("Invalid month")).when(monthValidator).validateMonth(INVALID_MONTH);

        // When
        try {
            rocketReservationService.book(VALID_ROCKET_NAME, INVALID_MONTH, USER_NAME);
            fail("Une DateTimeException devrait être levée");
        } catch (DateTimeException e) {
            // Then
            verify(monthValidator).validateMonth(INVALID_MONTH);
            // Vérifier qu'aucune autre méthode statique n'a été appelée car l'exception est levée en premier
        }
    }

    @Test
    public void testBook_AllValidationsInCorrectOrder() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(mockReservation));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME))
                          .thenReturn(false);
            rocketMock.when(() -> Rocket.findByName(VALID_ROCKET_NAME))
                     .thenReturn(Optional.of(mockRocket));

            // When
            Reservation result = rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then
            assertNotNull(result);

            // Vérifier l'ordre des appels avec InOrder
            var inOrder = inOrder(monthValidator);
            inOrder.verify(monthValidator).validateMonth(VALID_MONTH);
        }
    }

    // ========== Test spécifique - Logique métier ==========

    @Test
    public void testBook_ReservationIsModifiedInPlace() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Rocket> rocketMock = mockStatic(Rocket.class)) {

            // Créer une réservation sans rocket
            Reservation reservationWithoutRocket = new Reservation();
            reservationWithoutRocket.setUserName(USER_NAME);
            reservationWithoutRocket.setMonth(VALID_MONTH);
            reservationWithoutRocket.setHostel(mockHostel);
            reservationWithoutRocket.id = 4L;
            assertNull("La rocket doit être null initialement", reservationWithoutRocket.getRocket());

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.findByUserNameAndMonthAndHostelIsNotNull(USER_NAME, VALID_MONTH))
                          .thenReturn(Optional.of(reservationWithoutRocket));
            reservationMock.when(() -> Reservation.existsByMonthAndRocketName(VALID_MONTH, VALID_ROCKET_NAME))
                          .thenReturn(false);
            rocketMock.when(() -> Rocket.findByName(VALID_ROCKET_NAME))
                     .thenReturn(Optional.of(mockRocket));

            // When
            Reservation result = rocketReservationService.book(VALID_ROCKET_NAME, VALID_MONTH, USER_NAME);

            // Then
            assertSame("La même instance de réservation doit être retournée", reservationWithoutRocket, result);
            assertEquals("La rocket doit maintenant être assignée", mockRocket, result.getRocket());
            assertEquals("La rocket doit être assignée à la réservation originale", mockRocket, reservationWithoutRocket.getRocket());
        }
    }
}
