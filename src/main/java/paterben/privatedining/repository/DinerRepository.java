package paterben.privatedining.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.Diner;

public interface DinerRepository extends MongoRepository<Diner, String> {
  public Optional<Diner> findByEmail(String email);
}
