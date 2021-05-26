package com.example.inicorona;

public class Hospital {
    private String name;
    private String address;
    private String phone;
    private String region;

    public Hospital(String name, String address, String phone, String region) {
        this.name = name;
        this.address = address;

        if(phone.equals("null"))
            this.phone = "-";
        else
            this.phone = phone;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getRegion() {
        return region;
    }
}
