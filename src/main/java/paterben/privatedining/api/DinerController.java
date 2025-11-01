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
import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.api.model.ApiErrorInfo;
import paterben.privatedining.core.model.Diner;
import paterben.privatedining.service.DinerService;
import paterben.privatedining.service.ServiceException;

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
@Tag(name = "Diner controller", description = "The controller used to manage diners.")
public class DinerController {
    @Autowired
    private DinerService dinerService;

    @Autowired
    private ApiConverter converter;

    @GetMapping(path = "/api/diners")
    @Operation(summary = "List diners", description = "Returns the list of diners.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
    })
    public ResponseEntity<List<ApiDiner>> listDiners() {
        List<Diner> diners = dinerService.listDiners();
        List<ApiDiner> apiDiners = diners.stream().map(d -> converter.toApi(d)).toList();
        return ResponseEntity.ok(apiDiners);
    }

    @GetMapping(path = "/api/diners/{dinerId}")
    @Operation(summary = "Get diner by ID", description = "Returns the specific diner info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diner found"),
            @ApiResponse(responseCode = "404", description = "Diner not found", content = @Content(schema = @Schema()))
    })
    public ResponseEntity<ApiDiner> getDinerById(@PathVariable("dinerId") String dinerId) {
        Optional<Diner> diner = dinerService.getDinerById(dinerId);
        if (!diner.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(converter.toApi(diner.get()));
    }

    @PostMapping(path = "/api/diners", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new diner", description = "Creates a new diner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diner created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema()))
    })
    public ApiDiner createDiner(@RequestBody ApiDiner apiDiner) {
        Diner diner = converter.toCore(apiDiner);
        Diner newDiner = dinerService.createDiner(diner);
        return converter.toApi(newDiner);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorInfo> handleError(HttpServletRequest req, ServiceException ex) {
        // logger.error("Request: " + req.getRequestURL() + " raised " + ex);

        ApiErrorInfo info = new ApiErrorInfo();
        info.setErrorMessage(ex.getLocalizedMessage());
        return new ResponseEntity<ApiErrorInfo>(info, ex.getHttpStatusCode());
    }
}
