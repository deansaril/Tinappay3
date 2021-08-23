package com.mobdeve.s13.group12.tinappay.checklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;

import java.util.ArrayList;

public class ChecklistActivity extends AppCompatActivity {
    private RecyclerView rvChecklist;
    private LinearLayoutManager llmManager;
    private ArrayList<ChecklistItem> data;
    private ChecklistAdapter checklistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        initRecyclerView();
    }

    private void initRecyclerView () {
        this.rvChecklist = findViewById(R.id.rv_cl);

        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvChecklist.setLayoutManager(this.llmManager);

        data = new ArrayList<ChecklistItem>();
        for (int i = 1; i <= 25; i++) {
            String text = "Checklist item " + i;
            boolean checked = (i % 2 == 0);
            data.add(new ChecklistItem(text, checked));
        }

        this.checklistAdapter = new ChecklistAdapter(this.data);
        this.rvChecklist.setAdapter(checklistAdapter);
    }
}