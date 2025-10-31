package paterben.privatedining.service;

import org.springframework.http.HttpStatusCode;

public class ServiceException extends RuntimeException {
    private HttpStatusCode httpStatusCode;

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public ServiceException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
