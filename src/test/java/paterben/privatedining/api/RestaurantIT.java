package paterben.privatedining.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
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
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestUtils.class)
@ActiveProfiles("test")
// Integration tests for restaurant creation and retrieval.
// Requires a running MongoDB instance using `docker compose up -d` from the
// root directory.
public class RestaurantIT {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Autowired
    private IntegrationTestUtils utils;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        restaurantTablesRepository.deleteAll();
    }

    @Test
    @DisplayName("Listing restaurants when none exist returns empty")
    void testListRestaurantsEmpty() throws JsonProcessingException, UnsupportedEncodingException {
        // Call list restaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();

        // Check that returned list is empty.
        assertThat(restaurants).isEmpty();
    }

    @Test
    @DisplayName("Getting a restaurant that doesn't exist returns NOT_FOUND")
    void testGetNonExistentRestaurant() {
        // Call get restaurant API.
        MvcTestResult getResult = utils.getRestaurant("1234");

        // Check that request fails.
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Restaurant creation works and creates restaurant in database")
    void testCreateRestaurantWorksAndCreatesRestaurantInDatabase()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);

        // Check that returned restaurant matches request.
        String restaurantId = newRestaurant.getId();
        assertThat(restaurantId).isNotBlank();
        Instant createdAt = newRestaurant.getCreatedAt();
        assertThat(createdAt).isNotNull();
        apiRestaurant.setId(restaurantId);
        apiRestaurant.setCreatedAt(createdAt);
        assertEquals(apiRestaurant, newRestaurant);

        // Check that restaurant was created in DB.
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(restaurantId);
        assertThat(foundRestaurant).isPresent();
        assertEquals(restaurantId, foundRestaurant.get().getId());
        assertEquals(createdAt, foundRestaurant.get().getCreatedAt());
        assertEquals(newRestaurant.getName(), foundRestaurant.get().getName());
        assertEquals(newRestaurant.getEmail(), foundRestaurant.get().getEmail());

        // Check that restaurantTables was created in DB.
        Optional<RestaurantTables> foundRestaurantTables = restaurantTablesRepository.findById(restaurantId);
        assertThat(foundRestaurantTables).isPresent();
        assertEquals(restaurantId, foundRestaurantTables.get().getId());
        assertThat(foundRestaurantTables.get().getTables()).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation followed by get for the same restaurant")
    void testCreateAndGetRestaurant() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant newRestaurant = utils.createRestaurantAndGetResult(apiRestaurant);

        // Call get restaurant API.
        ApiRestaurant getRestaurant = utils.getRestaurantAndGetResult(newRestaurant.getId());

        // Check that returned restaurant matches created one.
        assertEquals(newRestaurant, getRestaurant);
    }

    @Test
    @DisplayName("Multiple restaurant creation followed by list restaurants returns all restaurants")
    void testCreateAndListMultipleRestaurants() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API twice.
        ApiRestaurant apiRestaurant1 = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant apiRestaurant2 = new ApiRestaurant("Restaurant2", "Address2", "email2", "USD");
        ApiRestaurant newRestaurant1 = utils.createRestaurantAndGetResult(apiRestaurant1);
        ApiRestaurant newRestaurant2 = utils.createRestaurantAndGetResult(apiRestaurant2);

        // Call list restaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();

        // Check that returned list matches.
        assertThat(restaurants).satisfiesExactly(
                r -> assertEquals(newRestaurant1, r),
                r -> assertEquals(newRestaurant2, r));
    }

    @Test
    @DisplayName("Creating multiple restaurants with same email fails")
    void testCreateMultipleRestaurantsWithSameEmailFails()
            throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API twice with same email.
        ApiRestaurant apiRestaurant1 = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        ApiRestaurant apiRestaurant2 = new ApiRestaurant("Restaurant2", "Address2", "email1", "USD");
        ApiRestaurant newRestaurant1 = utils.createRestaurantAndGetResult(apiRestaurant1);
        MvcTestResult createResult2 = utils.createRestaurant(apiRestaurant2);

        // Check that second request fails.
        assertThat(createResult2).hasStatus(HttpStatus.CONFLICT);
        assertThat(createResult2).bodyText().contains("already exists");

        // Check that only first restaurant exists by calling listRestaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();
        assertThat(restaurants).satisfiesExactly(r -> assertEquals(newRestaurant1, r));
    }

    @Test
    @DisplayName("Restaurant creation without a name fails")
    void testCreateRestaurantWithoutNameFails() throws JsonProcessingException, UnsupportedEncodingException {
        // Call create restaurant API without name.
        ApiRestaurant apiRestaurant = new ApiRestaurant("", "Address1", "email1", "EUR");
        MvcTestResult createResult = utils.createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`name` is required");

        // Check that no restaurants exist by calling listRestaurants API.
        List<ApiRestaurant> restaurants = utils.listRestaurantsAndGetResult();
        assertThat(restaurants).isEmpty();
    }
}
