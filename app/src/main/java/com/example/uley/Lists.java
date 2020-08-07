package com.example.uley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private LinearLayout colorImportant;

    private DatabaseReference myRef,refToTasks;
    private FirebaseAuth mAuth;
    private final Integer MAX_NUM_OF_TASKS = 10;

    private ArrayList<Tasks> tasksList;
    private RecyclerAdapter recyclerAdapter;
    private Button addNewTaskBtn;
    private EditText textNewTask;
    private ImageView openInputGroupBtn;
    private ConstraintLayout inputGroup;
    private Toolbar mToolBar;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mAuth = FirebaseAuth.getInstance();

        mToolBar = findViewById(R.id.tasks_list_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Улей");
        myTaskList = findViewById(R.id.list_tasks);
        addNewTaskBtn = findViewById(R.id.btn_new_task);
        textNewTask = findViewById(R.id.new_task);
        openInputGroupBtn = findViewById(R.id.open_input_module);
        inputGroup = findViewById(R.id.input_group);

        openInputGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Видимость вводимой группы и открытие клавиатуры
                inputGroup.setVisibility(View.VISIBLE);
                openInputGroupBtn.setVisibility(View.GONE);
                textNewTask.requestFocus();
                textNewTask.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textNewTask, InputMethodManager.SHOW_IMPLICIT);
            }
        });

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
                String taskToBase = textNewTask.getText().toString();
                //Проверка на совпадение введеного имени и существующих
                for (int i=0; i<tasksList.size();i++){
                    if (tasksList.get(i).getName().equals(taskToBase) && tasksList.size() > i ){
                        Toast.makeText(Lists.this,"Одинаковые названия не допускаются",Toast.LENGTH_SHORT).show();
                        textNewTask.getText().clear();
                        return;
                    }}
                // Проверка на макимальное количество записей
                if (recyclerAdapter.getItemCount() >= MAX_NUM_OF_TASKS){
                    Toast.makeText(Lists.this, "Максимальная длинна списка 10", Toast.LENGTH_LONG).show();
                }   else {
                    refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(taskToBase);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("name",taskToBase);
                hashMap.put("color","1");
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



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:{
                changeColorTask(item.getGroupId());
                break;}
            case 1:{
                removeValue(item.getGroupId());
                break;}
        }
        return super.onContextItemSelected(item);
    }

    private void GetDataFromFirebase() {

        try {
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

                        try {
                            tasks.setName(snapshot.child("name").getValue().toString());
                            tasks.setColor(snapshot.child("color").getValue().toString());

                            tasksList.add(tasks);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(), tasksList);
                    myTaskList.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void changeColorTask(final int pos){
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();
        refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        AlertDialog.Builder builder = new AlertDialog.Builder(Lists.this);
        String[] colors = {"Желтый", "Красный", "Синий"};
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case 0:
                    {refToTasks.child(tasksList.get(pos).name).child("color").setValue("1");
                    break;}
                case 1:
                    {refToTasks.child(tasksList.get(pos).name).child("color").setValue("3");
                    break;}
                case 2:
                    {refToTasks.child(tasksList.get(pos).name).child("color").setValue("2");
                    break;}
            }}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeValue (int pos){
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();
        refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        refToTasks.child(tasksList.get(pos).name).removeValue();
    }

    @Override
    public void onBackPressed() {
        if(inputGroup.getVisibility() == View.VISIBLE) {
        inputGroup.setVisibility(View.GONE);
        openInputGroupBtn.setVisibility(View.VISIBLE);}
        else {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.settings_option){

        }

        if (item.getItemId() == R.id.about_option){

        }

        if (item.getItemId() == R.id.logout_option){
            mAuth.signOut();
            Intent intent = new Intent(Lists.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
