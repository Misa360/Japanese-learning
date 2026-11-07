package com.jlearn.model;

public class EnrollResult {
    private String status;
    private String message;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static final String ENROLLED = "ENROLLED";
    public static final String ALREADY_ENROLLED = "ALREADY_ENROLLED";
    public static final String BLOCKED = "BLOCKED";

    public static EnrollResult of(String status, String message) {
        EnrollResult r = new EnrollResult();
        r.status = status; r.message = message;
        return r;
    }
}
