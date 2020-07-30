package com.example.uley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private TextInputLayout ETPass,ETEmail;
    private Button loginButton, regButton;
    private ImageView image;
    private TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            Intent intent = new Intent(Login.this, Lists.class);
            startActivity(intent);
            finish();
        }

        image = findViewById(R.id.logo_image);
        logo = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);

        ETEmail = findViewById(R.id.username);
        ETPass = findViewById(R.id.password);

        regButton = findViewById(R.id.btn_registration);
        loginButton = findViewById(R.id.btn_sign_in);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validatePass()){
                    return;
                }
                signIn(ETEmail.getEditText().getText().toString(),ETPass.getEditText().getText().toString());
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(logo,"logo_text");
                pairs[2] = new Pair<View,String>(slogan,"slogan_text");
                pairs[3] = new Pair<View,String>(regButton,"reg_btn");
                pairs[4] = new Pair<View,String>(loginButton,"login_btn");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);
                startActivity(intent,options.toBundle());
            }
        });
    }


    public void signIn(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                Toast.makeText(Login.this,"Вход выполнен", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this,Lists.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);}
                else Toast.makeText(Login.this,"Ошибка входа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail (){
        String val = ETEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()){
            ETEmail.setError("Поле не может быть пустым");
            return false;
        }else if (!val.matches(emailPattern)){
            ETEmail.setError("Введенеа некоректная почта");
            return false;
        }
        else {
            ETEmail.setError(null);
            return true;
        }
    }
    private boolean validatePass (){
        String val = ETPass.getEditText().getText().toString();
        if (val.isEmpty()){
            ETPass.setError("Поле не может быть пустым");
            return false;
        }else if(val.length()<=6){
            ETPass.setError("Пароль должен быть длиннее 6 символов");
            return false;
        }
        else {
            ETPass.setError(null);
            return true;
        }
    }

}
