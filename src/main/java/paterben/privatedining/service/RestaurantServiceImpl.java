package paterben.privatedining.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        // MongoDB silently truncates the created time to milliseconds.
        // See https://github.com/spring-projects/spring-data-mongodb/issues/2883.
        newRestaurant.setCreated(newRestaurant.getCreated().truncatedTo(ChronoUnit.MILLIS));
        // Create an empty document in the restaurantTables repository within the same
        // transaction, so it does not have to be created when adding the first table.
        RestaurantTables restaurantTables = new RestaurantTables();
        restaurantTables.setId(newRestaurant.getId());
        restaurantTablesRepository.save(restaurantTables);

        return newRestaurant;
    }

    @Override
    public Optional<Restaurant> getRestaurantById(String id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        return restaurant;
    }

    @Override
    public List<Restaurant> listRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants;
    }
}
