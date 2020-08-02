package com.example.uley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lists extends AppCompatActivity {

    private RecyclerView myTaskList;

    private DatabaseReference myRef,refToTasks;
    private FirebaseAuth mAuth;
    private final Integer MAX_NUM_OF_TASKS = 10;

    private ArrayList<Tasks> tasksList;
    private RecyclerAdapter recyclerAdapter;
    private Button addNewTaskBtn;
    private EditText textNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAuth = FirebaseAuth.getInstance();

        myTaskList = findViewById(R.id.list_tasks);
        addNewTaskBtn = findViewById(R.id.btn_new_task);
        textNewTask = findViewById(R.id.new_task);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myTaskList.setLayoutManager(layoutManager);
        myTaskList.setHasFixedSize(true);

        myRef = FirebaseDatabase.getInstance().getReference();


        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();



        tasksList = new ArrayList<>();

        ClearAllList();

        GetDataFromFirebase();

        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerAdapter.getItemCount() >= MAX_NUM_OF_TASKS){
                    Toast.makeText(Lists.this, "Максимальная длинна списка 10", Toast.LENGTH_LONG).show();
                } else {
                String taskToBase = textNewTask.getText().toString();
                refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(taskToBase);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Task",taskToBase);
                refToTasks.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Lists.this, "Добавлено", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                textNewTask.getText().clear();
            }}
        });

    }

    private void GetDataFromFirebase() {

        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        String userId = firebaseuser.getUid();
        Query query = myRef.child("Users").child(userId);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ClearAllList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tasks tasks = new Tasks();

                    tasks.setName(snapshot.child("Task").getValue().toString());

                    tasksList.add(tasks);
                }

                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), tasksList);
                myTaskList.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ClearAllList(){
        if (tasksList != null){
            tasksList.clear();

            if (recyclerAdapter != null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }

        tasksList = new ArrayList<>();
    }


}
