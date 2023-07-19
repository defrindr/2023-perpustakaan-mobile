package com.example.perpusmini.models;

import com.example.perpusmini.enums.Role;

public class User {
    public String email, username, noHp = "-";
        public Role role;

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
