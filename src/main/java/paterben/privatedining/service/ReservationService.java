package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Reservation;

/**
 * Service for managing {@link Reservation Reservations}.
 */
public interface ReservationService {
    /**
     * Lists all reservations for the given restaurant and table.
     * 
     * @param restaurantId the restaurant ID.
     * @param tableId      the table ID.
     * @return the list of reservations, or an empty {@link Optional} if the
     *         restaurant or table does not exist.
     */
    public Optional<List<Reservation>> listReservationsForRestaurantAndTable(String restaurantId, String tableId);

    /**
     * Lists all reservations for the given diner.
     * 
     * @param dinerId the diner ID.
     * @return the list of reservations, or an empty {@link Optional} if the diner
     *         does not exist.
     */
    public Optional<List<Reservation>> listReservationsForDiner(String dinerId);

    /**
     * Gets the reservation with the given ID for the given diner.
     * 
     * @param dinerId       the diner ID.
     * @param reservationId the reservation ID.
     * @return the reservation metadata, or an empty {@link Optional} if the diner
     *         or reservation does not exist.
     */
    public Optional<Reservation> getReservationForDinerById(String dinerId, String reservationId);

    /**
     * Gets the reservation with the given ID for the given restaurant and table.
     * 
     * @param restaurantId  the restaurant ID.
     * @param tableId       the table ID.
     * @param reservationId the reservation ID.
     * @return the reservation metadata, or an empty {@link Optional} if the
     *         restaurant, table or reservation does not exist.
     */
    public Optional<Reservation> getReservationForRestaurantAndTableById(String restaurantId, String tableId,
            String reservationId);

    /**
     * Creates the given reservation for the given restaurant and table.
     * 
     * @param restaurantId the restaurant ID.
     * @param tableId      the table ID.
     * @param reservation  the reservation to create. Required fields must be set.
     *                     Fields that are set automatically must not be set. The
     *                     reservation to create cannot be in the cancelled state.
     * @throws ServiceException if the {@code reservation} is invalid, conflicts
     *                          with an existing reservation for the same table, has
     *                          already started or the restaurant, table or diner
     *                          does not exist.
     * @return the created reservation.
     */
    public Reservation createReservationForRestaurantAndTable(String restaurantId, String tableId,
            Reservation reservation) throws ServiceException;

    /**
     * Updates the given reservation for the given restaurant and table. Currently,
     * only cancellation is supported.
     * 
     * @param restaurantId  the restaurant ID.
     * @param tableId       the table ID.
     * @param reservationId the reservation ID.
     * @param reservation   the reservation to update. To cancel the reservation,
     *                      set {@link Reservation#isCancelled} to true. Other
     *                      fields are ignored.
     * @throws ServiceException if the {@code reservation} is invalid, has already
     *                          started or the restaurant, table or reservation does
     *                          not exist.
     * @return the updated reservation.
     */
    public Reservation updateReservationForRestaurantAndTable(String restaurantId, String tableId, String reservationId,
            Reservation reservation) throws ServiceException;
}
