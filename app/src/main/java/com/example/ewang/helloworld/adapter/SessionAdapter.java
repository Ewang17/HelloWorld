package com.example.ewang.helloworld.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ewang.helloworld.R;
import com.example.ewang.helloworld.SessionActivity;
import com.example.ewang.helloworld.model.Session;
import com.example.ewang.helloworld.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ewang on 2018/4/21.
 */

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<Session> sessionList;

    public SessionAdapter(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View friendView;
        TextView textUsername;
        TextView textLatestTime;
        TextView textLatestMsg;
        TextView textUnread;

        public ViewHolder(View itemView) {
            super(itemView);
            friendView = itemView;
            textUsername = itemView.findViewById(R.id.text_username);
            textLatestMsg = itemView.findViewById(R.id.text_latestMsg);
            textLatestTime = itemView.findViewById(R.id.text_latestTime);
            textUnread = itemView.findViewById(R.id.text_unread);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.friendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SessionActivity.class);
                int position = viewHolder.getAdapterPosition();
                User user = sessionList.get(position).getToUser();
                intent.putExtra("toUserId", user.getId());
                intent.putExtra("toUsername", user.getUsername());
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Session session = sessionList.get(position);
        User user = session.getToUser();
        holder.textUsername.setText(user.getUsername());

        String messageContent = session.getLatestMessage();
        if (messageContent != null && !messageContent.isEmpty()) {
            if (messageContent.length() > 20) {
                messageContent = messageContent.substring(0, 20) + "...";
            }
            holder.textLatestMsg.setText(messageContent);
            int unreadCount = session.getUnread();
            if (unreadCount > 0) {
                holder.textUnread.setText(String.valueOf(unreadCount));
                holder.textUnread.setVisibility(View.VISIBLE);
            }

            Date latestMessageTime = session.getUpdateTime();
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
        return sessionList.size();
    }
}
