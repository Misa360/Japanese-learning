package com.jlearn.model;

import java.sql.Timestamp;

/** Du lieu toi thieu cho bang quan ly tai khoan admin, khong mang theo password hash. */
public class AdminUserRow {
    private long id;
    private String username;
    private String fullName;
    private String email;
    private String roleName;
    private String status;
    private long coinBalance;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;

    public long getId(){return id;} public void setId(long v){id=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getRoleName(){return roleName;} public void setRoleName(String v){roleName=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public long getCoinBalance(){return coinBalance;} public void setCoinBalance(long v){coinBalance=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
    public Timestamp getLastLoginAt(){return lastLoginAt;} public void setLastLoginAt(Timestamp v){lastLoginAt=v;}
}
