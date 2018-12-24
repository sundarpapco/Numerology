package com.papco.sundar.numerology;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    int topSpacing,bottomSpacing;
    Context context;

    public SpacingDecoration(Context context,int topSpacing, int bottomSpacing){
        this.context=context;
        this.topSpacing=topSpacing;
        this.bottomSpacing=bottomSpacing;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        if(parent.getChildAdapterPosition(view)==0) {
            if(topSpacing<=0)
                super.getItemOffsets(outRect, view, parent, state);
            else
                outRect.top=getPixelValue(topSpacing);
            return;
        }

        if(parent.getChildAdapterPosition(view)==state.getItemCount()-1) {

            if(bottomSpacing<=0)
                super.getItemOffsets(outRect, view, parent, state);
            else
                outRect.bottom=getPixelValue(bottomSpacing);

            return;
        }

        super.getItemOffsets(outRect, view, parent, state);
    }

    private int getPixelValue(int Dp){

        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,Dp,context.getResources().getDisplayMetrics());

    }
}
