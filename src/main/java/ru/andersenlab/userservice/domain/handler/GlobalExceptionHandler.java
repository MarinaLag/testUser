package ru.andersenlab.userservice.domain.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andersenlab.userservice.domain.exception.BadRequestEmployeeException;
import ru.andersenlab.userservice.domain.exception.ResourceNotFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestEmployeeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRefreshException(HttpServletRequest request, BadRequestEmployeeException e) {
        log.error("BadRefreshException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.error("AccessDeniedException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("Forbidden")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException e) {
        log.error("ResourceNotFoundException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("Not Found")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(HttpServletRequest request, Exception e) {
        log.error("Exception encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("Internal Server Error")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(getTime())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Value
    @Builder
    public static class ErrorResponse {
        String title;
        String detail;
        String request;
        String time;
    }

    private String getTime() {
        String datePattern = "dd-MM-yyyy HH:mm:ss";
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(datePattern));
    }
}
