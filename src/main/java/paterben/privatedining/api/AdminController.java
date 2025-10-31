package paterben.privatedining.api;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.Table;
import paterben.privatedining.service.AdminService;
import paterben.privatedining.service.RestaurantService;
import paterben.privatedining.service.TableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@Tag(name = "Admin controller", description = "Convenience admin actions. Not part of the REST API exposed to users.")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private TableService tableService;

    @PostMapping(path = "/deleteAllData")
    @Operation(summary = "Delete all data", description = "Deletes all data from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data deleted successfully"),
    })
    public void deleteAllData() {
        adminService.deleteAllData();
    }

    @PostMapping(path = "/setupSampleData")
    @Operation(summary = "Setup sample data", description = "Sets up sample restaurants, tables, reservations and diners.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data setup successfully"),
    })
    public void setupSampleData() {
        Restaurant restaurant1 = new Restaurant("Bob's Pizza", "1234 Bob Ave, WA, USA", "USD");
        Restaurant restaurant2 = new Restaurant("Restaurant Vietnamien",
                "3 rue de Becon, 92600 Asnieres-sur-Seine, France", "EUR");
        Restaurant restaurant3 = new Restaurant("Chez P. Sherman",
                "42 Wallaby Way, Sydney, Australia", "AUD");

        restaurantService.createRestaurant(restaurant1);
        restaurantService.createRestaurant(restaurant2);
        restaurantService.createRestaurant(restaurant3);

        Table table1_1 = new Table("Bob's Private Room");
        Table table1_2 = new Table("Bob's Not-So-Private Room");
        Table table2_1 = new Table("Salle privee");

        tableService.addTableToRestaurant(restaurant1.getId(), table1_1);
        tableService.addTableToRestaurant(restaurant1.getId(), table1_2);
        tableService.addTableToRestaurant(restaurant2.getId(), table2_1);
    }
}
