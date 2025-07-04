/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class SaleTransaction {
    private int id;
    private String customerId;
    private String cashierName;
    private LocalDate date;
    private LocalTime time;
    private List<SaleItem> saleItems;

 public SaleTransaction(int id, String customerId, String cashierName, LocalDate date, LocalTime time, List<SaleItem> saleItems) {
        this.id = id;
        this.customerId = customerId;
        this.cashierName = cashierName;
        this.date = date;
        this.time = time;
        this.saleItems = saleItems;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }

    public double getTotalPrice() {
        double total = 0;
        for (SaleItem item : saleItems) {
            total += item.getSubTotal();
        }
        return total;
    }
    
}

