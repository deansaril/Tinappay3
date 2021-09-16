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
import com.mobdeve.s13.group12.tinappay.objects.User;

/**
 * Handles the functionality of the register account screen
 */
public class RegisterActivity extends AppCompatActivity {

    //Activity Elements
    private TextView tvLogin;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnConfirm;
    private ProgressBar pbRegister;

    //Firebase Elements
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initFirebase();
        bindComponents();
        initComponents();
    }

    /**
     *  This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        this.tvLogin = findViewById(R.id.tv_register_login);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.pbRegister = findViewById(R.id.pb_register);
        this.btnConfirm = findViewById(R.id.btn_register_confirm);
        this.pbRegister.setVisibility(View.GONE);
    }

    /**
     *  This function initializes the components related to Firebase
    */
    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     *  This function adds the needed functionalities of the layout objects
     */
    private void initComponents(){
        initTvLogin();
        initBtnConfirm();
    }

    /**
     * sets click listener for text view returning the user to log in screen
     */
    private void initTvLogin() {
        this.tvLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               finish();
            }
        });
    }

    /**
     * sets click listener for confirming account registration button
     */
    private void initBtnConfirm() {
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(!checkEmpty(email, password)){
                    User user = new User(email, password);
                    storeUser(user);
                }
            }
        });
    }

    /**
     *   This function checks if edit text fields to be filled out by user are empty
     *   @param email the String text user entered for email edit text
     *   @param password the String text user entered for password edit text
     *   @return hasEmpty if at least one of the fields is empty
     */
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

    /**
     *   This function stores the user to the authentication database if successful, then directs user to log in screen
     *   @param user contains the credentials for user to be registered
     */
    private void storeUser(User user) {
        this.pbRegister.setVisibility(View.VISIBLE);

        // register the user to Firebase
        this.mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            successfulRegistration();
                        } else {
                            failedRegistration();
                        }
                    }
                });
    }

    /**
     *  This function informs the user of the registration success and redirects them to log in screen
     */
    private void successfulRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Registration Success", Toast.LENGTH_SHORT).show();

        // redirect to login page afterwards
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *  This function informts the user that registration is unsuccessful
    */
    private void failedRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Registration Failed", Toast.LENGTH_SHORT).show();
    }
}