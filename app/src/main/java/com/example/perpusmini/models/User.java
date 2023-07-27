package com.example.perpusmini.models;

import com.example.perpusmini.enums.Role;

public class User {
    public String email, username, noHp = "-", fotoKtp = "";
    int status = 0;
        public Role role;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getFotoKtp() {
        return fotoKtp;
    }

    public void setFotoKtp(String fotoKtp) {
        this.fotoKtp = fotoKtp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNoHp() {
        return noHp;
    }

    public  String getEmail() {
        return this.email;
    }
    public  String getUsername() {
        return this.username;
    }
    public  Role getRole() {
        return this.role;
    }
}
