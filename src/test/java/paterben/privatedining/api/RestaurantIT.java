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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;

@SpringBootTest
@AutoConfigureMockMvc
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
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
    }

    @Test
    @DisplayName("Can list restaurants when none exist")
    void listRestaurantsEmptyTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call list restaurants API.
        MvcTestResult listResult = listRestaurants();

        // Check that returned list is empty.
        assertThat(listResult).hasStatusOk();
        String responseBody = listResult.getResponse().getContentAsString();
        List<ApiRestaurant> restaurants = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiRestaurant>>() {
                });
        assertThat(restaurants).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation works and creates restaurant in DB")
    void createRestaurantTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that returned restaurant matches request.
        ApiRestaurant newRestaurant = getRestaurantFromResponseBody(createResult);
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

        // Check that empty restaurantTables was created in DB.
        Optional<RestaurantTables> foundRestaurantTables = restaurantTablesRepository.findById(restaurantId);
        assertThat(foundRestaurantTables).isPresent();
        assertEquals(restaurantId, foundRestaurantTables.get().getId());
        assertThat(foundRestaurantTables.get().getTables()).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation followed by get for the same restaurant")
    void createAndGetRestaurantTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Sanity check response.
        ApiRestaurant newRestaurant = getRestaurantFromResponseBody(createResult);
        String restaurantId = newRestaurant.getId();
        assertThat(restaurantId).isNotBlank();

        // Call get restaurant API.
        MvcTestResult getResult = getRestaurant(restaurantId);

        // Check that returned restaurant matches created one.
        ApiRestaurant getRestaurant = getRestaurantFromResponseBody(getResult);
        assertEquals(newRestaurant, getRestaurant);
    }

    @Test
    @DisplayName("Multiple restaurant creation followed by list")
    void createAndListMultipleRestaurantsTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API twice.
        ApiRestaurant apiRestaurant1 = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        MvcTestResult createResult1 = createRestaurant(apiRestaurant1);
        ApiRestaurant apiRestaurant2 = new ApiRestaurant("Restaurant2", "Address2", "email2", "USD");
        MvcTestResult createResult2 = createRestaurant(apiRestaurant2);

        // Sanity check responses.
        ApiRestaurant newRestaurant1 = getRestaurantFromResponseBody(createResult1);
        ApiRestaurant newRestaurant2 = getRestaurantFromResponseBody(createResult2);

        // Call list restaurants API.
        MvcTestResult listResult = listRestaurants();

        // Check that returned list matches.
        assertThat(listResult).hasStatusOk();
        String responseBody = listResult.getResponse().getContentAsString();
        List<ApiRestaurant> restaurants = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiRestaurant>>() {
                });
        assertThat(restaurants).satisfiesExactly(
                r1 -> assertEquals(newRestaurant1, r1),
                r2 -> assertEquals(newRestaurant2, r2));
    }

    @Test
    @DisplayName("Creating multiple restaurants with same email fails")
    void createMultipleRestaurantsWithSameEmailFailsTest()
            throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API twice.
        ApiRestaurant apiRestaurant1 = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        MvcTestResult createResult1 = createRestaurant(apiRestaurant1);
        ApiRestaurant newRestaurant1 = getRestaurantFromResponseBody(createResult1);
        ApiRestaurant apiRestaurant2 = new ApiRestaurant("Restaurant2", "Address2", "email1", "USD");
        MvcTestResult createResult2 = createRestaurant(apiRestaurant2);

        // Check that second request fails.
        assertThat(createResult2).hasStatus(HttpStatus.CONFLICT);
        assertThat(createResult2).bodyText().contains("already exists");

        // Check that only first restaurant exists by calling listRestaurants API.
        MvcTestResult listResult = listRestaurants();
        List<ApiRestaurant> restaurants = getRestaurantListFromResponseBody(listResult);
        assertThat(restaurants).satisfiesExactly(
                r1 -> assertEquals(newRestaurant1, r1));
    }

    @Test
    @DisplayName("Getting a restaurant that doesn't exist returns NOT_FOUND")
    void getNonExistentRestaurantTest() {
        // Call get restaurant API.
        MvcTestResult getResult = getRestaurant("1234");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Restaurant creation with explicit ID fails")
    void createRestaurantWithIDFailsTest() throws JsonProcessingException {
        // Call create restaurant API with explicit ID.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "EUR");
        apiRestaurant.setId("1234");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`id` must not be set");

        // Check that restaurant wasn't created.
        MvcTestResult getResult = getRestaurant("1234");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Restaurant creation without a name fails")
    void createRestaurantWithoutNameFailsTest() throws JsonProcessingException {
        // Call create restaurant API without name.
        ApiRestaurant apiRestaurant = new ApiRestaurant("", "Address1", "email1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`name` is required");

        // Check that no restaurant was created in DB.
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();
        assertThat(foundRestaurants).isEmpty();
    }
    
    @Test
    @DisplayName("Restaurant creation without an email fails")
    void createRestaurantWithoutEmailFailsTest() throws JsonProcessingException {
        // Call create restaurant API without email.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`email` is required");

        // Check that no restaurant was created in DB.
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();
        assertThat(foundRestaurants).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation without a currency fails")
    void createRestaurantWithoutCurrencyFailsTest() throws JsonProcessingException {
        // Call create restaurant API without name.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`currency` is required");

        // Check that no restaurant was created in DB.
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();
        assertThat(foundRestaurants).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation with invalid currency fails")
    void createRestaurantWithInvalidCurrencyFailsTest() throws JsonProcessingException {
        // Call create restaurant API without name.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "email1", "ASDF");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("ASDF is not a valid ISO 4217 currency");

        // Check that no restaurant was created in DB.
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();
        assertThat(foundRestaurants).isEmpty();
    }

    private MvcTestResult createRestaurant(ApiRestaurant apiRestaurant) throws JsonProcessingException {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiRestaurant))
                .exchange();
        return result;
    }

    private MvcTestResult getRestaurant(String id) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{id}", id)
                .exchange();
        return result;
    }

    private MvcTestResult listRestaurants() {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants")
                .exchange();
        return result;
    }

    private ApiRestaurant getRestaurantFromResponseBody(MvcTestResult testResult)
            throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiRestaurant newRestaurant = objectMapper.readValue(responseBody, ApiRestaurant.class);
        return newRestaurant;
    }

    private List<ApiRestaurant> getRestaurantListFromResponseBody(MvcTestResult testResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        List<ApiRestaurant> restaurants = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiRestaurant>>() {
                });
        return restaurants;
    }

}
