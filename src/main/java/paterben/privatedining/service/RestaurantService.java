package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Restaurant;

public interface RestaurantService {
    public List<Restaurant> listRestaurants();
    
    public Restaurant createRestaurant(Restaurant restaurant);

    public Optional<Restaurant> getRestaurantById(String id);
}
