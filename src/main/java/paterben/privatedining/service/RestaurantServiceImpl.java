package paterben.privatedining.service;

import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RestaurantTables;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Override
    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        ValidateRestaurantForCreation(restaurant);

        Optional<Restaurant> existingRestaurant = restaurantRepository.findByEmail(restaurant.getEmail());
        if (existingRestaurant.isPresent()) {
            throw new ServiceException("Restaurant with email \"" + restaurant.getEmail() + "\" already exists",
                    HttpStatus.CONFLICT);
        }

        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        // MongoDB silently truncates the created time to milliseconds.
        // See https://github.com/spring-projects/spring-data-mongodb/issues/2883.
        newRestaurant.setCreatedAt(newRestaurant.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        // Create an empty document in the restaurantTables repository within the same
        // transaction, so it does not have to be created when adding the first table.
        RestaurantTables restaurantTables = new RestaurantTables();
        restaurantTables.setId(newRestaurant.getId());
        restaurantTablesRepository.save(restaurantTables);

        return newRestaurant;
    }

    @Override
    public Optional<Restaurant> getRestaurantById(String restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        return restaurant;
    }

    @Override
    public List<Restaurant> listRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants;
    }

    private void ValidateRestaurantForCreation(Restaurant restaurant) {
        if (StringUtils.hasLength(restaurant.getId())) {
            throw new ServiceException("`id` must not be set when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(restaurant.getName())) {
            throw new ServiceException("`name` is required when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(restaurant.getEmail())) {
            throw new ServiceException("`email` is required when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(restaurant.getCurrency())) {
            throw new ServiceException("`currency` is required when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
        // All currency codes should be upper-case.
        restaurant.setCurrency(restaurant.getCurrency().toUpperCase());
        try {
            Currency.getInstance(restaurant.getCurrency());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(restaurant.getCurrency() + " is not a valid ISO 4217 currency.",
                    HttpStatus.BAD_REQUEST);
        }
        if (restaurant.getCreatedAt() != null) {
            throw new ServiceException("`createdAt` must not be set when creating a restaurant.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
