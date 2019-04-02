package com.example.caoan.shopmaster.EventBus;

import android.support.v4.app.Fragment;

public class LoadEvent {

    private boolean isLoad =false;
    private Fragment fragment;

    public LoadEvent(boolean isLoad, Fragment fragment) {
        this.isLoad = isLoad;
        this.fragment = fragment;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
