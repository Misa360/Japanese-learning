package com.jlearn.model;

public class LoginResult {
    private String status;
    private User user;
    private long retryAfterSeconds;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public long getRetryAfterSeconds() { return retryAfterSeconds; }
    public void setRetryAfterSeconds(long retryAfterSeconds) { this.retryAfterSeconds = retryAfterSeconds; }

    public static final String OK = "OK";
    public static final String BAD_CREDENTIALS = "BAD_CREDENTIALS";
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String TEMP_LOCKED = "TEMP_LOCKED";

    public static LoginResult of(String status, User user, long retryAfterSeconds) {
        LoginResult r = new LoginResult();
        r.status = status; r.user = user; r.retryAfterSeconds = retryAfterSeconds;
        return r;
    }
}
