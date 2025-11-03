package paterben.privatedining.api;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import paterben.privatedining.api.conversion.ApiConverter;
import paterben.privatedining.api.model.ApiErrorInfo;
import paterben.privatedining.api.model.ApiReservation;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.service.ReservationService;
import paterben.privatedining.service.ServiceException;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "Reservation controller", description = "The controller used to manage reservations.")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ApiConverter converter;

    @GetMapping(path = "/api/restaurants/{restaurantId}/tables/{tableId}/reservations")
    @Operation(summary = "List reservations for table", description = "Returns the list of reservations for the table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table found"),
            @ApiResponse(responseCode = "404", description = "Table not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<List<ApiReservation>> listReservationsForRestaurantAndTable(
            @PathVariable("restaurantId") String restaurantId, @PathVariable("tableId") String tableId) {
        Optional<List<Reservation>> reservations = reservationService
                .listReservationsForRestaurantAndTable(restaurantId, tableId);
        if (!reservations.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ApiReservation> apiReservations = reservations.get().stream().map(t -> converter.toApi(t)).toList();
        return ResponseEntity.ok(apiReservations);
    }

    @GetMapping(path = "/api/restaurants/{restaurantId}/tables/{tableId}/reservations/{reservationId}")
    @Operation(summary = "Get reservation for table by ID", description = "Returns the specific reservation info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation found"),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ApiReservation> getReservationForRestaurantAndTableById(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("tableId") String tableId, @PathVariable("reservationId") String reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationForRestaurantAndTableById(restaurantId,
                tableId, reservationId);
        if (!reservation.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(converter.toApi(reservation.get()));
    }

    @PostMapping(path = "/api/restaurants/{restaurantId}/tables/{tableId}/reservations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new reservation", description = "Creates a new reservation for a table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Reservation schedule conflict"),

    })
    public ApiReservation createReservationForRestaurantAndTable(@PathVariable("restaurantId") String restaurantId,
            @PathVariable("tableId") String tableId,
            @RequestBody ApiReservation apiReservation) {
        Reservation reservation = converter.toCore(apiReservation);
        Reservation newReservation = reservationService.createReservationForRestaurantAndTable(restaurantId, tableId,
                reservation);
        return converter.toApi(newReservation);
    }

    @PatchMapping(path = "/api/restaurants/{restaurantId}/tables/{tableId}/reservations/{reservationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a reservation", description = "Updates an existing reservation. Currently only cancellation is supported by setting `isCancelled` to true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "412", description = "Reservation cannot be updated, e.g. already cancelled."),

    })
    public ApiReservation updateReservationForRestaurantAndTable(@PathVariable("restaurantId") String restaurantId,
            @PathVariable("tableId") String tableId, @PathVariable("reservationId") String reservationId,
            @RequestBody ApiReservation apiReservation) {
        Reservation reservation = converter.toCore(apiReservation);
        Reservation newReservation = reservationService.updateReservationForRestaurantAndTable(restaurantId, tableId,
                reservationId, reservation);
        return converter.toApi(newReservation);
    }

    @GetMapping(path = "/api/diners/{dinerId}/reservations")
    @Operation(summary = "List reservations for diner", description = "Returns the list of reservations for the diner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diner found"),
            @ApiResponse(responseCode = "404", description = "Diner not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<List<ApiReservation>> listReservationsForDiner(
            @PathVariable("dinerId") String dinerId) {
        Optional<List<Reservation>> reservations = reservationService
                .listReservationsForDiner(dinerId);
        if (!reservations.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ApiReservation> apiReservations = reservations.get().stream().map(t -> converter.toApi(t)).toList();
        return ResponseEntity.ok(apiReservations);
    }

    @GetMapping(path = "/api/diners/{dinerId}/reservations/{reservationId}")
    @Operation(summary = "Get reservation for diner by ID", description = "Returns the specific reservation info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation found"),
            @ApiResponse(responseCode = "404", description = "Reservation not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ApiReservation> getReservationForDinerById(@PathVariable("dinerId") String dinerId,
            @PathVariable("reservationId") String reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationForDinerById(dinerId, reservationId);
        if (!reservation.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(converter.toApi(reservation.get()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, ServiceException ex) {
        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }
}
