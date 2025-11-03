package paterben.privatedining.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;

import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.api.model.ApiReservation;
import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestUtils.class)
@ActiveProfiles("test")
// Integration tests for reservation creation, update and retrieval.
// Requires a running MongoDB instance using `docker compose up -d` from the
// root directory.
public class ReservationIT {
    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Autowired
    private TableReservationsRepository tableReservationsRepository;

    @Autowired
    private DinerRepository dinerRepository;

    @Autowired
    private DinerReservationsRepository dinerReservationsRepository;

    @Autowired
    private IntegrationTestUtils utils;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        restaurantTablesRepository.deleteAll();
        tableReservationsRepository.deleteAll();
        dinerRepository.deleteAll();
        dinerReservationsRepository.deleteAll();
    }

    @Test
    @DisplayName("Listing reservations for restaurant and table when restaurant doesn't exist returns NOT_FOUND")
    void testListReservationsForRestaurantAndTableNonExistentRestaurant()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call list reservations API.
        MvcTestResult listResult = utils.listReservationsForRestaurantAndTable("1234", "2345");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Listing reservations for restaurant and table when table doesn't exist returns NOT_FOUND")
    void testListReservationsForRestaurantAndTableNonExistentTable()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call list reservations API.
        MvcTestResult listResult = utils.listReservationsForRestaurantAndTable(restaurantId, "2345");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Listing reservations for restaurant and table when table exists but in a different restaurant returns NOT_FOUND")
    void testListReservationsForRestaurantAndTableRestaurantMismatch()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call list reservations API.
        MvcTestResult listResult = utils.listReservationsForRestaurantAndTable("1234", tableId);

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Listing reservations for restaurant and table when table doesn't have any reservations returns empty")
    void testListReservationsForRestaurantAndTableEmpty() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call list reservations API.
        List<ApiReservation> listResult = utils.listReservationsForRestaurantAndTableAndGetResult(restaurantId,
                tableId);

        // Check that returned list is empty.
        assertThat(listResult).isEmpty();
    }

    @Test
    @DisplayName("Listing reservations for diner when diner doesn't exist returns NOT_FOUND")
    void testListReservationsForDinerNonExistentDiner() throws JsonProcessingException, UnsupportedEncodingException {
        // Call list reservations API.
        MvcTestResult listResult = utils.listReservationsForDiner("1234");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Listing reservations for diner when diner doesn't have any reservations returns empty")
    void testListReservationsForDinerEmpty() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call list reservations API.
        List<ApiReservation> listResult = utils.listReservationsForDinerAndGetResult(dinerId);

        // Check that returned list is empty.
        assertThat(listResult).isEmpty();
    }

    @Test
    @DisplayName("Getting reservation for diner when diner doesn't exist returns NOT_FOUND")
    void testGetReservationForDinerNonExistentDiner() throws JsonProcessingException, UnsupportedEncodingException {
        // Call get reservation API.
        MvcTestResult listResult = utils.getReservationForDiner("1234", "2345");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting reservation for diner when reservation doesn't exist returns NOT_FOUND")
    void testGetReservationForDinerNonExistentReservation()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call get reservation API.
        MvcTestResult listResult = utils.getReservationForDiner(dinerId, "2345");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting reservation for restaurant and table when restaurant doesn't exist returns NOT_FOUND")
    void testGetReservationForRestaurantAndTableNonExistentRestaurant()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call get reservation API.
        MvcTestResult getResult = utils.getReservationForRestaurantAndTable("1234", "2345", "3456");

        // Check that request fails.
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting reservation for restaurant and table when table doesn't exist returns NOT_FOUND")
    void testGetReservationForRestaurantAndTableNonExistentTable()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call get reservation API.
        MvcTestResult getResult = utils.getReservationForRestaurantAndTable(restaurantId, "2345", "3456");

        // Check that request fails.
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting reservation for restaurant and table when table exists but in a different restaurant returns NOT_FOUND")
    void testGetReservationForRestaurantAndTableRestaurantMismatch()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API with 2 different restaurants.
        ApiRestaurant apiRestaurant1 = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant apiRestaurant2 = new ApiRestaurant("Restaurant2", "Address2", "email2", "EUR");
        ApiRestaurant newRestaurant1 = utils.createRestaurantAndGetResult(apiRestaurant1);
        ApiRestaurant newRestaurant2 = utils.createRestaurantAndGetResult(apiRestaurant2);
        String restaurantId1 = newRestaurant1.getId();
        String restaurantId2 = newRestaurant2.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId1, apiTable);
        String tableId = newTable.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId1, tableId,
                apiReservation);
        String reservationId = newReservation.getId();

        // Call get reservation API with other restaurant ID.
        MvcTestResult getResult = utils.getReservationForRestaurantAndTable(restaurantId2, tableId, reservationId);

        // Check that request fails.
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting reservation for restaurant and table when reservation exists but in a different table returns NOT_FOUND")
    void testGetReservationForRestaurantAndTableTableMismatch()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API with 2 different tables.
        ApiTable apiTable1 = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable apiTable2 = new ApiTable("table2", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable1 = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable1);
        ApiTable newTable2 = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable2);
        String tableId1 = newTable1.getId();
        String tableId2 = newTable2.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId1,
                apiReservation);
        String reservationId = newReservation.getId();

        // Call get reservation API with other table ID.
        MvcTestResult getResult = utils.getReservationForRestaurantAndTable(restaurantId, tableId2, reservationId);

        // Check that request fails.
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Reservation creation works and creates reservation in database")
    void testCreateReservationWorksAndCreatesReservationInDatabase()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId,
                apiReservation);

        // Check that returned reservation matches request.
        String reservationId = newReservation.getId();
        assertThat(reservationId).isNotBlank();
        Instant createdAt = newReservation.getCreatedAt();
        assertThat(createdAt).isNotNull();
        apiReservation.setId(reservationId);
        apiReservation.setRestaurantId(restaurantId);
        apiReservation.setTableId(tableId);
        apiReservation.setCreatedAt(createdAt);
        assertEquals(apiReservation, newReservation);

        // Check that reservation was added to tableReservations in DB.
        Optional<TableReservations> foundTableReservations = tableReservationsRepository.findById(tableId);
        assertThat(foundTableReservations).isPresent();
        assertThat(foundTableReservations.get().getReservations()).satisfiesExactly(
                r -> assertAll(
                        () -> assertEquals(reservationId, r.getId()),
                        () -> assertEquals(newReservation.getName(), r.getName())));

        // Check that reservation was added to dinerReservations in DB.
        Optional<DinerReservations> foundDinerReservations = dinerReservationsRepository.findById(dinerId);
        assertThat(foundDinerReservations).isPresent();
        assertThat(foundDinerReservations.get().getReservations()).satisfiesExactly(
                r -> assertAll(
                        () -> assertEquals(reservationId, r.getId()),
                        () -> assertEquals(newReservation.getName(), r.getName())));
    }

    @Test
    @DisplayName("Reservation creation followed by get for the same reservation (from both restaurant+table and diner APIs) returns reservation")
    void testCreateAndGetReservation() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId,
                apiReservation);
        String reservationId = newReservation.getId();

        // Call get reservation for restaurant and table API.
        ApiReservation getReservationForRestaurantAndTable = utils
                .getReservationForRestaurantAndTableAndGetResult(restaurantId, tableId, reservationId);

        // Check that returned reservation matches created one.
        assertEquals(newReservation, getReservationForRestaurantAndTable);

        // Call get reservation for diner API.
        ApiReservation getReservationForDiner = utils.getReservationForDinerAndGetResult(dinerId, reservationId);

        // Check that returned reservation matches created one.
        assertEquals(newReservation, getReservationForDiner);
    }

    @Test
    @DisplayName("Multiple reservation creation (different tables, same diner) followed by list reservations (from both restaurant+table and diner APIs) returns correct reservations")
    void testCreateAndListMultipleReservationsDifferentTablesSameDiner()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API twice.
        ApiTable apiTable1 = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable apiTable2 = new ApiTable("table2", 0, 3, RoomType.HALL, 0);
        ApiTable newTable1 = utils.addTableToRestaurantAndGetResult(restaurantId,
                apiTable1);
        ApiTable newTable2 = utils.addTableToRestaurantAndGetResult(restaurantId,
                apiTable2);
        String tableId1 = newTable1.getId();
        String tableId2 = newTable2.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API twice (different tables, same diner).
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation1 = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation1 = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId,
                tableId1,
                apiReservation1);
        ApiReservation apiReservation2 = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation2 = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId,
                tableId2, apiReservation2);

        // Call list reservations for restaurant and table API (first table).
        List<ApiReservation> tableReservations1 = utils.listReservationsForRestaurantAndTableAndGetResult(restaurantId,
                tableId1);

        // Check that returned list matches only first reservation.
        assertThat(tableReservations1).satisfiesExactly(
                r -> assertEquals(newReservation1, r));

        // Call list reservations for restaurant and table API (second table).
        List<ApiReservation> tableReservations2 = utils.listReservationsForRestaurantAndTableAndGetResult(restaurantId,
                tableId2);

        // Check that returned list matches only second reservation.
        assertThat(tableReservations2).satisfiesExactly(
                r -> assertEquals(newReservation2, r));

        // Call list reservations for diner API.
        List<ApiReservation> dinerReservations = utils.listReservationsForDinerAndGetResult(dinerId);

        // Check that returned list matches both reservations.
        assertThat(dinerReservations).satisfiesExactly(
                r -> assertEquals(newReservation1, r),
                r -> assertEquals(newReservation2, r));
    }

    @Test
    @DisplayName("Reservation creation without diner ID fails")
    void testCreateReservationWithoutDinerIdFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create reservation API without diner ID.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation("", "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        MvcTestResult createReservationResult = utils.createReservationForRestaurantAndTable(restaurantId, tableId,
                apiReservation);

        // Check that request fails.
        assertThat(createReservationResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createReservationResult).bodyText().contains("`dinerId` is required");

        // Check that no reservations exist by calling list reservations for restaurant
        // and table API.
        List<ApiReservation> reservationsForRestaurantAndTable = utils
                .listReservationsForRestaurantAndTableAndGetResult(restaurantId, tableId);
        assertThat(reservationsForRestaurantAndTable).isEmpty();
    }

    @Test
    @DisplayName("Reservation creation with schedule conflict fails")
    void testCreateReservationWithScheduleConflictFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create diner API twice.
        ApiDiner apiDiner1 = new ApiDiner("diner1", "email1");
        ApiDiner newDiner1 = utils.createDinerAndGetResult(apiDiner1);
        ApiDiner apiDiner2 = new ApiDiner("diner2", "email2");
        ApiDiner newDiner2 = utils.createDinerAndGetResult(apiDiner2);
        String dinerId1 = newDiner1.getId();
        String dinerId2 = newDiner2.getId();

        // Call create reservation API twice (different diners, schedule conflict).
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation1 = new ApiReservation(dinerId1, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        utils.createReservationForRestaurantAndTableAndGetResult(restaurantId,
                tableId,
                apiReservation1);
        ApiReservation apiReservation2 = new ApiReservation(dinerId2, "reservation1", 3,
                now.plus(30, ChronoUnit.MINUTES),
                now.plus(90, ChronoUnit.MINUTES));
        MvcTestResult createReservationResult2 = utils.createReservationForRestaurantAndTable(restaurantId,
                tableId, apiReservation2);

        // Check that second request fails.
        assertThat(createReservationResult2).hasStatus(HttpStatus.CONFLICT);
        assertThat(createReservationResult2).bodyText().contains("Reservation to create conflicts with reservation");

        // Check that second reservation wasn't created calling list reservations for
        // diner API.
        List<ApiReservation> reservationsForDiner = utils.listReservationsForDinerAndGetResult(dinerId2);
        assertThat(reservationsForDiner).isEmpty();
    }

    @Test
    @DisplayName("Reservation creation followed by cancellation followed by get returns cancelled reservation")
    void testCreateThenCancelThenGetReservation() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId,
                apiReservation);
        String reservationId = newReservation.getId();

        // Cancel reservation.
        ApiReservation apiCancelReservation = new ApiReservation("ignored", "ignored", "ignored", "ignored", "ignored",
                0, null, null, null);
        apiCancelReservation.setIsCancelled(true);
        ApiReservation updatedReservation = utils.updateReservationForRestaurantAndTableAndGetResult(restaurantId,
                tableId, reservationId, apiCancelReservation);

        // Check that cancelled reservation matches original one except cancelled.
        assertThat(updatedReservation.getCancelledAt()).isNotNull();
        ApiReservation expectedReservation = new ApiReservation(reservationId, restaurantId, tableId, dinerId,
                "reservation1", 3, now.plus(1, ChronoUnit.HOURS), now.plus(2, ChronoUnit.HOURS),
                newReservation.getCreatedAt());
        expectedReservation.setIsCancelled(true);
        expectedReservation.setCancelledAt(updatedReservation.getCancelledAt());
        assertEquals(expectedReservation, updatedReservation);

        // Call get reservation for restaurant and table API.
        ApiReservation getReservationForRestaurantAndTable = utils
                .getReservationForRestaurantAndTableAndGetResult(restaurantId, tableId, reservationId);

        // Check that returned reservation matches cancelled one.
        assertEquals(updatedReservation, getReservationForRestaurantAndTable);

        // Call get reservation for diner API.
        ApiReservation getReservationForDiner = utils.getReservationForDinerAndGetResult(dinerId, reservationId);

        // Check that returned reservation matches cancelled one.
        assertEquals(updatedReservation, getReservationForDiner);
    }

    @Test
    @DisplayName("Reservation creation followed by cancellation followed by another cancellation fails")
    void testCancelAlreadyCancelledReservationFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);
        String tableId = newTable.getId();

        // Call create diner API.
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        ApiDiner newDiner = utils.createDinerAndGetResult(apiDiner);
        String dinerId = newDiner.getId();

        // Call create reservation API.
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation1", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        ApiReservation newReservation = utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId,
                apiReservation);
        String reservationId = newReservation.getId();

        // Cancel reservation.
        ApiReservation apiCancelReservation = new ApiReservation("ignored", "ignored", "ignored", "ignored", "ignored",
                0, null, null, null);
        apiCancelReservation.setIsCancelled(true);
        ApiReservation updatedReservation = utils.updateReservationForRestaurantAndTableAndGetResult(restaurantId,
                tableId, reservationId, apiCancelReservation);

        // Cancel reservation again.
        MvcTestResult cancelReservationResult2 = utils.updateReservationForRestaurantAndTable(restaurantId,
                tableId, reservationId, apiCancelReservation);

        // Check that second request fails.
        assertThat(cancelReservationResult2).hasStatus(HttpStatus.PRECONDITION_FAILED);
        assertThat(cancelReservationResult2).bodyText().contains("is already cancelled");

        // Call get reservation for restaurant and table API.
        ApiReservation getReservationForRestaurantAndTable = utils
                .getReservationForRestaurantAndTableAndGetResult(restaurantId, tableId, reservationId);

        // Check that returned reservation matches cancelled one.
        assertEquals(updatedReservation, getReservationForRestaurantAndTable);

        // Call get reservation for diner API.
        ApiReservation getReservationForDiner = utils.getReservationForDinerAndGetResult(dinerId, reservationId);

        // Check that returned reservation matches cancelled one.
        assertEquals(updatedReservation, getReservationForDiner);
    }
}
