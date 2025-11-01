package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Restaurant;

/**
 * Service for managing {@link Restaurant Restaurants}.
 */
public interface RestaurantService {
    /**
     * Lists all restaurants.
     */
    public List<Restaurant> listRestaurants();

    /**
     * Creates the given restaurant.
     * 
     * @param restaurant the restaurant to create. Required fields must be set.
     *                   Fields that are set automatically must not be set.
     * @throws ServiceException if the {@code restaurant} is invalid or a restaurant
     *                          with the same email already exists.
     * @return the created restaurant.
     */
    public Restaurant createRestaurant(Restaurant restaurant) throws ServiceException;

    /**
     * Gets the restaurant with the given ID.
     * 
     * @param restaurantId the restaurant ID.
     * @return the restaurant metadata, or an empty {@link Optional} if the
     *         restaurant does not exist.
     */
    public Optional<Restaurant> getRestaurantById(String restaurantId);
}
