package paterben.privatedining.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
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

import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantTablesRepository restaurantTablesRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @BeforeEach
    void setUp() {
        lenient().when(restaurantRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(restaurantRepository.findById(any())).thenReturn(Optional.empty());
        lenient().when(restaurantRepository.findByEmail(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("When restaurants exist, listRestaurants returns the list")
    void testListRestaurants() {
        // Arrange
        Restaurant foundRestaurant1 = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        Restaurant foundRestaurant2 = new Restaurant("2345", "restaurant2", "address2", "email2", "EUR",
                Instant.ofEpochSecond(2345));
        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(foundRestaurant1, foundRestaurant2));

        // Act
        List<Restaurant> result = restaurantService.listRestaurants();

        // Assert
        assertThat(result).satisfiesExactly(
                r -> assertEquals(foundRestaurant1, r),
                r -> assertEquals(foundRestaurant2, r));
    }

    @Test
    @DisplayName("When no restaurants exist, listRestaurants returns an empty list")
    void testListRestaurantsEmpty() {
        // Act
        List<Restaurant> result = restaurantService.listRestaurants();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When restaurant exists, getRestaurantById returns it")
    void testGetRestaurantByIdFound() {
        // Arrange
        Restaurant foundRestaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        when(restaurantRepository.findById("1234")).thenReturn(Optional.of(foundRestaurant));

        // Act
        Optional<Restaurant> result = restaurantService.getRestaurantById("1234");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(foundRestaurant);
    }

    @Test
    @DisplayName("When restaurant doesn't exist, getRestaurantById returns empty")
    void testGetRestaurantByIdNotFound() {
        // Act
        Optional<Restaurant> result = restaurantService.getRestaurantById("1234");

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("When restaurant is valid, createRestaurant saves to restaurant and restaurantTables and returns new restaurant")
    void testCreateRestaurant() {
        // Arrange
        when(restaurantRepository.save(any()))
                .thenAnswer(makeSetIdAndCreatedTimeOnRestaurantAnswer("1234", Instant.ofEpochSecond(1234)));
        when(restaurantTablesRepository.save(any())).thenAnswer(makeRestaurantTablesAnswer());

        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "EUR");
        Restaurant result = restaurantService.createRestaurant(restaurant);

        // Assert
        Restaurant expectedRestaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        assertThat(result).isEqualTo(expectedRestaurant);
        verify(restaurantRepository).save(ArgumentMatchers.eq(restaurant));
        RestaurantTables restaurantTables = new RestaurantTables("1234");
        verify(restaurantTablesRepository).save(ArgumentMatchers.eq(restaurantTables));
    }

    @Test
    @DisplayName("createRestaurant truncates created time to millis")
    void testCreateRestaurantTruncatesCreatedTimeToMillis() {
        // Arrange
        Instant nano = Instant.ofEpochSecond(1234, 111222333);
        Instant truncated = Instant.ofEpochSecond(1234, 111000000);
        when(restaurantRepository.save(any()))
                .thenAnswer(makeSetIdAndCreatedTimeOnRestaurantAnswer("1234", nano));
        when(restaurantTablesRepository.save(any())).thenAnswer(makeRestaurantTablesAnswer());

        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "EUR");
        Restaurant result = restaurantService.createRestaurant(restaurant);

        // Assert
        Restaurant expectedRestaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR", truncated);
        assertThat(result).isEqualTo(expectedRestaurant);
    }

    @Test
    @DisplayName("createRestaurant transforms lowercase currency to uppercase")
    void testCreateRestaurantLowercaseCurrency() {
        // Arrange
        when(restaurantRepository.save(any()))
                .thenAnswer(makeSetIdAndCreatedTimeOnRestaurantAnswer("1234", Instant.ofEpochSecond(1234)));
        when(restaurantTablesRepository.save(any())).thenAnswer(makeRestaurantTablesAnswer());

        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "eur");
        Restaurant result = restaurantService.createRestaurant(restaurant);

        // Assert
        Restaurant expectedRestaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        assertThat(result).isEqualTo(expectedRestaurant);
        restaurant.setCurrency("EUR");
        verify(restaurantRepository).save(ArgumentMatchers.eq(restaurant));
    }

    @Test
    @DisplayName("When restaurant with same email already exists, createRestaurant fails with CONFLICT")
    void testCreateRestaurantSameEmailFailsWithConflict() {
        // Arrange
        Restaurant foundRestaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1111));
        when(restaurantRepository.findByEmail("email1")).thenReturn(Optional.of(foundRestaurant));

        // Act
        Restaurant restaurant = new Restaurant("restaurant2", "address2", "email1", "EUR");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getMessage()).contains("already exists");
        }
    }

    @Test
    @DisplayName("When restaurant to create has ID set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithIdFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "EUR");
        restaurant.setId("1234");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`id` must not be set");
        }
    }

    @Test
    @DisplayName("When restaurant to create doesn't have name set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithoutNameFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("", "address1", "email1", "EUR");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`name` is required");
        }
    }

    @Test
    @DisplayName("When restaurant to create doesn't have email set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithoutEmailFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "", "EUR");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`email` is required");
        }
    }

    @Test
    @DisplayName("When restaurant to create doesn't have currency set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithoutCurrencyFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`currency` is required");
        }
    }

    @Test
    @DisplayName("When restaurant to create has invalid currency set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithInvalidCurrencyFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "ASDF");
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("not a valid ISO 4217 currency");
        }
    }

    @Test
    @DisplayName("When restaurant to create has createdAt set, createRestaurant fails with BAD_REQUEST")
    void testCreateRestaurantWithCreatedAtFailsWithBadRequest() {
        // Act
        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "EUR");
        restaurant.setCreatedAt(Instant.ofEpochSecond(1234));
        try {
            restaurantService.createRestaurant(restaurant);
            fail();
        } catch (ServiceException e) {
            // Assert
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getMessage()).contains("`createdAt` must not be set");
        }
    }

    private Answer<Restaurant> makeSetIdAndCreatedTimeOnRestaurantAnswer(String id, Instant createdAt) {
        return new Answer<Restaurant>() {
            public Restaurant answer(InvocationOnMock invocation) {
                Restaurant rest = invocation.getArgument(0, Restaurant.class);
                rest.setId(id);
                rest.setCreatedAt(createdAt);
                return rest;
            }
        };
    }

    private Answer<RestaurantTables> makeRestaurantTablesAnswer() {
        return new Answer<RestaurantTables>() {
            public RestaurantTables answer(InvocationOnMock invocation) {
                RestaurantTables rt = invocation.getArgument(0, RestaurantTables.class);
                return rt;
            }
        };
    }
}
