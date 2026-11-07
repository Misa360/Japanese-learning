package com.jlearn.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException() { super(403, "Bạn không có quyền thực hiện thao tác này"); }
    public ForbiddenException(String message) { super(403, message); }
}
