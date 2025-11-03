package paterben.privatedining.service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import paterben.privatedining.core.ReservationConflict;
import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.core.model.TableReservations;
import paterben.privatedining.repository.DinerReservationsRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private TableReservationsRepository tableReservationsRepository;

    @Autowired
    private DinerReservationsRepository dinerReservationsRepository;

    @Autowired
    private Clock clock;

    @Override
    public Optional<List<Reservation>> listReservationsForRestaurantAndTable(String restaurantId, String tableId) {
        Optional<TableReservations> tableReservations = tableReservationsRepository.findById(tableId);
        if (!tableReservations.isPresent()) {
            return Optional.empty();
        }
        // Verify that the restaurant IDs match, otherwise the table is for another
        // restaurant.
        String existingRestaurantId = tableReservations.get().getRestaurantId();
        if (existingRestaurantId == null || !existingRestaurantId.equals(restaurantId)) {
            return Optional.empty();
        }
        return Optional.of(tableReservations.get().getReservations());
    }

    @Override
    public Optional<List<Reservation>> listReservationsForDiner(String dinerId) {
        Optional<DinerReservations> dinerReservations = dinerReservationsRepository.findById(dinerId);
        if (!dinerReservations.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(dinerReservations.get().getReservations());
    }

    @Override
    public Optional<Reservation> getReservationForDinerById(String dinerId, String reservationId) {
        Optional<DinerReservations> dinerReservations = dinerReservationsRepository.findById(dinerId);
        if (!dinerReservations.isPresent()) {
            return Optional.empty();
        }
        Optional<Reservation> reservation = dinerReservations.get().getReservations().stream()
                .filter(r -> r.getId() != null && r.getId().equals(reservationId)).findFirst();
        return reservation;
    }

    @Override
    public Optional<Reservation> getReservationForRestaurantAndTableById(String restaurantId, String tableId,
            String reservationId) {
        Optional<TableReservations> tableReservations = tableReservationsRepository.findById(tableId);
        if (!tableReservations.isPresent()) {
            return Optional.empty();
        }
        // Verify that the restaurant IDs match, otherwise the table is for another
        // restaurant.
        String existingRestaurantId = tableReservations.get().getRestaurantId();
        if (existingRestaurantId == null || !existingRestaurantId.equals(restaurantId)) {
            return Optional.empty();
        }
        Optional<Reservation> reservation = tableReservations.get().getReservations().stream()
                .filter(r -> r.getId() != null && r.getId().equals(reservationId)).findFirst();
        return reservation;
    }

    @Override
    @Transactional
    public Reservation createReservationForRestaurantAndTable(String restaurantId, String tableId,
            Reservation reservation) {
        ValidateReservationForCreation(reservation);

        Optional<TableReservations> tableReservations = tableReservationsRepository.findById(tableId);
        if (tableReservations.isEmpty()) {
            throw new ServiceException("Table with ID " + tableId + " not found",
                    HttpStatus.NOT_FOUND);
        }
        // Verify that the restaurant IDs match, otherwise the table is for another
        // restaurant.
        String existingRestaurantId = tableReservations.get().getRestaurantId();
        if (existingRestaurantId == null || !existingRestaurantId.equals(restaurantId)) {
            throw new ServiceException("Table with ID " + tableId + " not found",
                    HttpStatus.NOT_FOUND);
        }

        // Verify that the reservation doesn't conflict with existing reservations, that
        // the table is big enough, etc.
        verifyReservationToCreateIsCompatibleWithTableReservations(reservation,
                tableReservations.get());

        Optional<DinerReservations> dinerReservations = dinerReservationsRepository.findById(reservation.getDinerId());
        if (dinerReservations.isEmpty()) {
            throw new ServiceException("Diner with ID " + reservation.getDinerId() + " not found",
                    HttpStatus.NOT_FOUND);
        }

        // We generate the reservation ID ourselves since it is an embedded document in
        // tableReservations / dinerReservations.
        String reservationId = new ObjectId().toString();
        reservation.setId(reservationId);
        reservation.setRestaurantId(restaurantId);
        reservation.setTableId(tableId);
        // We also generate the creation time ourselves for the same reason.
        reservation.setCreatedAt(Instant.now(clock).truncatedTo(ChronoUnit.MILLIS));

        // Finally, add the reservation to both tableReservations and dinerReservations
        // in the same transaction.
        tableReservations.get().getReservations().addLast(reservation);
        TableReservations newTableReservations = tableReservationsRepository.save(tableReservations.get());
        Reservation newReservation = newTableReservations.getReservations().getLast();
        dinerReservations.get().getReservations().addLast(reservation);
        dinerReservationsRepository.save(dinerReservations.get());

        return newReservation;
    }

    @Override
    @Transactional
    public Reservation updateReservationForRestaurantAndTable(String restaurantId, String tableId, String reservationId,
            Reservation reservation) {
        if (!reservation.getIsCancelled()) {
            throw new ServiceException(
                    "Setting `isCancelled` to true is required when updating a reservation. Only cancellation is supported.",
                    HttpStatus.BAD_REQUEST);
        }

        Optional<TableReservations> tableReservations = tableReservationsRepository.findById(tableId);
        if (tableReservations.isEmpty()) {
            throw new ServiceException("Table with ID " + tableId + " not found",
                    HttpStatus.NOT_FOUND);
        }
        // Verify that the restaurant IDs match, otherwise the table is for another
        // restaurant.
        String existingRestaurantId = tableReservations.get().getRestaurantId();
        if (existingRestaurantId == null || !existingRestaurantId.equals(restaurantId)) {
            throw new ServiceException("Table with ID " + tableId + " not found",
                    HttpStatus.NOT_FOUND);
        }

        // Verify that the reservation exists and is not already cancelled.
        Optional<Reservation> existingTableReservation = tableReservations.get().getReservations().stream()
                .filter(r -> r.getId() != null && r.getId().equals(reservationId)).findFirst();
        if (existingTableReservation.isEmpty()) {
            throw new ServiceException("Reservation with ID " + reservationId + " not found",
                    HttpStatus.NOT_FOUND);
        }
        if (existingTableReservation.get().getIsCancelled()) {
            throw new ServiceException("Reservation with ID " + reservationId + " is already cancelled",
                    HttpStatus.PRECONDITION_FAILED);
        }

        // Verify that the reservation hasn't already started.
        // TODO: Restaurants should probably be allowed to cancel started reservations.
        Instant now = Instant.now(clock);
        if (existingTableReservation.get().getReservationStart().isBefore(now)) {
            throw new ServiceException("Cannot cancel a reservation that has already begun.",
                    HttpStatus.BAD_REQUEST);
        }

        String dinerId = existingTableReservation.get().getDinerId();
        Optional<DinerReservations> dinerReservations = dinerReservationsRepository.findById(dinerId);
        if (dinerReservations.isEmpty()) {
            // The diner for the reservation should always exist, hence the internal server
            // error.
            throw new ServiceException("Diner with ID " + dinerId + " not found, this is unexpected",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<Reservation> existingDinerReservation = dinerReservations.get().getReservations().stream()
                .filter(r -> r.getId() != null && r.getId().equals(reservationId)).findFirst();
        if (existingDinerReservation.isEmpty()) {
            // The reservation should always exist in dinerReservations if it exists in
            // tableReservations, hence the internal server error.
            throw new ServiceException("Reservation with ID " + reservationId + " not found, this unexpected",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Generate reservation cancellation time.
        Instant cancelledAt = now.truncatedTo(ChronoUnit.MILLIS);

        // Finally, update the reservation in both tableReservations and
        // dinerReservations in the same transaction.
        existingTableReservation.get().setIsCancelled(true);
        existingTableReservation.get().setCancelledAt(cancelledAt);
        TableReservations newTableReservations = tableReservationsRepository.save(tableReservations.get());

        existingDinerReservation.get().setIsCancelled(true);
        existingDinerReservation.get().setCancelledAt(cancelledAt);
        dinerReservationsRepository.save(dinerReservations.get());

        Reservation newReservation = newTableReservations.getReservations().stream()
                .filter(r -> r.getId() != null && r.getId().equals(reservationId)).findFirst().get();
        return newReservation;
    }

    private void ValidateReservationForCreation(Reservation reservation) {
        if (StringUtils.hasLength(reservation.getId())) {
            throw new ServiceException("`id` must not be set when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.hasLength(reservation.getRestaurantId())) {
            throw new ServiceException("`restaurantId` must not be set when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.hasLength(reservation.getTableId())) {
            throw new ServiceException("`tableId` must not be set when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(reservation.getDinerId())) {
            throw new ServiceException("`dinerId` is required when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(reservation.getName())) {
            throw new ServiceException("`name` is required when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getNumGuests() == 0) {
            throw new ServiceException("`numGuests` is required when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getReservationStart() == null) {
            throw new ServiceException("`reservationStart` is required when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getReservationEnd() == null) {
            throw new ServiceException("`reservationEnd` is required when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getReservationStart().compareTo(reservation.getReservationEnd()) >= 0) {
            throw new ServiceException("`reservationEnd` must be strictly later than `reservationStart`.",
                    HttpStatus.BAD_REQUEST);
        }
        if (ChronoUnit.HOURS.between(reservation.getReservationStart(), reservation.getReservationEnd()) > 10) {
            throw new ServiceException("Hours between `reservationStart` and `reservationEnd` must be 10 or less.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getIsCancelled() != null && reservation.getIsCancelled()) {
            throw new ServiceException("`isCancelled` cannot be set to true when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        // Set isCancelled to false in case it is null.
        reservation.setIsCancelled(false);
        if (reservation.getCreatedAt() != null) {
            throw new ServiceException("`createdAt` must not be set when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
        if (reservation.getCancelledAt() != null) {
            throw new ServiceException("`cancelledAt` must not be set when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }

        if (reservation.getReservationStart().isBefore(Instant.now(clock))) {
            throw new ServiceException("`reservationStart` must not be in the past when creating a reservation.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void verifyReservationToCreateIsCompatibleWithTableReservations(Reservation reservation,
            TableReservations tableReservations) {
        // Check compatibility with table metadata.
        if (reservation.getNumGuests() > tableReservations.getMaxCapacity()) {
            throw new ServiceException(
                    "Number of guests in reservation is too high (maxCapacity " + tableReservations.getMaxCapacity()
                            + ", numGuests " + reservation.getNumGuests() + ").",
                    HttpStatus.CONFLICT);
        }
        if (reservation.getNumGuests() < tableReservations.getMinCapacity()) {
            throw new ServiceException(
                    "Number of guests in reservation is too low (minCapacity " + tableReservations.getMaxCapacity()
                            + ", numGuests " + reservation.getNumGuests() + ").",
                    HttpStatus.CONFLICT);
        }

        // Check compatibility with existing reservations.
        for (Reservation r : tableReservations.getReservations()) {
            if (r.getIsCancelled() != null && r.getIsCancelled()) {
                continue;
            }

            if (ReservationConflict.reservationsOverlap(r, reservation)) {
                throw new ServiceException(
                        "Reservation to create conflicts with reservation with ID " + r.getId() + ".",
                        HttpStatus.CONFLICT);
            }
        }
    }
}
