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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {

    private TextInputLayout ETPass, ETEmail;
    private Button haveAcc, regButton;

    private ImageView image;
    private TextView logo, slogan;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        ETEmail = findViewById(R.id.username);
        ETPass = findViewById(R.id.password);

        regButton = findViewById(R.id.btn_registration);
        haveAcc = findViewById(R.id.btn_have_acc);

        image = findViewById(R.id.logo_image);
        logo = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validatePass()){
                    return;
                }
                String email = ETEmail.getEditText().getText().toString();
                String password = ETPass.getEditText().getText().toString();
                registration(email,password);
            }
        });

        haveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registration(final String email, final String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseuser = mAuth.getCurrentUser();
                    assert firebaseuser != null;
                    String userId = firebaseuser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Registration.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Registration.this, Lists.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                } else{
                    String message = task.getException().toString();
                    Toast.makeText(Registration.this, "Error: " + message, Toast.LENGTH_SHORT).show();}
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
