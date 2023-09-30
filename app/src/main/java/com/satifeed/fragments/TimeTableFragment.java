package com.satifeed.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.satifeed.NoticeActivity;
import com.satifeed.R;
import com.satifeed.adapter.NoticeAdapter;
import com.satifeed.adapter.TimeAdapter;
import com.satifeed.db.DatabaseHelper;
import com.satifeed.db.entity.Notice;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableFragment extends Fragment {

    private TimeAdapter timeAdapter;
    private ArrayList<Notice> notices = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoticeActivity noticeActivity;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    public TimeTableFragment(NoticeActivity noticeActivity) {
        this.noticeActivity = noticeActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);

        recyclerView = view.findViewById(R.id.timeRecyclerView);

        // Add Time table here
        notices.add(new Notice(0, "CSE A 6th Sem", "CSE A 6th Sem_1680237141968"));
        notices.add(new Notice(1, "CSE B 6th Sem", "CSE B 6th Sem_1680237141968"));

        timeAdapter = new TimeAdapter(view.getContext(), notices, noticeActivity);

        // Setup the Recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(timeAdapter);

        return view;
    }
}