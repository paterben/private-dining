package paterben.privatedining.api;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.repository.RestaurantRepository;

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
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
    }

    @Test
    @DisplayName("Restaurant creation works and creates restaurant in DB")
    void createRestaurantTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that returned restaurant matches request.
        assertThat(createResult).hasStatusOk();
        String responseBody = createResult.getResponse().getContentAsString();
        ApiRestaurant newRestaurant = objectMapper.readValue(responseBody, ApiRestaurant.class);
        String restaurantId = newRestaurant.getId();
        assertThat(restaurantId).isNotBlank();
        Instant created = newRestaurant.getCreated();
        assertThat(created).isNotNull();
        apiRestaurant.setId(restaurantId);
        assertEquals(apiRestaurant, newRestaurant);

        // Check that restaurant was created in DB.
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(restaurantId);
        assertThat(foundRestaurant).isPresent();
        assertEquals(restaurantId, foundRestaurant.get().getId());
        // Created time is truncated to milliseconds by MongoDB.
        assertEquals(created.truncatedTo(ChronoUnit.MILLIS),
                foundRestaurant.get().getCreated().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(newRestaurant.getName(), foundRestaurant.get().getName());
    }

    @Test
    @DisplayName("Restaurant creation followed by get for the same restaurant")
    void createAndGetRestaurantTest() throws UnsupportedEncodingException, JsonProcessingException {
        // Call create restaurant API.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Sanity check response.
        assertThat(createResult).hasStatusOk();
        String createResponseBody = createResult.getResponse().getContentAsString();
        ApiRestaurant newRestaurant = objectMapper.readValue(createResponseBody, ApiRestaurant.class);
        String restaurantId = newRestaurant.getId();
        assertThat(restaurantId).isNotBlank();

        // Call get restaurant API.
        MvcTestResult getResult = getRestaurant(restaurantId);
        assertThat(getResult).hasStatusOk();

        // Check that returned restaurant matches created one.
        assertThat(getResult).hasStatusOk();
        String getResponseBody = getResult.getResponse().getContentAsString();
        ApiRestaurant getRestaurant = objectMapper.readValue(getResponseBody, ApiRestaurant.class);
        assertEquals(newRestaurant, getRestaurant);
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
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "EUR");
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
        ApiRestaurant apiRestaurant = new ApiRestaurant("", "Address1", "EUR");
        MvcTestResult createResult = createRestaurant(apiRestaurant);

        // Check that request fails.
        assertThat(createResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(createResult).bodyText().contains("`name` is required");

        // Check that no restaurant was created in DB.
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();
        assertThat(foundRestaurants).isEmpty();
    }

    @Test
    @DisplayName("Restaurant creation without a currency fails")
    void createRestaurantWithoutCurrencyFailsTest() throws JsonProcessingException {
        // Call create restaurant API without name.
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "");
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
        ApiRestaurant apiRestaurant = new ApiRestaurant("Restaurant1", "Address1", "ASDF");
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

}
