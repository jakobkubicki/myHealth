/**
 * This program is a watch list for any type of video content
 * CPSC 312-01, Fall 2021
 * Programming Assignment #6
 * No sources to cite.
 *
 * @author Jakob Kubicki
 * @version v5.0 11/10/21
 */
package com.jklt.myHealth;

public class Drug {

    private String name;
    private String description;
    private String manufacturer;
    private int id;

    public Drug(int id, String title, String desc, String manu) {
        this.id = id;
        this.name = title;
        this.description = desc;
        this.manufacturer = manu;
    }

    public Drug(String title, String paramType, String manu) {
        this.id = -1;
        this.name = title;
        this.description = paramType;
        this.manufacturer = manu;
    }

    public Drug() {
        id = -1;
        name = "";
        description = "";
        manufacturer = "";
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String newTitle) { name = newTitle; }

    public String getDescription() {
        return description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setDescription(String newType) { description = newType; }

    public int getID() {
        return id;
    }

    public void setID(int newID) { id = newID; }

}
