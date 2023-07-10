package com.example.perpusmini.models;

import com.example.perpusmini.enums.Role;

public class Peminjam extends User {
    public String username,email, noHp, alamat;
    public Role role;

    public Peminjam() {}

    public Peminjam(String username, String email, Role role, String noHp, String alamat) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
