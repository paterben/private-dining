package paterben.privatedining.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
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

import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestUtils.class)
@ActiveProfiles("test")
// Integration tests for table creation and retrieval.
// Requires a running MongoDB instance using `docker compose up -d` from the
// root directory.
public class TableIT {
    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Autowired
    private TableReservationsRepository tableReservationsRepository;

    @Autowired
    private IntegrationTestUtils utils;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        restaurantTablesRepository.deleteAll();
        tableReservationsRepository.deleteAll();
    }

    @Test
    @DisplayName("Listing tables when restaurant doesn't exist returns NOT_FOUND")
    void testListTablesNonExistentRestaurant() throws JsonProcessingException, UnsupportedEncodingException {
        // Call list tables API.
        MvcTestResult listResult = utils.listTablesForRestaurant("1234");

        // Check that request fails.
        assertThat(listResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Listing tables when restaurant doesn't have any returns empty")
    void testListTablesEmpty() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call list tables API.
        List<ApiTable> tables = utils.listTablesForRestaurantAndGetResult(restaurantId);

        // Check that returned list is empty.
        assertThat(tables).isEmpty();
    }

    @Test
    @DisplayName("Getting a table that doesn't exist returns NOT_FOUND")
    void testGetNonExistentTable() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call get table API.
        MvcTestResult getResult = utils.getTableForRestaurant(restaurantId, "1234");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Getting a table when restaurant doesn't exist returns NOT_FOUND")
    void testGetTableNonExistentRestaurant() throws JsonProcessingException, UnsupportedEncodingException {
        // Call get table API.
        MvcTestResult getResult = utils.getTableForRestaurant("1234", "2345");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Table creation works and creates table in database")
    void testCreateTableWorksAndCreatesTableInDatabase() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);

        // Check that returned table matches request.
        String tableId = newTable.getId();
        assertThat(tableId).isNotBlank();
        apiTable.setId(tableId);
        assertEquals(apiTable, newTable);

        // Check that table was added to restaurantTables in DB.
        Optional<RestaurantTables> foundRestaurantTables = restaurantTablesRepository.findById(restaurantId);
        assertThat(foundRestaurantTables).isPresent();
        assertThat(foundRestaurantTables.get().getTables()).satisfiesExactly(
                t -> assertAll(
                        () -> assertEquals(tableId, t.getId()),
                        () -> assertEquals(newTable.getName(), t.getName())));

        // Check that tableReservations was created in DB.
        Optional<TableReservations> foundTableReservations = tableReservationsRepository.findById(tableId);
        assertThat(foundTableReservations).isPresent();
        assertEquals(tableId, foundTableReservations.get().getId());
        assertThat(foundTableReservations.get().getReservations()).isEmpty();
    }

    @Test
    @DisplayName("Table creation followed by get for the same table")
    void testCreateAndGetTable() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable newTable = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable);

        // Call get table API.
        ApiTable getTable = utils.getTableForRestaurantAndGetResult(restaurantId, newTable.getId());

        // Check that returned table matches created one.
        assertEquals(newTable, getTable);
    }

    @Test
    @DisplayName("Multiple table creation followed by list tables returns all tables")
    void testCreateAndListMultipleTables() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API twice.
        ApiTable apiTable1 = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable apiTable2 = new ApiTable("table2", 0, 3, RoomType.HALL, 0);
        ApiTable newTable1 = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable1);
        ApiTable newTable2 = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable2);

        // Call list tables API.
        List<ApiTable> tables = utils.listTablesForRestaurantAndGetResult(restaurantId);

        // Check that returned list matches.
        assertThat(tables).satisfiesExactly(
                t -> assertEquals(newTable1, t),
                t -> assertEquals(newTable2, t));
    }

    @Test
    @DisplayName("Creating multiple tables with same name fails")
    void testCreateMultipleTablesWithSameNameFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API twice with same name.
        ApiTable apiTable1 = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        ApiTable apiTable2 = new ApiTable("table1", 0, 3, RoomType.HALL, 0);
        ApiTable newTable1 = utils.addTableToRestaurantAndGetResult(restaurantId, apiTable1);
        MvcTestResult addResult2 = utils.addTableToRestaurant(restaurantId, apiTable2);

        // Check that second request fails.
        assertThat(addResult2).hasStatus(HttpStatus.CONFLICT);
        assertThat(addResult2).bodyText().contains("already exists");

        // Check that only first table exists by calling listTables API.
        List<ApiTable> tables = utils.listTablesForRestaurantAndGetResult(restaurantId);
        assertThat(tables).satisfiesExactly(t -> assertEquals(newTable1, t));
    }

    @Test
    @DisplayName("Table creation without a name fails")
    void testCreateTableWithoutNameFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);
        String restaurantId = newRestaurant.getId();

        // Call create table API without name.
        ApiTable apiTable = new ApiTable("", 1, 3, RoomType.HALL, 1.5);
        MvcTestResult addResult = utils.addTableToRestaurant(restaurantId, apiTable);

        // Check that request fails.
        assertThat(addResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(addResult).bodyText().contains("`name` is required");

        // Check that no tables exist by calling listTables API.
        List<ApiTable> tables = utils.listTablesForRestaurantAndGetResult(restaurantId);
        assertThat(tables).isEmpty();
    }

    @Test
    @DisplayName("Table creation for a non-existent restaurant fails")
    void testCreateTableNonExistentRestaurantFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create table API without name.
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        MvcTestResult addResult = utils.addTableToRestaurant("1234", apiTable);

        // Check that request fails.
        assertThat(addResult).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(addResult).bodyText().contains("not found");
    }
}
