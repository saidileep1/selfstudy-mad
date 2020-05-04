package com.example.selfstudy_mad.Model;

public class User {

    private String name;
    private String password;
    private String Phone;
    private String admin;
    private String homeaddress;

    public User(){
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.admin="false";
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
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

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;
    }
}
