package com.jlearn.exception;

/** Loi nghiep vu co ma HTTP kem theo (400/401/403/404). BaseServlet bat va chuyen thanh trang loi hoac JSON. */
public class AppException extends RuntimeException {
    private final int status;

    public AppException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
