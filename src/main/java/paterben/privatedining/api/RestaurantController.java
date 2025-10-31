package paterben.privatedining.api;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import paterben.privatedining.api.conversion.ApiConverter;
import paterben.privatedining.api.model.ApiErrorInfo;
import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.service.RestaurantService;
import paterben.privatedining.service.RestaurantServiceException;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "Restaurant controller", description = "The controller used to manage restaurants.")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ApiConverter converter;

    @GetMapping(path = "/api/restaurants")
    @Operation(summary = "List restaurants", description = "Returns the list of restaurants.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
    })
    public ResponseEntity<List<ApiRestaurant>> listRestaurants() {
        List<Restaurant> restaurants = restaurantService.listRestaurants();
        List<ApiRestaurant> apiRestaurants = restaurants.stream().map(r -> converter.ToApi(r)).toList();
        return ResponseEntity.ok(apiRestaurants);
    }

    @GetMapping(path = "/api/restaurants/{restaurantId}")
    @Operation(summary = "Get restaurant by ID", description = "Returns the specific restaurant info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ApiRestaurant> getRestaurantById(@PathVariable("restaurantId") String restaurantId) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(restaurantId);
        if (!restaurant.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(converter.ToApi(restaurant.get()));
    }

    @PostMapping(path = "/api/restaurants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new restaurant", description = "Creates a new restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema()))
    })
    public ApiRestaurant createRestaurant(@RequestBody ApiRestaurant apiRestaurant) {
        ValidateRestaurantForCreation(apiRestaurant);

        Restaurant restaurant = converter.ToCore(apiRestaurant);
        Restaurant newRestaurant = restaurantService.createRestaurant(restaurant);
        return converter.ToApi(newRestaurant);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, ApiException ex) {
        // logger.error("Request: " + req.getRequestURL() + " raised " + ex);

        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }

    @ExceptionHandler(RestaurantServiceException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, RestaurantServiceException ex) {
        // logger.error("Request: " + req.getRequestURL() + " raised " + ex);

        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }

    // TODO move to core logic.
    private void ValidateRestaurantForCreation(ApiRestaurant apiRestaurant) throws ApiException {
        if (StringUtils.hasLength(apiRestaurant.getId())) {
            throw new ApiException("`id` must not be set when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(apiRestaurant.getName())) {
            throw new ApiException("`name` is required when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(apiRestaurant.getCurrency())) {
            throw new ApiException("`currency` is required when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Currency.getInstance(apiRestaurant.getCurrency());
        } catch (IllegalArgumentException e) {
            throw new ApiException(apiRestaurant.getCurrency() + " is not a valid ISO 4217 currency.",
                    HttpStatus.BAD_REQUEST);
        }
        // Standardize currency code to upper-case.
        apiRestaurant.setCurrency(apiRestaurant.getCurrency().toUpperCase());
        if (apiRestaurant.getCreated() != null) {
            throw new ApiException("`created` must not be set when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
