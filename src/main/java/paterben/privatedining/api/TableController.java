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
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.Table;
import paterben.privatedining.service.ServiceException;
import paterben.privatedining.service.TableService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "Table controller", description = "The controller used to manage tables.")
public class TableController {
    @Autowired
    private TableService tableService;

    @Autowired
    private ApiConverter converter;

    @GetMapping(path = "/api/restaurants/{restaurantId}/tables")
    @Operation(summary = "List tables for restaurant", description = "Returns the list of tables for the restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<List<ApiTable>> listTablesForRestaurant(@PathVariable("restaurantId") String restaurantId) {
        Optional<List<Table>> tables = tableService.listTablesForRestaurant(restaurantId);
        if (!tables.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<ApiTable> apiTables = tables.get().stream().map(t -> converter.ToApi(t)).toList();
        return ResponseEntity.ok(apiTables);
    }

    @GetMapping(path = "/api/restaurants/{restaurantId}/table/{tableId}")
    @Operation(summary = "Get table by ID", description = "Returns the specific table info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table found"),
            @ApiResponse(responseCode = "404", description = "Table not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ApiTable> getTableForRestaurantById(@PathVariable("restaurantId") String restaurantId,
            @PathVariable("tableId") String tableId) {
        Optional<Table> table = tableService.getTableForRestaurantById(restaurantId, tableId);
        if (!table.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(converter.ToApi(table.get()));
    }

    @PostMapping(path = "/api/restaurants/{restaurantId}/tables", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new table", description = "Creates a new table for a restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Table with same name already exists"),

    })
    public ApiTable createTableForRestaurant(@PathVariable("restaurantId") String restaurantId,
            @RequestBody ApiTable apiTable) {
        Table table = converter.ToCore(apiTable);
        Table newTable = tableService.addTableToRestaurant(restaurantId, table);
        return converter.ToApi(newTable);
    }
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, ServiceException ex) {
        // logger.error("Request: " + req.getRequestURL() + " raised " + ex);

        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }
}
