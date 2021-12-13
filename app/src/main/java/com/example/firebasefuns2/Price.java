package com.example.firebasefuns2;

public class Price {
    int id;
    String seller;
    Double price;
    String website;
    String drug;

    //DVC
    public Price(){
        id = -1;
        seller= "";
        price= 0.0;
        drug = "";
        website = "";
    }

    //EVC
    public Price (int id, String seller, Double price, String website, String drug){
        this.id = id;
        this.seller = seller;
        this.price = price;
        this.website = website;
        this.drug = drug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {
        this.drug = drug;
    }
}
