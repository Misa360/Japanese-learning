package com.jlearn.model;

public class EligibilityResult {
    private boolean ok;
    private String code;
    private String reason;
    private long cost;

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public long getCost() { return cost; }
    public void setCost(long cost) { this.cost = cost; }

    public static final String OK = "OK";
    public static final String ALREADY_ENROLLED = "ALREADY_ENROLLED";
    public static final String NOT_AVAILABLE = "NOT_AVAILABLE";
    public static final String REQUIRED_COURSE = "REQUIRED_COURSE";
    public static final String NOT_ENOUGH_COINS = "NOT_ENOUGH_COINS";

    public static EligibilityResult of(boolean ok, String code, String reason, long cost) {
        EligibilityResult r = new EligibilityResult();
        r.ok = ok; r.code = code; r.reason = reason; r.cost = cost;
        return r;
    }
}
