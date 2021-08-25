package com.mobdeve.s13.group12.tinappay.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.Collections;
import com.mobdeve.s13.group12.tinappay.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnConfirm;
    private ProgressBar pbRegister;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initFirebase();
        initComponents();
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
    }

    private void initComponents(){
        this.tvRegister = findViewById(R.id.tv_register_login);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.pbRegister = findViewById(R.id.pb_register);
        this.btnConfirm = findViewById(R.id.btn_register_confirm);
        this.pbRegister.setVisibility(View.GONE);


        this.tvRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(!checkEmpty(email, password)){
                    User user = new User(email, password);
                    //Toast.makeText(RegisterActivity.this, "CREDENTIALS VALID", Toast.LENGTH_SHORT).show();
                    storeUser(user);
                }
            }
        });
    }

    private boolean checkEmpty(String email, String password) {
        boolean hasEmpty = false;
        if (email.isEmpty()) {
            // set error message
            this.etEmail.setError("Please Enter Email");
            this.etEmail.requestFocus();
            hasEmpty = true;
        } else if (password.isEmpty()) {
            // set error message
            this.etPassword.setError("Please Enter Password");
            this.etPassword.requestFocus();
            hasEmpty = true;
        }
        return hasEmpty;
    }

    private void storeUser(User user) {
        this.pbRegister.setVisibility(View.VISIBLE);

        Toast.makeText(RegisterActivity.this, "IN STORE USER", Toast.LENGTH_SHORT).show();
        // register the user to Firebase
        this.mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "AUTHRESULT SUCCESS", Toast.LENGTH_SHORT).show();
                            database.getReference(Collections.users.name())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "VOID TASK SUCCESS", Toast.LENGTH_SHORT).show();
                                        successfulRegistration();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "VOID TASK FAIL", Toast.LENGTH_SHORT).show();
                                        failedRegistration();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "AUTHRESULT FAIL", Toast.LENGTH_SHORT).show();
                            failedRegistration();
                        }
                    }
                });
    }

    private void successfulRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Registration Success", Toast.LENGTH_SHORT).show();

        // redirect to login page afterwards
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void failedRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Registration Failed", Toast.LENGTH_SHORT).show();
    }
}