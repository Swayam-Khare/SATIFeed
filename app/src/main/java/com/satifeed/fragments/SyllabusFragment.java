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
import com.satifeed.adapter.SyllabusAdapter;
import com.satifeed.adapter.TimeAdapter;
import com.satifeed.db.entity.Notice;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SyllabusFragment extends Fragment {

    private SyllabusAdapter syllabusAdapter;
    private ArrayList<Notice> notices = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoticeActivity noticeActivity;

    public SyllabusFragment() {
        // Required empty public constructor
    }

    public SyllabusFragment(NoticeActivity noticeActivity) {
        this.noticeActivity = noticeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_syllabus, container, false);

        recyclerView = view.findViewById(R.id.syllabusRecyclerView);

        // Add Time table here
        notices.add(new Notice(0, "CSE 6th Sem", "CSE 6th Sem_1680237141968"));

        syllabusAdapter = new SyllabusAdapter(view.getContext(), notices, noticeActivity);

        // Setup the Recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(syllabusAdapter);

        return view;
    }
}