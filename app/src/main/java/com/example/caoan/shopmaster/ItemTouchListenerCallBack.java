package com.example.caoan.shopmaster;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchListenerCallBack extends ItemTouchHelper.Callback {

    private ItemTouchListener itemTouchListener;

    public ItemTouchListenerCallBack(ItemTouchListener itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int swipeFlag = ItemTouchHelper.RIGHT;
        int moveFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(moveFlag,swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemTouchListener.Move(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchListener.Swipe(viewHolder.getAdapterPosition(),direction);
    }
}
