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
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initComponents();
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initComponents(){
        this.tvRegister = findViewById(R.id.tv_register_login);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.btnLogin = findViewById(R.id.btn_register_confirm);

        this.pbLogin = findViewById(R.id.pb_login);
        this.pbLogin.setVisibility(View.GONE);

        this.tvRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        this.btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                signIn(email,password);
            }
        });
    }

    private void signIn(String email, String password){
        this.pbLogin.setVisibility(View.VISIBLE);

        //code
        this.mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
                        }
                        pbLogin.setVisibility(View.GONE);
                    }
                });
    }


}