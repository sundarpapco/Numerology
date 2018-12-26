package com.papco.sundar.numerology;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    int dragFrom=-1;
    int dragTo=-1;
    DragCallBack callBack;

    public ItemTouchHelperCallBack(DragCallBack callBack){
        this.callBack=callBack;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags=ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags=ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        if(dragFrom==-1)
            dragFrom=viewHolder.getAdapterPosition();

        dragTo=target.getAdapterPosition();

        if(callBack!=null)
            callBack.onDragging(viewHolder.getAdapterPosition(),target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(callBack!=null)
            callBack.onSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        //if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
        if(viewHolder!=null)
            viewHolder.itemView.setActivated(true);
        //}
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setActivated(false);

        if(dragFrom!=-1 && dragTo!=-1&& dragFrom!=dragTo)
            if(callBack!=null)
                callBack.onMoved(dragFrom,dragTo);

        dragFrom=dragTo=-1;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public static interface DragCallBack{

        public void onDragging(int fromPosition,int toPosition);
        public void onMoved(int fromPosition,int toPosition);
        public void onSwiped(int position);

    }

}
