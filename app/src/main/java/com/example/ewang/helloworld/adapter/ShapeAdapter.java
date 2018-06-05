package com.example.ewang.helloworld.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.constants.PaintGraphics;
import com.example.ewang.helloworld.helper.PopupPencilWindowHelper;
import com.example.ewang.helloworld.model.client.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ewang on 2018/6/4.
 */

public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {
    private List<Shape> shapeList;
    PopupPencilWindowHelper popupPencilWindowHelper;
    private List<ViewHolder> viewHolderList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View shapeView;
        ImageView shapeImage;

        public ViewHolder(View view) {
            //这个view通常是RecyclerView子项的最外层布局
            super(view);
            shapeView = view;
            shapeImage = view.findViewById(R.id.shape_item_image);
        }
    }

    public ShapeAdapter(List<Shape> shapeList, PopupPencilWindowHelper popupPencilWindowHelper) {
        this.shapeList = shapeList;
        this.popupPencilWindowHelper = popupPencilWindowHelper;
        viewHolderList = new ArrayList<>();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.shape_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        viewHolderList.add(holder);
        holder.shapeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setChecked(holder);
                int position = holder.getAdapterPosition();
                Shape shape = shapeList.get(position);
                if (shape.getShapeId() == PaintGraphics.DRAW_CIRCLE.getValue()) {
                    popupPencilWindowHelper.setCurrentPaintGraphics(PaintGraphics.DRAW_CIRCLE);
                } else if (shape.getShapeId() == PaintGraphics.DRAW_RECTANGLE.getValue()) {
                    popupPencilWindowHelper.setCurrentPaintGraphics(PaintGraphics.DRAW_RECTANGLE);
                } else if (shape.getShapeId() == PaintGraphics.DRAW_ARROW.getValue()) {
                    popupPencilWindowHelper.setCurrentPaintGraphics(PaintGraphics.DRAW_ARROW);
                } else if (shape.getShapeId() == PaintGraphics.DRAW_TRIANGLE.getValue()) {
                    popupPencilWindowHelper.setCurrentPaintGraphics(PaintGraphics.DRAW_TRIANGLE);
                }
            }
        });

        return holder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Shape shape = shapeList.get(position);
        holder.shapeImage.setImageResource(shape.getShapeImage());
        if (popupPencilWindowHelper.getCurrentPaintGraphics().getValue() == shape.getShapeId()) {
            holder.shapeView.setBackgroundColor(holder.shapeView.getResources().getColor(R.color.pink));
        }
    }

    //用于返回一共有多少子项
    public int getItemCount() {
        return shapeList.size();
    }

    void setChecked(ViewHolder viewHolder) {
        for (ViewHolder holder : viewHolderList) {
            View v = holder.shapeView;
            if (viewHolder == holder) {
                v.setBackgroundColor(v.getResources().getColor(R.color.pink));
                continue;
            }
            v.setBackgroundResource(0);
        }
    }

}
