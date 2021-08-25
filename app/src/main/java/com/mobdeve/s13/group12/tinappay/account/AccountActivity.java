package com.mobdeve.s13.group12.tinappay.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group12.tinappay.R;

public class AccountActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etPassword;
    private Button btnChangePass;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initFirebase();
        initComponents();
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        this.user = this.mAuth.getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void initComponents(){
        this.tvEmail = findViewById(R.id.tv_register_login);
        this.etPassword = findViewById(R.id.et_register_email);
        this.btnChangePass = findViewById(R.id.btn_register_confirm);
        this.btnLogout = findViewById(R.id.btn_account_logout);

        this.btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email = etPassword.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
            }
        });
    }


}