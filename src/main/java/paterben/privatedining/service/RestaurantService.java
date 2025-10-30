package paterben.privatedining.service;

import java.util.Optional;

import paterben.privatedining.core.model.Restaurant;

public interface RestaurantService {
    public Restaurant createRestaurant(Restaurant restaurant);

    public Optional<Restaurant> getRestaurantById(String id);
}
