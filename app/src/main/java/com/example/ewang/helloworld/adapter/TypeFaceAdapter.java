package com.example.ewang.helloworld.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.model.client.CustomTypeFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ewang on 2018/6/8.
 */

public class TypeFaceAdapter extends RecyclerView.Adapter<TypeFaceAdapter.ViewHolder> {

    private List<CustomTypeFace> customTypeFaceList;

    private EditText editTextView;

    private List<ViewHolder> viewHolderList;

    public TypeFaceAdapter(List<CustomTypeFace> customTypeFaceList, EditText editTextView) {
        this.customTypeFaceList = customTypeFaceList;
        this.editTextView = editTextView;
        viewHolderList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.typeface_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolderList.add(viewHolder);
        viewHolder.preLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(viewHolder);
                int position = viewHolder.getAdapterPosition();
                CustomTypeFace customTypeFace = customTypeFaceList.get(position);
                editTextView.setTypeface(customTypeFace.getTypeface());
                editTextView.setText(editTextView.getText() + "");
                editTextView.clearFocus();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomTypeFace customTypeFace = customTypeFaceList.get(position);

        holder.preLook.setText(customTypeFace.getName());
        holder.preLook.setTypeface(customTypeFace.getTypeface());

    }

    @Override
    public int getItemCount() {
        return customTypeFaceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView preLook;

        public ViewHolder(View view) {
            super(view);
            preLook = view.findViewById(R.id.text_typeface_item);
        }
    }

    void setChecked(TypeFaceAdapter.ViewHolder viewHolder) {
        for (TypeFaceAdapter.ViewHolder holder : viewHolderList) {
            TextView v = holder.preLook;
            if (viewHolder == holder) {
                v.setBackgroundColor(v.getResources().getColor(R.color.pink));
                continue;
            }
            v.setBackgroundResource(0);
        }
    }


}
