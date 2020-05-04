package com.example.selfstudy_mad.Model;

public class Food1 {
    private String name;
    private String image;
    private String price;
    private String veg;
    private String menuid;

    public Food1() {
    }

    public Food1(String name, String image, String price, String veg,String category) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.veg = veg;
        this.menuid=menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVeg() {
        return veg;
    }

    public void setVeg(String veg) {
        this.veg = veg;
    }


    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }


}

