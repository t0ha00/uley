package com.example.uley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SubTasks extends AppCompatActivity {
    private final Integer MAX_NUM_OF_TASKS = 10;

    private ArrayList<Tasks> tasksList;
    private Button addNewTaskBtn;
    private RecyclerView myTaskList;
    private Toolbar mToolBar;
    private EditText textNewTask;
    private ImageView openInputGroupBtn;
    private SubTaskAdapter subTaskAdapter;

    private DatabaseReference myRef,refToTasks;
    private FirebaseAuth mAuth;
    public String nameMainTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_tasks);
        Intent intent = getIntent();
        nameMainTask = intent.getStringExtra("Main name task");

        myTaskList = findViewById(R.id.list_sub_tasks);
        addNewTaskBtn = findViewById(R.id.btn_new_task);
        textNewTask = findViewById(R.id.new_task);
        openInputGroupBtn = findViewById(R.id.open_input_module_sub);
        mToolBar = findViewById(R.id.tasks_list_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(nameMainTask);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myTaskList.setLayoutManager(layoutManager);
        myTaskList.setHasFixedSize(true);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();
        ClearAllList();

        GetDataFromFirebase();

        openInputGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Видимость вводимой группы и открытие клавиатуры
                AlertDialog.Builder builder = new AlertDialog.Builder(SubTasks.this);
                builder.setTitle("Создать подзадачу");
                final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog,null);
                builder.setView(customLayout);

                final EditText editText = customLayout.findViewById(R.id.dialog_edit_text);
                final RadioButton redRadio = customLayout.findViewById(R.id.radio_btn_red);
                final RadioButton yellowRadio = customLayout.findViewById(R.id.radio_btn_yellow);
                final RadioButton blueRadio = customLayout.findViewById(R.id.radio_btn_blue);
                final RadioButton textRadio = customLayout.findViewById(R.id.radio_btn_text);
                final RadioButton mapRadio = customLayout.findViewById(R.id.radio_btn_map);
                editText.requestFocus();
                editText.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

                builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskToBase = editText.getText().toString();
                        boolean redChecked = redRadio.isChecked();
                        boolean yellowChecked = yellowRadio.isChecked();
                        boolean blueChecked = blueRadio.isChecked();
                        boolean textChecked = textRadio.isChecked();
                        boolean mapChecked = mapRadio.isChecked();

                        for (int i=0; i<tasksList.size();i++){
                            if (tasksList.get(i).getName().equals(taskToBase) && tasksList.size() > i ){
                                Toast.makeText(SubTasks.this,"Одинаковые названия не допускаются",Toast.LENGTH_SHORT).show();
                                textNewTask.getText().clear();
                                return;
                            }}
                        // Проверка на макимальное количество записей
                        if (subTaskAdapter.getItemCount() >= MAX_NUM_OF_TASKS){
                            Toast.makeText(SubTasks.this, "Максимальная длинна списка 10", Toast.LENGTH_LONG).show();
                        }   else {
                            refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(nameMainTask).child("subtasks").child(taskToBase);
                            final HashMap<String, String> hashMap = new HashMap<>();
                            if (redChecked){hashMap.put("color","3");}
                            else if (yellowChecked){hashMap.put("color","1");}
                            else if (blueChecked){ hashMap.put("color","2"); }

                            hashMap.put("name",taskToBase);

                            if (textChecked){hashMap.put("type","t");}
                            else if (mapChecked){hashMap.put("type","m");}
                            refToTasks.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SubTasks.this, "Добавлено", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            textNewTask.getText().clear();
                        }
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void GetDataFromFirebase() {
        Intent intent = getIntent();
        final String nameMainTask = intent.getStringExtra("Main name task");
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        String userId = firebaseuser.getUid();
        Query query = myRef.child("Users").child(userId).child(nameMainTask).child("subtasks");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ClearAllList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tasks tasks = new Tasks();

                    tasks.setName(snapshot.child("name").getValue().toString());
                    tasks.setColor(snapshot.child("color").getValue().toString());
                    tasks.setTypeSubTask(snapshot.child("type").getValue().toString());

                    tasksList.add(tasks);
                }

                subTaskAdapter = new SubTaskAdapter(getApplicationContext(), tasksList);
                myTaskList.setAdapter(subTaskAdapter);
                subTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

    private void changeColorTask(final int pos){
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();
        refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(nameMainTask).child("subtasks");
        AlertDialog.Builder builder = new AlertDialog.Builder(SubTasks.this);
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

    private void ClearAllList(){
        if (tasksList != null){
            tasksList.clear();

            if (subTaskAdapter != null){
                subTaskAdapter.notifyDataSetChanged();
            }
        }

        tasksList = new ArrayList<>();
    }

    private void removeValue (int pos){
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        assert firebaseuser != null;
        final String userId = firebaseuser.getUid();
        refToTasks = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(nameMainTask).child("subtasks");
        refToTasks.child(tasksList.get(pos).name).removeValue();
    }

    @Override
    public void onBackPressed() {

            finish();
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
            Intent intent = new Intent(SubTasks.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
