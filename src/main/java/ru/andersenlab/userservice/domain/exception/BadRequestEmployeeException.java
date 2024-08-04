package ru.andersenlab.userservice.domain.exception;

public class BadRequestEmployeeException extends RuntimeException {
    public static final String EMPLOYEE_BAD_REQUEST = "Некорректный запрос";
    public BadRequestEmployeeException() {
        super(EMPLOYEE_BAD_REQUEST);
    }
}
