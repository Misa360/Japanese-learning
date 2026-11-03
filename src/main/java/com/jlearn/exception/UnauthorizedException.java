package com.jlearn.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException() { super(401, "Bạn cần đăng nhập"); }
    public UnauthorizedException(String message) { super(401, message); }
}
