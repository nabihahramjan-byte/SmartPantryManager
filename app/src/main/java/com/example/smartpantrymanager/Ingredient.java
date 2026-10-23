package com.example.smartpantrymanager;

// Model class: one item in the user's pantry.
public class Ingredient {
    private int id;          // database id (-1 means "not saved yet")
    private String name;
    private double quantity;
    private String unit;     // g, kg, ml, l or pcs
    private String expiry;   // "yyyy-MM-dd" or "" if there is no expiry date

    public Ingredient() {
        id = -1;
        name = "";
        quantity = 0;
        unit = "g";
        expiry = "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }
}
