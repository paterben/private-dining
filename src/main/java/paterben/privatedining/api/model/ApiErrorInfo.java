package paterben.privatedining.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error information.")
public class ApiErrorInfo {
    @Schema(description = "Message describing the error.")
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
