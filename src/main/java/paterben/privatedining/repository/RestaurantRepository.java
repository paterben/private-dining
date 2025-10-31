package paterben.privatedining.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.Restaurant;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
  public Optional<Restaurant> findByEmail(String email);
}
