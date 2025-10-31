package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Reservation;

public interface ReservationService {
    public Optional<List<Reservation>> listReservationsForRestaurantAndTable(String restaurantId, String tableId);

    public Optional<List<Reservation>> listReservationsForDiner(String dinerId);

    public Optional<Reservation> getReservationForDinerById(String dinerId, String reservationId);

    public Optional<Reservation> getReservationForRestaurantAndTableById(String restaurantId, String tableId,
            String reservationId);

    public Reservation createReservationForRestaurantAndTable(String restaurantId, String tableId,
            Reservation reservation);

    public Reservation updateReservationForRestaurantAndTable(String restaurantId, String tableId, String reservationId,
            Reservation reservation);
}
