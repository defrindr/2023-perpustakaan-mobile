package com.example.perpusmini.models;

import com.example.perpusmini.enums.Role;

public class User {
    public String email, username;
        public Role role;


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
