package paterben.privatedining.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import paterben.privatedining.core.model.TableReservations;

public interface TableReservationsRepository extends MongoRepository<TableReservations, String> {
}
