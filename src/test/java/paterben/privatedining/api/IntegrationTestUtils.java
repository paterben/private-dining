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
import paterben.privatedining.api.model.ApiRestaurant;

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

    public ApiDiner getDinerAndGetResult(String id)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = getDiner(id);
        return getDinerFromResponseBody(result);
    }

    public MvcTestResult listDiners() {
        MvcTestResult result = this.mockMvcTester.get()
                .uri("/api/diners")
                .exchange();
        return result;
    }

    public List<ApiDiner> listDinersAndGetResult()
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = listDiners();
        return getDinerListFromResponseBody(result);
    }

    public ApiRestaurant getRestaurantFromResponseBody(MvcTestResult testResult)
            throws JsonProcessingException, UnsupportedEncodingException {
        assertThat(testResult).hasStatusOk();
        String responseBody = testResult.getResponse().getContentAsString();
        ApiRestaurant newRestaurant = objectMapper.readValue(responseBody, ApiRestaurant.class);
        return newRestaurant;
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
        ApiDiner newDiner = objectMapper.readValue(responseBody, ApiDiner.class);
        return newDiner;
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

}
