package com.github.jtama.app.hostels;

import com.github.jtama.app.exception.UnavailableException;
import com.github.jtama.app.exception.UnknownEntityException;
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
public class HostelReservationServiceTest {

    @Mock
    private MonthValidator monthValidator;

    @InjectMocks
    private HostelReservationService hostelReservationService;

    private static final String VALID_HOSTEL_NAME = "test-hostel";
    private static final String INVALID_HOSTEL_NAME = "nonexistent-hostel";
    private static final String USER_NAME = "testuser";
    private static final int VALID_MONTH = 6;
    private static final int INVALID_MONTH = 13;

    private Hostel mockHostel;

    @Before
    public void setUp() {
        mockHostel = new Hostel();
        mockHostel.setName(VALID_HOSTEL_NAME);
        // Simuler un ID pour le mock hostel
        mockHostel.id = 1L;
    }

    // ========== Tests de succès ==========

    @Test
    public void testBook_Success() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            // Configuration des mocks
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(VALID_HOSTEL_NAME))
                     .thenReturn(Optional.of(mockHostel));

            // When
            Reservation result = hostelReservationService.book(VALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);

            // Then
            assertNotNull("La réservation ne doit pas être null", result);
            assertEquals("Le nom d'utilisateur doit correspondre", USER_NAME, result.getUserName());
            assertEquals("Le mois doit correspondre", VALID_MONTH, result.getMonth());
            assertEquals("L'hostel doit correspondre", mockHostel, result.getHostel());

            // Vérification des appels
            verify(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.verify(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME));
            hostelMock.verify(() -> Hostel.findByName(VALID_HOSTEL_NAME));
        }
    }

    // ========== Tests d'erreur - Validation du mois ==========

    @Test(expected = DateTimeException.class)
    public void testBook_InvalidMonth_ThrowsDateTimeException() {
        // Given
        doThrow(new DateTimeException("Invalid month")).when(monthValidator).validateMonth(INVALID_MONTH);

        // When
        hostelReservationService.book(VALID_HOSTEL_NAME, INVALID_MONTH, USER_NAME);

        // Then - Exception attendue
    }

    @Test
    public void testBook_InvalidMonth_ValidatorCalled() {
        // Given
        doThrow(new DateTimeException("Invalid month")).when(monthValidator).validateMonth(INVALID_MONTH);

        // When
        try {
            hostelReservationService.book(VALID_HOSTEL_NAME, INVALID_MONTH, USER_NAME);
            fail("Une DateTimeException devrait être levée");
        } catch (DateTimeException e) {
            // Then
            verify(monthValidator).validateMonth(INVALID_MONTH);
            assertEquals("Invalid month", e.getMessage());
        }
    }

    // ========== Tests d'erreur - Réservation déjà existante ==========

    @Test(expected = UnavailableException.class)
    public void testBook_AlreadyBooked_ThrowsUnavailableException() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME))
                          .thenReturn(true);

            // When
            hostelReservationService.book(VALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);

            // Then - Exception attendue
        }
    }

    @Test
    public void testBook_AlreadyBooked_CorrectExceptionMessage() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class)) {
            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME))
                          .thenReturn(true);

            // When
            try {
                hostelReservationService.book(VALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);
                fail("Une UnavailableException devrait être levée");
            } catch (UnavailableException e) {
                // Then
                String expectedMessage = "Hostel %s is already booked for month %s".formatted(VALID_HOSTEL_NAME, VALID_MONTH);
                assertEquals(expectedMessage, e.getMessage());

                // Vérification que la validation du mois a été appelée
                verify(monthValidator).validateMonth(VALID_MONTH);
                reservationMock.verify(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME));
            }
        }
    }

    // ========== Tests d'erreur - Hostel inexistant ==========

    @Test(expected = UnknownEntityException.class)
    public void testBook_HostelNotFound_ThrowsUnknownEntityException() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, INVALID_HOSTEL_NAME))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(INVALID_HOSTEL_NAME))
                     .thenReturn(Optional.empty());

            // When
            hostelReservationService.book(INVALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);

            // Then - Exception attendue
        }
    }

    @Test
    public void testBook_HostelNotFound_CorrectExceptionMessage() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, INVALID_HOSTEL_NAME))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(INVALID_HOSTEL_NAME))
                     .thenReturn(Optional.empty());

            // When
            try {
                hostelReservationService.book(INVALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);
                fail("Une UnknownEntityException devrait être levée");
            } catch (UnknownEntityException e) {
                // Then
                String expectedMessage = "The hostel %s doesn't exist".formatted(INVALID_HOSTEL_NAME);
                assertEquals(expectedMessage, e.getMessage());

                // Vérification des appels
                verify(monthValidator).validateMonth(VALID_MONTH);
                reservationMock.verify(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, INVALID_HOSTEL_NAME));
                hostelMock.verify(() -> Hostel.findByName(INVALID_HOSTEL_NAME));
            }
        }
    }

    // ========== Tests de cas limites ==========

    @Test
    public void testBook_WithNullUserName_Success() {
        // Given
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(null, VALID_MONTH, VALID_HOSTEL_NAME))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(VALID_HOSTEL_NAME))
                     .thenReturn(Optional.of(mockHostel));

            // When
            Reservation result = hostelReservationService.book(VALID_HOSTEL_NAME, VALID_MONTH, null);

            // Then
            assertNotNull("La réservation ne doit pas être null", result);
            assertNull("Le nom d'utilisateur doit être null", result.getUserName());
            assertEquals("Le mois doit correspondre", VALID_MONTH, result.getMonth());
            assertEquals("L'hostel doit correspondre", mockHostel, result.getHostel());
        }
    }

    @Test
    public void testBook_WithEmptyHostelName_ThrowsUnknownEntityException() {
        // Given
        String emptyHostelName = "";
        try (MockedStatic<Reservation> reservationMock = mockStatic(Reservation.class);
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, emptyHostelName))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(emptyHostelName))
                     .thenReturn(Optional.empty());

            // When
            try {
                hostelReservationService.book(emptyHostelName, VALID_MONTH, USER_NAME);
                fail("Une UnknownEntityException devrait être levée");
            } catch (UnknownEntityException e) {
                // Then
                String expectedMessage = "The hostel %s doesn't exist".formatted(emptyHostelName);
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
            hostelReservationService.book(VALID_HOSTEL_NAME, INVALID_MONTH, USER_NAME);
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
             MockedStatic<Hostel> hostelMock = mockStatic(Hostel.class)) {

            doNothing().when(monthValidator).validateMonth(VALID_MONTH);
            reservationMock.when(() -> Reservation.existsByUserNameAndMonthAndHostelName(USER_NAME, VALID_MONTH, VALID_HOSTEL_NAME))
                          .thenReturn(false);
            hostelMock.when(() -> Hostel.findByName(VALID_HOSTEL_NAME))
                     .thenReturn(Optional.of(mockHostel));

            // When
            Reservation result = hostelReservationService.book(VALID_HOSTEL_NAME, VALID_MONTH, USER_NAME);

            // Then
            assertNotNull(result);

            // Vérifier l'ordre des appels avec InOrder
            var inOrder = inOrder(monthValidator);
            inOrder.verify(monthValidator).validateMonth(VALID_MONTH);
        }
    }
}
