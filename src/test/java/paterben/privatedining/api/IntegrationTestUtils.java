package paterben.privatedining.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.api.model.ApiReservation;
import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;

/**
 * Utility methods for integration tests.
 */
@TestComponent
public class IntegrationTestUtils {
    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    public MvcTestResult createRestaurant(ApiRestaurant apiRestaurant) throws JsonProcessingException {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiRestaurant))
                .exchange();
        return result;
    }

    public ApiRestaurant createRestaurantAndGetResult(ApiRestaurant apiRestaurant)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = createRestaurant(apiRestaurant);
        return getRestaurantFromResponseBody(result);
    }

    public MvcTestResult getRestaurant(String id) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{id}", id)
                .exchange();
        return result;
    }

    public ApiRestaurant getRestaurantAndGetResult(String id)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getRestaurant(id);
        return getRestaurantFromResponseBody(result);
    }

    public MvcTestResult listRestaurants() {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants")
                .exchange();
        return result;
    }

    public List<ApiRestaurant> listRestaurantsAndGetResult()
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listRestaurants();
        return getRestaurantListFromResponseBody(result);
    }

    public MvcTestResult createDiner(ApiDiner apiDiner) throws JsonProcessingException {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/api/diners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiDiner))
                .exchange();
        return result;
    }

    public ApiDiner createDinerAndGetResult(ApiDiner apiDiner)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = createDiner(apiDiner);
        return getDinerFromResponseBody(result);
    }

    public MvcTestResult getDiner(String id) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/diners/{id}", id)
                .exchange();
        return result;
    }

    public ApiDiner getDinerAndGetResult(String id) throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getDiner(id);
        return getDinerFromResponseBody(result);
    }

    public MvcTestResult listDiners() {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/diners")
                .exchange();
        return result;
    }

    public List<ApiDiner> listDinersAndGetResult() throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listDiners();
        return getDinerListFromResponseBody(result);
    }

    public MvcTestResult addTableToRestaurant(String restaurantId, ApiTable apiTable) throws JsonProcessingException {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/api/restaurants/{restaurantId}/tables", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiTable))
                .exchange();
        return result;
    }

    public ApiTable addTableToRestaurantAndGetResult(String restaurantId, ApiTable apiTable)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = addTableToRestaurant(restaurantId, apiTable);
        return getTableFromResponseBody(result);
    }

    public MvcTestResult getTableForRestaurant(String restaurantId, String tableId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{restaurantId}/tables/{tableId}", restaurantId, tableId)
                .exchange();
        return result;
    }

    public ApiTable getTableForRestaurantAndGetResult(String restaurantId, String tableId)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getTableForRestaurant(restaurantId, tableId);
        return getTableFromResponseBody(result);
    }

    public MvcTestResult listTablesForRestaurant(String restaurantId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{restaurantId}/tables", restaurantId)
                .exchange();
        return result;
    }

    public List<ApiTable> listTablesForRestaurantAndGetResult(String restaurantId)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listTablesForRestaurant(restaurantId);
        return getTableListFromResponseBody(result);
    }

    public MvcTestResult listReservationsForRestaurantAndTable(String restaurantId, String tableId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{restaurantId}/tables/{tableId}/reservations", restaurantId, tableId)
                .exchange();
        return result;
    }

    public List<ApiReservation> listReservationsForRestaurantAndTableAndGetResult(String restaurantId, String tableId)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listReservationsForRestaurantAndTable(restaurantId, tableId);
        return getApiReservationListFromResponseBody(result);
    }

    public MvcTestResult listReservationsForDiner(String dinerId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/diners/{dinerId}/reservations", dinerId)
                .exchange();
        return result;
    }

    public List<ApiReservation> listReservationsForDinerAndGetResult(String dinerId)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listReservationsForDiner(dinerId);
        return getApiReservationListFromResponseBody(result);
    }

    public MvcTestResult getReservationForDiner(String dinerId, String reservationId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/diners/{dinerId}/reservations/{reservationId}", dinerId, reservationId)
                .exchange();
        return result;
    }

    public ApiReservation getReservationForDinerAndGetResult(String dinerId, String reservationId)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getReservationForDiner(dinerId, reservationId);
        return getApiReservationFromResponseBody(result);
    }

    public MvcTestResult getReservationForRestaurantAndTable(String restaurantId, String tableId,
            String reservationId) {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/restaurants/{restaurantId}/tables/{tableId}/reservations/{reservationId}", restaurantId,
                        tableId, reservationId)
                .exchange();
        return result;
    }

    public ApiReservation getReservationForRestaurantAndTableAndGetResult(String restaurantId, String tableId,
            String reservationId) throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getReservationForRestaurantAndTable(restaurantId, tableId, reservationId);
        return getApiReservationFromResponseBody(result);
    }

    public MvcTestResult createReservationForRestaurantAndTable(String restaurantId, String tableId,
            ApiReservation apiReservation) throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/api/restaurants/{restaurantId}/tables/{tableId}/reservations", restaurantId, tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiReservation))
                .exchange();
        return result;
    }

    public ApiReservation createReservationForRestaurantAndTableAndGetResult(String restaurantId, String tableId,
            ApiReservation apiReservation) throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = createReservationForRestaurantAndTable(restaurantId, tableId, apiReservation);
        return getApiReservationFromResponseBody(result);
    }

    public MvcTestResult updateReservationForRestaurantAndTable(String restaurantId, String tableId,
            String reservationId, ApiReservation apiReservation)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = this.mockMvcTester.patch()
                .uri("/api/restaurants/{restaurantId}/tables/{tableId}/reservations/{reservationId}", restaurantId,
                        tableId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apiReservation))
                .exchange();
        return result;
    }

    public ApiReservation updateReservationForRestaurantAndTableAndGetResult(String restaurantId, String tableId,
            String reservationId, ApiReservation apiReservation)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = updateReservationForRestaurantAndTable(restaurantId, tableId, reservationId,
                apiReservation);
        return getApiReservationFromResponseBody(result);
    }

    public ApiRestaurant getRestaurantFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiRestaurant restaurant = objectMapper.readValue(responseBody, ApiRestaurant.class);
        return restaurant;
    }

    public List<ApiRestaurant> getRestaurantListFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        List<ApiRestaurant> restaurants = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiRestaurant>>() {
                });
        return restaurants;
    }

    public ApiDiner getDinerFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiDiner diner = objectMapper.readValue(responseBody, ApiDiner.class);
        return diner;
    }

    public List<ApiDiner> getDinerListFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        List<ApiDiner> diners = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiDiner>>() {
                });
        return diners;
    }

    public ApiTable getTableFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiTable table = objectMapper.readValue(responseBody, ApiTable.class);
        return table;
    }

    public List<ApiTable> getTableListFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        List<ApiTable> tables = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiTable>>() {
                });
        return tables;
    }

    public ApiReservation getApiReservationFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiReservation reservation = objectMapper.readValue(responseBody, ApiReservation.class);
        return reservation;
    }

    public List<ApiReservation> getApiReservationListFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        List<ApiReservation> reservations = objectMapper.readValue(responseBody,
                new TypeReference<List<ApiReservation>>() {
                });
        return reservations;
    }

    public MvcTestResult deleteAllData() {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/admin/deleteAllData")
                .exchange();
        return result;
    }

    public MvcTestResult setupSampleData() {
        MvcTestResult result = this.mockMvcTester.post()
                .uri("/admin/setupSampleData")
                .exchange();
        return result;
    }
}
