package paterben.privatedining.api;

import org.springframework.http.HttpStatusCode;

public class ApiException extends RuntimeException {
    private HttpStatusCode httpStatusCode;

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public ApiException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}