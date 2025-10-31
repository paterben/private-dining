package paterben.privatedining.service;

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
        // Create documents for the restaurant in both restaurant-related repositories
        // (restaurants and restaurantTables) within a single transaction.
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        RestaurantTables restaurantTables = new RestaurantTables();
        restaurantTables.setId(newRestaurant.getId());
        restaurantTablesRepository.save(restaurantTables);

        return newRestaurant;
    }

    @Override
    public Optional<Restaurant> getRestaurantById(String id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (!restaurant.isPresent()) {
            return Optional.empty();
        }
        return restaurant;
    }
}
