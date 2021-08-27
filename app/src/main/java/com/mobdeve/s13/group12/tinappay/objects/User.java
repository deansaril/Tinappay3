package com.mobdeve.s13.group12.tinappay.objects;

public class User {
    private String email;
    private String password;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){ return this.email;}

    public String getPassword(){ return this.password;}

}
