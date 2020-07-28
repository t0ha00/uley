package com.example.uley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity /*implements View.OnClickListener*/{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextInputLayout ETPass,ETEmail;
    private Button loginButton, regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        ETEmail = findViewById(R.id.username);
        ETPass = findViewById(R.id.password);

        regButton = findViewById(R.id.btn_registration);
        loginButton = findViewById(R.id.btn_sign_in);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
        Intent intent = new Intent(MainActivity.this, Lists.class);
        startActivity(intent);
        finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(ETEmail.getEditText().getText().toString(),ETPass.getEditText().toString());
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration(ETEmail.getEditText().getText().toString(),ETPass.getEditText().toString());
            }
        });
    }


    public void signIn(String email, String pass){
        final String raz = email;
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                Toast.makeText(MainActivity.this,"Вход выполнен", Toast.LENGTH_SHORT).show();}
                else Toast.makeText(MainActivity.this,raz + " Ошибка входа", Toast.LENGTH_SHORT).show();
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
