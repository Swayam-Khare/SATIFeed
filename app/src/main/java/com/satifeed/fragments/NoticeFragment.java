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
import com.satifeed.db.DatabaseHelper;
import com.satifeed.db.entity.Notice;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment {

    private NoticeAdapter noticeAdapter;
    private final ArrayList<Notice> notices = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private NoticeActivity noticeActivity;

    public NoticeFragment() {
        // Required empty public constructor
    }

    public NoticeFragment(NoticeActivity noticeActivity) {
        this.noticeActivity = noticeActivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        recyclerView = view.findViewById(R.id.noticeRecyclerView);
        dbHelper = new DatabaseHelper(view.getContext());

        notices.addAll(dbHelper.getAllNotices());
        noticeAdapter = new NoticeAdapter(view.getContext(), notices, noticeActivity);

        // Setup the Recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(noticeAdapter);

        return view;
    }


    public void deleteNotice(Notice notice, int position) {
        // Delete the contact from the array list
        notices.remove(notice);

        // Remove the contact from the database
        dbHelper.deleteNotice(notice);

        // Notify that a item is removed from the list
        noticeAdapter.notifyItemRemoved(position);
    }
}