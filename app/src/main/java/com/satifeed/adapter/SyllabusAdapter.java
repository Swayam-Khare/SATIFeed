package com.satifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.satifeed.NoticeActivity;
import com.satifeed.R;
import com.satifeed.db.entity.Notice;
import com.satifeed.utils.DataUtil;

import java.util.ArrayList;

public class SyllabusAdapter extends RecyclerView.Adapter<SyllabusAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<Notice> notices;
    private final NoticeActivity noticeActivity;

    public SyllabusAdapter(Context context, ArrayList<Notice> notices, NoticeActivity noticeActivity) {
        this.context = context;
        this.notices = notices;
        this.noticeActivity = noticeActivity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView date;
        public TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.noticeName);
            this.date = itemView.findViewById(R.id.dateText);
            this.time = itemView.findViewById(R.id.timeText);
        }
    }

    @NonNull
    @Override
    public SyllabusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SyllabusAdapter.MyViewHolder holder, int position) {
        final Notice notice = notices.get(position);

        holder.name.setText(notice.getName());
        
        holder.date.setText(DataUtil.formatDate(notice.getFile()));
        holder.time.setText(DataUtil.formatTime(notice.getFile()));

        holder.itemView.setOnClickListener(v -> noticeActivity.handleTimeClick(notice));
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }
}
