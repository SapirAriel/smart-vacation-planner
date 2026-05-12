package com.sapir.smartvacationplanner.exception;

import com.sapir.smartvacationplanner.dto.error.ApiErrorResponse;
import com.sapir.smartvacationplanner.dto.error.FieldErrorItem;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exc,
            HttpServletRequest request
    ) {

        List<FieldErrorItem> fieldErrors = exc.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldErrorItem(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setMessage("Validation failed");
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(Instant.now().toString());
        errorResponse.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
            ResourceNotFoundException exc,
            HttpServletRequest request
    ) {

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exc,
            HttpServletRequest request
    ) {

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleFallbackException(
            Exception exc,
            HttpServletRequest request
    ) {

        log.error("Unhandled exception for path={}", request.getRequestURI(), exc);

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setMessage(exc.getClass().getSimpleName() + ": " + exc.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setTimestamp(Instant.now().toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

