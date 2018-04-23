package com.example.ewang.helloworld.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.model.Message;
import com.example.ewang.helloworld.model.Msg;
import com.example.ewang.helloworld.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ewang on 2018/4/21.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<User> userList;

    private Map<Long, Message> latestMessageMap;

    public FriendAdapter(List<User> userList, Map<Long, Message> latestMessageMap) {
        this.userList = userList;
        this.latestMessageMap = latestMessageMap;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textUsername;
        TextView textLatestTime;
        TextView textLatestMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textLatestMsg = itemView.findViewById(R.id.text_latestMsg);
            textLatestTime = itemView.findViewById(R.id.text_latestTime);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textUsername.setText(user.getUsername());

        Message message = latestMessageMap.get(user.getId());
        if (message != null) {
            String content = message.getContent();
            if (content.length() > 40) {
                content = content.substring(0, 40) + "...";
            }
            holder.textLatestMsg.setText(content);

            Date latestMessageTime = message.getCreateTime();
            long today = new Date().getTime();

            String datePattern = "HH:mm";
            //当天的时间显示 HH:mm
            if (Math.abs(latestMessageTime.getTime() - today) <= 24 * 60 * 60 * 1000) {
                datePattern = "HH:mm";
            } else {
                datePattern = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            holder.textLatestTime.setText(sdf.format(latestMessageTime));
        } else {
            holder.textLatestMsg.setText("还没聊过天");
            holder.textLatestTime.setText("没有聊天时间");
        }


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
