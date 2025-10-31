package paterben.privatedining.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.RestaurantTables;

public interface RestaurantTablesRepository extends MongoRepository<RestaurantTables, String> {
}
