package com.jlearn.exception;

public class BusinessRuleException extends AppException {
    public BusinessRuleException() { super(400, "Thao tác không hợp lệ"); }
    public BusinessRuleException(String message) { super(400, message); }
}
