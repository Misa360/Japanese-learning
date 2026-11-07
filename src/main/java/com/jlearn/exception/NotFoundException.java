package com.jlearn.exception;

public class NotFoundException extends AppException {
    public NotFoundException() { super(404, "Không tìm thấy nội dung yêu cầu"); }
    public NotFoundException(String message) { super(404, message); }
}
