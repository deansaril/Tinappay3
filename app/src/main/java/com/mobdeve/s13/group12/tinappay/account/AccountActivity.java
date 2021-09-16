package com.mobdeve.s13.group12.tinappay.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.mobdeve.s13.group12.tinappay.R;


import org.jetbrains.annotations.NotNull;

/**
 *  This activity handles the features and functionality of the account settings
 */
public class AccountActivity extends AppCompatActivity {

    // layout objects
    private TextView tvEmail;
    private EditText etPassword;
    private Button btnChangePass;
    private Button btnLogout;
    private Button btnConfirmPass;
    private ProgressBar pbAccount;

    // Firebase data
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initFirebase();
        bindComponents();
        initComponents();
    }

    /**
     *  This function initializes the components related to Firebase
     */
    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        this.user = this.mAuth.getCurrentUser();
        this.userId = this.user.getUid();
        this.userEmail = this.user.getEmail();

    }

    /**
     *  This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents(){
        this.tvEmail = findViewById(R.id.tv_a_email);
        tvEmail.setText(this.userEmail);
        this.etPassword = findViewById(R.id.et_a_pass);
        this.btnChangePass = findViewById(R.id.btn_a_change_pass);
        this.btnLogout = findViewById(R.id.btn_a_logout);
        this.btnConfirmPass = findViewById(R.id.btn_a_confirm_pass);
        this.pbAccount = findViewById(R.id.pb_a);
    }

    /**
     *    This function adds the needed functionalities of the layout objects
    */
    private void initComponents(){
        initBtnLogout();
        initBtnChangePass();
        initBtnConfirmPass();
    }

    /**
     * sets click listener for confirm pass, which checks for text in change password edit text and uses it to change the password
     */
    private void initBtnConfirmPass() {
        this.btnConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = etPassword.getText().toString().trim();
                if(password.isEmpty()){
                    etPassword.setError("Please Enter Email");
                    etPassword.requestFocus();
                }
                else{
                    //updates the password in Authentication
                    user.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AccountActivity.this, "Change Password Successful", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(AccountActivity.this, AccountActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(AccountActivity.this, "Change Password Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * sets click listener for change password button, which toggles the visibility of edit text fields for changing buttons
     */
    private void initBtnChangePass() {
        this.btnChangePass.setOnClickListener(new View.OnClickListener(){
            boolean isClicked = false;

            @Override
            public void onClick(View view){

                //sets up the toggle for changing
                if(!isClicked) {
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
    }

    /**
     * sets click listener for log out button, which logs out the current user
     */
    private void initBtnLogout() {
        this.btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //Logs out and redirects user to sign in screen after logging out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}