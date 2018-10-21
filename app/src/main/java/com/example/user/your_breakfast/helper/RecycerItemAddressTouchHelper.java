package com.example.user.your_breakfast.helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.user.your_breakfast.viewholder.AddressViewHolder;

public class RecycerItemAddressTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecycerItemAddressTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        AddressViewHolder addressViewHolder = (AddressViewHolder) viewHolder;

        View foreGroundView = addressViewHolder.foreground;
        getDefaultUIUtil().clearView(foreGroundView);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((AddressViewHolder) viewHolder).foreground;
        getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((AddressViewHolder) viewHolder).foreground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView = ((AddressViewHolder) viewHolder).foreground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }
}
