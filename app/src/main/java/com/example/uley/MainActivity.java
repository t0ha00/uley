package com.example.uley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText ETPass;
    private EditText ETemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(MainActivity.this, Lists.class);
                    startActivity(intent);
                }
                else {

                }
            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETPass = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_registration).setOnClickListener(this);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
        Intent intent = new Intent(MainActivity.this, Lists.class);
        startActivity(intent);}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sign_in){
            signIn(ETemail.getText().toString(),ETPass.getText().toString());
        }
            else if (v.getId() == R.id.btn_registration)
        {
            registration(ETemail.getText().toString(),ETPass.getText().toString());
        }
    }

    public void signIn(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                Toast.makeText(MainActivity.this,"Вход выполнен", Toast.LENGTH_SHORT).show();}
                else Toast.makeText(MainActivity.this,"Вход выполнен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registration (String email, String pass){
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Регистрация выполнена успешно", Toast.LENGTH_SHORT).show();}
                else Toast.makeText(MainActivity.this,"Регистрация неуспешна", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
