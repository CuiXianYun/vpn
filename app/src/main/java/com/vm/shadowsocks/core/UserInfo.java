package com.vm.shadowsocks.core;

public class UserInfo {

    private String name;
    private String password;
    private String level;
    private String token;

    public UserInfo() {
    }

    public UserInfo(String name, String password, String level, String token) {
        this.name = name;
        this.password = password;
        this.level = level;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
