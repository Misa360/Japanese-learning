package com.jlearn.exception;

public class ValidationException extends AppException {
    public ValidationException() { super(400, "Dữ liệu không hợp lệ"); }
    public ValidationException(String message) { super(400, message); }
}
