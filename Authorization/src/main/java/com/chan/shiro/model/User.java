package com.chan.shiro.model;

/**
 * User
 *
 * @author Chan
 * @since 2021/1/13
 */
public class User {
    private String username;
    private String password;
    private String salt;
    /**
     * 0-正常，1-禁止，2-封号
     */
    private int status;

    public User(String username, String password, String salt, int status) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
