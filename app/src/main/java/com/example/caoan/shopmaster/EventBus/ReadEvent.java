package com.example.caoan.shopmaster.EventBus;

import android.support.v4.app.Fragment;

public class ReadEvent {
    private boolean isRead;
    private Fragment fragment;

    public ReadEvent(boolean isRead, Fragment fragment) {
        this.isRead = isRead;
        this.fragment = fragment;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
