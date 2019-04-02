package com.example.caoan.shopmaster.EventBus;

import com.example.caoan.shopmaster.Model.Bill;

public class SucessEvent {
    private Bill bill;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public SucessEvent(Bill bill) {

        this.bill = bill;
    }
}
