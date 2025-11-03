package paterben.privatedining.api;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import paterben.privatedining.api.model.ApiErrorInfo;
import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.core.model.Table;
import paterben.privatedining.service.AdminService;
import paterben.privatedining.service.DinerService;
import paterben.privatedining.service.ReservationService;
import paterben.privatedining.service.RestaurantService;
import paterben.privatedining.service.ServiceException;
import paterben.privatedining.service.TableService;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@Tag(name = "Admin controller", description = "Convenience admin actions. Not part of the REST API exposed to users.")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private DinerService dinerService;

    @Autowired
    private TableService tableService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private Clock clock;

    @PostMapping(path = "/admin/deleteAllData")
    @Operation(summary = "Delete all data", description = "Deletes all data from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data deleted successfully"),
    })
    public void deleteAllData() {
        adminService.deleteAllData();
    }

    @PostMapping(path = "/admin/setupSampleData")
    @Operation(summary = "Setup sample data", description = "Sets up sample restaurants, tables, reservations and diners.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data setup successfully"),
    })
    public void setupSampleData() {
        Restaurant restaurant1 = new Restaurant("Bob's Pizza", "1234 Bob Ave, WA, USA", "bobspizza@example.com", "USD");
        Restaurant restaurant2 = new Restaurant("Restaurant Vietnamien",
                "3 rue de Becon, 92600 Asnieres-sur-Seine, France", "restauvietnam@example.com", "EUR");
        Restaurant restaurant3 = new Restaurant("Chez P. Sherman",
                "42 Wallaby Way, Sydney, Australia", "chezpsherman@example.com", "AUD");

        restaurantService.createRestaurant(restaurant1);
        restaurantService.createRestaurant(restaurant2);
        restaurantService.createRestaurant(restaurant3);

        Table table1_1 = new Table("Bob's Private Room", 1, 5, RoomType.PRIVATE_ROOM, 100);
        Table table1_2 = new Table("Bob's Not-So-Private Room", 0, 10, RoomType.HALL, 0);
        Table table2_1 = new Table("Rooftop prive", 4, 20, RoomType.ROOFTOP, 200);

        tableService.addTableToRestaurant(restaurant1.getId(), table1_1);
        tableService.addTableToRestaurant(restaurant1.getId(), table1_2);
        tableService.addTableToRestaurant(restaurant2.getId(), table2_1);

        Diner diner1 = new Diner("Alice", "alice@example.com");
        Diner diner2 = new Diner("Bob", "bob@example.com");
        Diner diner3 = new Diner("Charlie", "charlie@example.com");

        dinerService.createDiner(diner1);
        dinerService.createDiner(diner2);
        dinerService.createDiner(diner3);

        Instant now = Instant.now(clock);

        Reservation reservation1_1_1 = new Reservation(diner1.getId(),
                "Alice and friends - cancelled", 5,
                now.plus(1, ChronoUnit.HOURS),
                now.plus(3, ChronoUnit.HOURS));
        Reservation reservation1_1_2 = new Reservation(diner1.getId(),
                "Alice and friends", 4,
                now.plus(2, ChronoUnit.HOURS),
                now.plus(4, ChronoUnit.HOURS));
        Reservation reservation1_1_3 = new Reservation(diner2.getId(),
                "Bobby", 1,
                now.plus(4, ChronoUnit.HOURS),
                now.plus(5, ChronoUnit.HOURS));
        Reservation reservation2_1_1 = new Reservation(diner2.getId(),
                "Bobby's friend", 6,
                now.plus(4, ChronoUnit.HOURS),
                now.plus(5, ChronoUnit.HOURS));

        reservationService.createReservationForRestaurantAndTable(restaurant1.getId(), table1_1.getId(),
                reservation1_1_1);
        reservation1_1_1.setIsCancelled(true);
        reservationService.updateReservationForRestaurantAndTable(restaurant1.getId(), table1_1.getId(),
                reservation1_1_1.getId(), reservation1_1_1);
        reservationService.createReservationForRestaurantAndTable(restaurant1.getId(), table1_1.getId(),
                reservation1_1_2);
        reservationService.createReservationForRestaurantAndTable(restaurant1.getId(), table1_1.getId(),
                reservation1_1_3);
        reservationService.createReservationForRestaurantAndTable(restaurant2.getId(), table2_1.getId(),
                reservation2_1_1);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, ServiceException ex) {
        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }
}
