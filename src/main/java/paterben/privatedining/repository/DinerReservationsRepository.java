package paterben.privatedining.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.DinerReservations;

public interface DinerReservationsRepository extends MongoRepository<DinerReservations, String> {
}
