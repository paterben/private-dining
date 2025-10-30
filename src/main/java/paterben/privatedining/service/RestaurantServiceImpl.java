package paterben.privatedining.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.repository.RestaurantRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
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
