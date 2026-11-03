package com.jlearn.model;

public class User {
    private long id;
    private long roleId;
    private String roleName;
    private String username;
    private String fullName;
    private String email;
    private String passwordHash;
    private String avatarPath;
    private String status;
    private Long targetLevelId;
    private long coinBalance;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRoleId() { return roleId; }
    public void setRoleId(long roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTargetLevelId() { return targetLevelId; }
    public void setTargetLevelId(Long targetLevelId) { this.targetLevelId = targetLevelId; }
    public long getCoinBalance() { return coinBalance; }
    public void setCoinBalance(long coinBalance) { this.coinBalance = coinBalance; }

    public boolean isActive() { return "active".equals(status); }
    public boolean isAdmin() { return "admin".equalsIgnoreCase(roleName); }
}
