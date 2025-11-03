package paterben.privatedining.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestUtils.class)
@ActiveProfiles("test")
// Integration tests for admin APIs.
// Requires a running MongoDB instance using `docker compose up -d` from the
// root directory.
public class AdminIT {
    @Autowired
    private RestaurantRepository restaurantRepository;

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
    @DisplayName("Deleting all data works multiple times when there is no data")
    void testDeleteAllDataMultipleTimesWorks() {
        // Call deleteAllData API.
        MvcTestResult deleteResult = utils.deleteAllData();
        assertThat(deleteResult).hasStatusOk();

        // Call deleteAllData API again.
        MvcTestResult deleteResult2 = utils.deleteAllData();
        assertThat(deleteResult2).hasStatusOk();
    }

    @Test
    @DisplayName("Creating a bunch of data then deleting all data then listing data returns empty")
    void testCreateDataThenDeleteAllDataThenListDataReturnsEmpty()
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
        Instant now = Instant.now();
        ApiReservation apiReservation = new ApiReservation(dinerId, "reservation4", 3,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(2, ChronoUnit.HOURS));
        utils.createReservationForRestaurantAndTableAndGetResult(restaurantId, tableId, apiReservation);

        // Call deleteAllData API.
        MvcTestResult deleteResult = utils.deleteAllData();
        assertThat(deleteResult).hasStatusOk();

        // Call list restaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();
        assertThat(restaurants).isEmpty();

        // Call list diners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();
        assertThat(diners).isEmpty();

        // Call list tables API.
        MvcTestResult listTablesResult = utils.listTablesForRestaurant(restaurantId);
        assertThat(listTablesResult).hasStatus(HttpStatus.NOT_FOUND);

        // Call list reservations for diner API.
        MvcTestResult listReservationsForDinerResult = utils.listReservationsForDiner(dinerId);
        assertThat(listReservationsForDinerResult).hasStatus(HttpStatus.NOT_FOUND);

        // Call list reservations for restaurant and table API.
        MvcTestResult listReservationsForRestaurantAndTableResult = utils
                .listReservationsForRestaurantAndTable(restaurantId, tableId);
        assertThat(listReservationsForRestaurantAndTableResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Setting up sample data followed by deleting all data results in nothing left in database")
    void testSetupSampleDataThenDeleteAllDataNoDataInDatabase()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call setupSampleData API.
        MvcTestResult setupResult = utils.setupSampleData();
        assertThat(setupResult).hasStatusOk();

        // Call deleteAllData API.
        MvcTestResult deleteResult = utils.deleteAllData();
        assertThat(deleteResult).hasStatusOk();

        // Check that there is no data in the DB.
        assertThat(restaurantRepository.findAll()).isEmpty();
        assertThat(restaurantTablesRepository.findAll()).isEmpty();
        assertThat(tableReservationsRepository.findAll()).isEmpty();
        assertThat(dinerRepository.findAll()).isEmpty();
        assertThat(dinerReservationsRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Setting up sample data then listing data returns data")
    void testSetupSampleDataThenListDataWorks() throws JsonProcessingException, UnsupportedEncodingException {
        // Call setupSampleData API.
        MvcTestResult setupResult = utils.setupSampleData();
        assertThat(setupResult).hasStatusOk();

        // Call list restaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();
        assertThat(restaurants).hasSizeGreaterThanOrEqualTo(1);
        String restaurantId = restaurants.get(0).getId();

        // Call list diners API.
        List<ApiDiner> diners = utils.listDinersAndGetResult();
        assertThat(diners).hasSizeGreaterThanOrEqualTo(1);
        String dinerId = diners.get(0).getId();

        // Call list tables API.
        List<ApiTable> listTablesResult = utils.listTablesForRestaurantAndGetResult(restaurantId);
        assertThat(listTablesResult).hasSizeGreaterThanOrEqualTo(1);
        String tableId = listTablesResult.get(0).getId();

        // Call list reservations for diner API.
        List<ApiReservation> listReservationsForDinerResult = utils.listReservationsForDinerAndGetResult(dinerId);
        assertThat(listReservationsForDinerResult).hasSizeGreaterThanOrEqualTo(1);

        // Call list reservations for restaurant and table API.
        List<ApiReservation> listReservationsForRestaurantAndTableResult = utils
                .listReservationsForRestaurantAndTableAndGetResult(restaurantId, tableId);
        assertThat(listReservationsForRestaurantAndTableResult).hasSizeGreaterThanOrEqualTo(1);
    }
}
