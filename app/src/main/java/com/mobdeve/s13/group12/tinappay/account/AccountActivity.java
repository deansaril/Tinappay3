package com.mobdeve.s13.group12.tinappay.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;

import org.jetbrains.annotations.NotNull;

public class AccountActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etPassword;
    private Button btnChangePass;
    private Button btnLogout;
    private Button btnConfirmPass;
    private ProgressBar pbAccount;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initComponents();
        initFirebase();
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        this.user = this.mAuth.getCurrentUser();
        this.userId = this.user.getUid();
        this.userEmail = this.user.getEmail();

        tvEmail.setText(this.userEmail);
    }

    private void initComponents(){
        this.tvEmail = findViewById(R.id.tv_a_email);
        this.etPassword = findViewById(R.id.et_a_pass);
        this.btnChangePass = findViewById(R.id.btn_a_change_pass);
        this.btnLogout = findViewById(R.id.btn_a_logout);
        this.btnConfirmPass = findViewById(R.id.btn_a_confirm_pass);
        this.pbAccount = findViewById(R.id.pb_a);

        this.btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        this.btnChangePass.setOnClickListener(new View.OnClickListener(){
            boolean isClicked = false;

            @Override
            public void onClick(View view){
                // TODO DEAN: DO A TOGGLE FOR CHANGE PASSWORD FOR SETTING CONFIRM BUTTON

                if(isClicked == false) {
                    etPassword.setVisibility(View.VISIBLE);
                    btnConfirmPass.setVisibility(View.VISIBLE);
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnChangePass.setText("CANCEL");
                    isClicked = true;
                }
                else{
                    etPassword.setVisibility(View.GONE);
                    btnConfirmPass.setVisibility(View.GONE);
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnChangePass.setText("CHANGE PASSWORD");
                    isClicked = false;
                }

            }
        });

        this.btnConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO DEAN: DO A CHANGE PASSWORD FOR FIREBASE

                String password = etPassword.getText().toString().trim();
                if(password.isEmpty()){
                    etPassword.setError("Please Enter Email");
                    etPassword.requestFocus();
                }
                else{
                    user.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AccountActivity.this, "CHANGE PASSWORD SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(AccountActivity.this, AccountActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(AccountActivity.this, "CHANGE PASSWORD FAILED", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


}