package paterben.privatedining.service;

import org.springframework.http.HttpStatusCode;

public class RestaurantServiceException extends RuntimeException {
    private HttpStatusCode httpStatusCode;

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public RestaurantServiceException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
