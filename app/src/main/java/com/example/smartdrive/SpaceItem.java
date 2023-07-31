package com.example.smartdrive;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class SpaceItem extends DrawerItem<SpaceItem.ViewHolder> {

    private final int spaceDp;
    public SpaceItem(int spaceDp){
        this.spaceDp = spaceDp;

    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {

        Context c = parent.getContext();
        View view = new View(c);

        int height = (int) (c.getResources().getDisplayMetrics().density * spaceDp);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));

    return new ViewHolder(view);


    }


    @Override
    public void bindViewHolder(ViewHolder holder) {

    }

    public  class ViewHolder extends DrawerAdapter.ViewHolder{


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }



}
