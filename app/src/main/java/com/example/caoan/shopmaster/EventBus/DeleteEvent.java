package com.example.caoan.shopmaster.EventBus;

import com.example.caoan.shopmaster.Model.Bill;

public class DeleteEvent {

    private Bill bill;

    public DeleteEvent(Bill bill) {
        this.bill = bill;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
