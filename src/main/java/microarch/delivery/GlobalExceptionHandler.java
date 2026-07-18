package microarch.delivery;

import libs.errs.DomainInvariantException;
import microarch.delivery.adapters.in.http.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PREFIX = "Domain invariant violated: ";

    @ExceptionHandler(DomainInvariantException.class)
    public ResponseEntity<Error> handleDomainInvariant(DomainInvariantException exception) {
        var message = cleanMessage(exception);
        var status = getStatus(message);
        var error = new Error(status.value(), message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnexpected(Exception exception) {
        LOGGER.error("Unexpected error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
    }

    private HttpStatus getStatus(String message) {
        if (message.contains("invalid") && message.contains("status")) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private String cleanMessage(DomainInvariantException exception) {
        var message = exception.getMessage();
        if (message != null && message.startsWith(PREFIX)) {
            return message.substring(PREFIX.length());
        }
        return message;
    }
}