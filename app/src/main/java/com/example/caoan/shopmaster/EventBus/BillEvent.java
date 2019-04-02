package com.example.caoan.shopmaster.EventBus;

import com.example.caoan.shopmaster.Model.Bill;

public class BillEvent {

    private Bill bill;

    public BillEvent(Bill bill) {
        this.bill = bill;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
