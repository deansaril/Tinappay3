package com.mobdeve.s13.group12.tinappay.objects;

/**
 * Stores credentials for account authentication
 */
public class User {
    private String email;
    private String password;

    /**
     * Constructor for User
     * @param email the String email of the user
     * @param password the String password of the user
     */
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    /**
     * Returns email of user
     * @return email String of user
     */
    public String getEmail(){ return this.email;}

    /**
     * Returns password of user
     * @return password String of user
     */
    public String getPassword(){ return this.password;}

}
