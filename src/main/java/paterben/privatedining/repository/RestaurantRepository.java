package paterben.privatedining.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.Restaurant;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {

  // public Optional<StorageRestaurant> findById(String id);
  // public List<Restaurant> findByName(String name);
}
