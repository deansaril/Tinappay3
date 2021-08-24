package com.mobdeve.s13.group12.tinappay.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s13.group12.tinappay.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnConfirm;
    private ProgressBar pbRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    private void initComponents(){
        this.tvRegister = findViewById(R.id.tv_register_login);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.pbRegister = findViewById(R.id.pb_register);
        this.btnConfirm = findViewById(R.id.btn_register_confirm);

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
                /*
                if(!checkEmpty(email, password){
                    User user = new User(email, password);
                    storeUser(user);
                }
                 */
            }
        });
    }
}