package paterben.privatedining.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;

import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.DinerReservationsRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @Mock
    private TableReservationsRepository tableReservationsRepository;

    @Mock
    private DinerReservationsRepository dinerReservationsRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        lenient().when(tableReservationsRepository.findById(any())).thenReturn(Optional.empty());
        lenient().when(dinerReservationsRepository.findById(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("When table exists, listReservationsForRestaurantAndTable returns the list")
    void testListReservationsForRestaurantAndTable() {
        // Arrange
        Reservation foundReservation1 = new Reservation("5678", "1234", "2345", "3456", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("6789", "1234", "2345", "4567", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2345", "1234", 1, 3,
                Arrays.asList(foundReservation1, foundReservation2));
        when(tableReservationsRepository.findById("2345")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Optional<List<Reservation>> result = reservationService.listReservationsForRestaurantAndTable("1234", "2345");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).satisfiesExactly(
                r -> assertEquals(foundReservation1, r),
                r -> assertEquals(foundReservation2, r));
    }

    @Test
    @DisplayName("When table exists but restaurant ID doesn't match, listReservationsForRestaurantAndTable returns empty")
    void testListReservationsForRestaurantAndTableRestaurantMismatch() {
        // Arrange
        Reservation foundReservation1 = new Reservation("5678", "9999", "2345", "3456", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("6789", "9999", "2345", "4567", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2345", "9999", 1, 3,
                Arrays.asList(foundReservation1, foundReservation2));
        when(tableReservationsRepository.findById("2345")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Optional<List<Reservation>> result = reservationService.listReservationsForRestaurantAndTable("1234", "2345");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When table doesn't exist, listReservationsForRestaurantAndTable returns empty")
    void testListReservationsForRestaurantAndTableNotFound() {
        // Act
        Optional<List<Reservation>> result = reservationService.listReservationsForRestaurantAndTable("1234", "2345");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When diner exists, listReservationsForDiner returns the list")
    void testListReservationsForDiner() {
        // Arrange
        Reservation foundReservation1 = new Reservation("6789", "1234", "2345", "5678", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("7890", "3456", "4567", "5678", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        DinerReservations foundDinerReservations = new DinerReservations("5678",
                Arrays.asList(foundReservation1, foundReservation2));
        when(dinerReservationsRepository.findById("5678")).thenReturn(Optional.of(foundDinerReservations));

        // Act
        Optional<List<Reservation>> result = reservationService.listReservationsForDiner("5678");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).satisfiesExactly(
                r -> assertEquals(foundReservation1, r),
                r -> assertEquals(foundReservation2, r));
    }

    @Test
    @DisplayName("When diner doesn't exist, listReservationsForDiner returns empty")
    void testListReservationsForDinerNotFound() {
        // Act
        Optional<List<Reservation>> result = reservationService.listReservationsForDiner("5678");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When reservation exists, getReservationForDinerById returns it")
    void testGetReservationForDinerByIdFound() {
        // Arrange
        Reservation foundReservation1 = new Reservation("6789", "1234", "2345", "5678", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("7890", "3456", "4567", "5678", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        DinerReservations foundDinerReservations = new DinerReservations("5678",
                Arrays.asList(foundReservation1, foundReservation2));
        when(dinerReservationsRepository.findById("5678")).thenReturn(Optional.of(foundDinerReservations));

        // Act
        Optional<Reservation> result = reservationService.getReservationForDinerById("5678", "7890");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(foundReservation2);
    }

    @Test
    @DisplayName("When reservation doesn't exist, getReservationForDinerById returns empty")
    void testGetReservationForDinerByIdReservationNotFound() {
        // Arrange
        Reservation foundReservation1 = new Reservation("6789", "1234", "2345", "5678", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("8901", "3456", "4567", "5678", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        DinerReservations foundDinerReservations = new DinerReservations("5678",
                Arrays.asList(foundReservation1, foundReservation2));
        when(dinerReservationsRepository.findById("5678")).thenReturn(Optional.of(foundDinerReservations));

        // Act
        Optional<Reservation> result = reservationService.getReservationForDinerById("5678", "7890");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When diner doesn't exist, getReservationForDinerById returns empty")
    void testGetReservationForDinerByIdDinerNotFound() {
        // Act
        Optional<Reservation> result = reservationService.getReservationForDinerById("5678", "7890");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When reservation exists, getReservationForRestaurantAndTableById returns it")
    void testGetReservationForRestaurantAndTableByIdFound() {
        // Arrange
        Reservation foundReservation1 = new Reservation("5678", "1234", "2345", "3456", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("6789", "1234", "2345", "4567", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2345", "1234", 1, 3,
                Arrays.asList(foundReservation1, foundReservation2));
        when(tableReservationsRepository.findById("2345")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Optional<Reservation> result = reservationService.getReservationForRestaurantAndTableById("1234", "2345",
                "6789");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(foundReservation2);
    }

    @Test
    @DisplayName("When table and reservation exist but restaurant ID doesn't match, getReservationForRestaurantAndTableById returns empty")
    void testGetReservationForRestaurantAndTableByIdRestaurantMismatch() {
        // Arrange
        Reservation foundReservation1 = new Reservation("5678", "9999", "2345", "3456", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("6789", "9999", "2345", "4567", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2345", "9999", 1, 3,
                Arrays.asList(foundReservation1, foundReservation2));
        when(tableReservationsRepository.findById("2345")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Optional<Reservation> result = reservationService.getReservationForRestaurantAndTableById("1234", "2345",
                "6789");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When reservation doesn't exist, getReservationForRestaurantAndTableById returns empty")
    void testGetReservationForRestaurantAndTableByIdReservationNotFound() {
        // Arrange
        Reservation foundReservation1 = new Reservation("5678", "1234", "2345", "3456", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundReservation2 = new Reservation("7890", "1234", "2345", "4567", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2345", "1234", 1, 3,
                Arrays.asList(foundReservation1, foundReservation2));
        when(tableReservationsRepository.findById("2345")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Optional<Reservation> result = reservationService.getReservationForRestaurantAndTableById("1234", "2345",
                "6789");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When table doesn't exist, getReservationForRestaurantAndTableById returns empty")
    void testGetReservationForRestaurantAndTableByIdTableNotFound() {
        // Act
        Optional<Reservation> result = reservationService.getReservationForRestaurantAndTableById("1234", "2345",
                "6789");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When reservation is valid and table and diner exist, createReservationForRestaurantAndTable saves to tableReservations and dinerReservations and returns new reservation")
    void testCreateReservationForRestaurantAndTable() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        Reservation foundDinerReservation1 = new Reservation("4333", "1222", "2333", "3222", "reservation3", 3,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundDinerReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        DinerReservations foundDinerReservations = new DinerReservations("3222",
                new ArrayList<Reservation>(Arrays.asList(foundDinerReservation1, foundDinerReservation2)));
        when(dinerReservationsRepository.findById("3222")).thenReturn(Optional.of(foundDinerReservations));
        when(tableReservationsRepository.save(any())).thenAnswer(makeTableReservationsAnswer());
        when(dinerReservationsRepository.save(any())).thenAnswer(makeDinerReservationsAnswer());
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33332));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        Reservation result = reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);

        // Assert
        assertThat(result.getId()).isNotBlank();
        Reservation expectedReservation = new Reservation(result.getId(), "1111", "2222", "3222", "reservation4", 3,
                Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444), Instant.ofEpochSecond(33332));
        assertThat(result).isEqualTo(expectedReservation);
        TableReservations tableReservations = new TableReservations("2222", "1111", 1, 3,
                Arrays.asList(foundTableReservation1, foundTableReservation2, expectedReservation));
        verify(tableReservationsRepository).save(ArgumentMatchers.eq(tableReservations));
        DinerReservations dinerReservations = new DinerReservations("3222",
                Arrays.asList(foundDinerReservation1, foundDinerReservation2, expectedReservation));
        verify(dinerReservationsRepository).save(ArgumentMatchers.eq(dinerReservations));
    }

    @Test
    @DisplayName("When reservations are not compatible (schedule conflict), createReservationForRestaurantAndTable fails with CONFLICT")
    void testCreateReservationForRestaurantAndTableScheduleConflict() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33331));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33332),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains("Reservation to create conflicts with reservation with ID 4222");
        }
    }

    @Test
    @DisplayName("When numGuests is more than maxCapacity, createReservationForRestaurantAndTable fails with CONFLICT")
    void testCreateReservationForRestaurantAndTableMaxCapacityExceeded() {
        // Arrange
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3);
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33331));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 4, Instant.ofEpochSecond(33332),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage())
                    .contains("Number of guests in reservation is too high (maxCapacity 3, numGuests 4)");
        }
    }

    @Test
    @DisplayName("When numGuests is less than minCapacity, createReservationForRestaurantAndTable fails with CONFLICT")
    void testCreateReservationForRestaurantAndTableMinCapacityNotRespected() {
        // Arrange
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 2, 4);
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33331));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 1, Instant.ofEpochSecond(33332),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage())
                    .contains("Number of guests in reservation is too low (minCapacity 4, numGuests 1)");
        }
    }

    @Test
    @DisplayName("When table doesn't exist, createReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCreateReservationForRestaurantAndTableTableNotFound() {
        // Arrange
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33332));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Table with ID 2222 not found");
        }
    }

    @Test
    @DisplayName("When table exists but restaurant ID doesn't match, createReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCreateReservationForRestaurantAndTableRestaurantIdMismatch() {
        // Arrange
        TableReservations foundTableReservations = new TableReservations("2222", "1222", 1, 3);
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33332));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Table with ID 2222 not found");
        }
    }

    @Test
    @DisplayName("When diner doesn't exist, createReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCreateReservationForRestaurantAndTableDinerNotFound() {
        // Arrange
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3);
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33332));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Diner with ID 3222 not found");
        }
    }

    @Test
    @DisplayName("When reservation to create has ID set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithIdFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setId("4111");
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`id` must not be set");
        }
    }

    @Test
    @DisplayName("When reservation to create has restaurant ID set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithRestaurantIdFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setRestaurantId("1111");
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`restaurantId` must not be set");
        }
    }

    @Test
    @DisplayName("When reservation to create has table ID set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithTableIdFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setTableId("2222");
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`tableId` must not be set");
        }
    }

    @Test
    @DisplayName("When reservation to create doesn't have diner ID set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithoutDinerIdFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`dinerId` is required");
        }
    }

    @Test
    @DisplayName("When reservation to create doesn't have name set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithoutNameIdFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`name` is required");
        }
    }

    @Test
    @DisplayName("When reservation to create doesn't have numGuests set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithoutNumGuestsFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 0, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`numGuests` is required");
        }
    }

    @Test
    @DisplayName("When reservation to create doesn't have reservationStart, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithoutReservationStartFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, null, Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`reservationStart` is required");
        }
    }

    @Test
    @DisplayName("When reservation to create doesn't have reservationEnd, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithoutReservationEndFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333), null);
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`reservationEnd` is required");
        }
    }

    @Test
    @DisplayName("When reservation to create has zero duration, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithZeroDurationFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(33333));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`reservationEnd` must be strictly later than `reservationStart`");
        }
    }

    @Test
    @DisplayName("When reservation to create has negative duration, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithNegativeDurationFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(44444),
                Instant.ofEpochSecond(33333));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`reservationEnd` must be strictly later than `reservationStart`");
        }
    }

    @Test
    @DisplayName("When reservation to create starts in the past, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithReservationStartInThePastFailsWithBadRequest() {
        // Arrange
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(33334));

        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`reservationStart` must not be in the past");
        }
    }

    @Test
    @DisplayName("When reservation to create is 11 hours long, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithReservationTooLongFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(72933));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage())
                    .contains("Hours between `reservationStart` and `reservationEnd` must be 10 or less");
        }
    }

    @Test
    @DisplayName("When reservation to create has isCancelled set to true, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithIsCancelledTrueFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setIsCancelled(true);
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`isCancelled` cannot be set to true");
        }
    }

    @Test
    @DisplayName("When reservation to create has createdAt set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithCreatedAtFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setCreatedAt(Instant.ofEpochSecond(12345));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`createdAt` must not be set");
        }
    }

    @Test
    @DisplayName("When reservation to create has cancelledAt set, createReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCreateReservationForRestaurantAndTableWithCancelledAtFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("3222", "reservation4", 3, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        reservation.setCancelledAt(Instant.ofEpochSecond(12345));
        try {
            reservationService.createReservationForRestaurantAndTable("1111", "2222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`cancelledAt` must not be set");
        }
    }

    @Test
    @DisplayName("When reservation exists and cancellation update occurs, updateReservationForRestaurantAndTable updates it in tableReservations and dinerReservations and returns cancelled reservation")
    void testCancelUpdateReservationForRestaurantAndTable() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        Reservation foundDinerReservation1 = new Reservation("4333", "1222", "2333", "3222", "reservation3", 3,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundDinerReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        DinerReservations foundDinerReservations = new DinerReservations("3222",
                new ArrayList<Reservation>(Arrays.asList(foundDinerReservation1, foundDinerReservation2)));
        when(dinerReservationsRepository.findById("3222")).thenReturn(Optional.of(foundDinerReservations));
        when(tableReservationsRepository.save(any())).thenAnswer(makeTableReservationsAnswer());
        when(dinerReservationsRepository.save(any())).thenAnswer(makeDinerReservationsAnswer());
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(22221));

        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0, null, null,
                null);
        reservation.setIsCancelled(true);
        Reservation result = reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222",
                reservation);

        // Assert
        Reservation expectedReservation = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222),
                Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        expectedReservation.setIsCancelled(true);
        expectedReservation.setCancelledAt(Instant.ofEpochSecond(22221));
        assertThat(result).isEqualTo(expectedReservation);
        TableReservations tableReservations = new TableReservations("2222", "1111", 1, 3,
                Arrays.asList(foundTableReservation1, expectedReservation));
        verify(tableReservationsRepository).save(ArgumentMatchers.eq(tableReservations));
        DinerReservations dinerReservations = new DinerReservations("3222",
                Arrays.asList(foundDinerReservation1, expectedReservation));
        verify(dinerReservationsRepository).save(ArgumentMatchers.eq(dinerReservations));
    }

    @Test
    @DisplayName("When reservation update is not a cancellation, updateReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testNonCancelUpdateReservationForRestaurantAndTableFailsWithBadRequest() {
        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0,
                null, null, null);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Only cancellation is supported");
        }
    }

    @Test
    @DisplayName("When table doesn't exist, updateReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCancelUpdateReservationForRestaurantAndTableTableNotFound() {
        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0,
                null, null, null);
        reservation.setIsCancelled(true);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Table with ID 2222 not found");
        }
    }

    @Test
    @DisplayName("When table exists but restaurant ID doesn't match, updateReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCancelUpdateReservationForRestaurantAndTableRestaurantIdMismatch() {
        // Arrange
        TableReservations foundTableReservations = new TableReservations("2222", "1222", 1, 3);
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0,
                null, null, null);
        reservation.setIsCancelled(true);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Table with ID 2222 not found");
        }
    }

    @Test
    @DisplayName("When reservation doesn't exist, updateReservationForRestaurantAndTable fails with NOT_FOUND")
    void testCancelUpdateReservationForRestaurantAndTableReservationNotFound() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0, null, null,
                null);
        reservation.setIsCancelled(true);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4333", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getMessage()).contains("Reservation with ID 4333 not found");
        }
    }

    @Test
    @DisplayName("When reservation is already cancelled and cancellation update occurs, updateReservationForRestaurantAndTable fails with PRECONDITION_FAILED")
    void testCancelUpdateReservationForRestaurantAndTableReservationAlreadyCancelled() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        foundTableReservation2.setIsCancelled(true);
        foundTableReservation2.setCancelledAt(Instant.ofEpochSecond(3456));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));

        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0, null, null,
                null);
        reservation.setIsCancelled(true);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
            assertThat(e.getMessage()).contains("Reservation with ID 4222 is already cancelled");
        }
    }

    @Test
    @DisplayName("When reservation has already started and cancellation update occurs, updateReservationForRestaurantAndTable fails with BAD_REQUEST")
    void testCancelUpdateReservationForRestaurantAndTableReservationAlreadyStartedFailsWithBadRequest() {
        // Arrange
        Reservation foundTableReservation1 = new Reservation("4111", "1111", "2222", "3111", "reservation1", 1,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation foundTableReservation2 = new Reservation("4222", "1111", "2222", "3222", "reservation2", 2,
                Instant.ofEpochSecond(22222), Instant.ofEpochSecond(33333), Instant.ofEpochSecond(2345));
        TableReservations foundTableReservations = new TableReservations("2222", "1111", 1, 3,
                new ArrayList<Reservation>(Arrays.asList(foundTableReservation1, foundTableReservation2)));
        when(tableReservationsRepository.findById("2222")).thenReturn(Optional.of(foundTableReservations));
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(22223));

        // Act
        Reservation reservation = new Reservation("ignored", "ignored", "ignored", "ignored", "ignored", 0, null, null,
                null);
        reservation.setIsCancelled(true);
        try {
            reservationService.updateReservationForRestaurantAndTable("1111", "2222", "4222", reservation);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("Cannot cancel a reservation that has already begun");
        }
    }

    private Answer<TableReservations> makeTableReservationsAnswer() {
        return new Answer<TableReservations>() {
            public TableReservations answer(InvocationOnMock invocation) {
                TableReservations tr = invocation.getArgument(0, TableReservations.class);
                return tr;
            }
        };
    }

    private Answer<DinerReservations> makeDinerReservationsAnswer() {
        return new Answer<DinerReservations>() {
            public DinerReservations answer(InvocationOnMock invocation) {
                DinerReservations dr = invocation.getArgument(0, DinerReservations.class);
                return dr;
            }
        };
    }
}
