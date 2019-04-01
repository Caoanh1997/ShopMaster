package com.example.caoan.shopmaster.EventBus;

public class DeleteProductEvent {

    private String key_store;

    public DeleteProductEvent(String key_store) {
        this.key_store = key_store;
    }

    public String getKey_store() {
        return key_store;
    }

    public void setKey_store(String key_store) {
        this.key_store = key_store;
    }

    public DeleteProductEvent() {
    }
}
