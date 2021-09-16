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

/**
 *   This activity handles the functionalities of Sign in screen.
 */
public class MainActivity extends AppCompatActivity {

    //Activity elements
    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;

    //Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        bindComponents();
        initComponents();
    }

    /**
     *  This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        this.tvRegister = findViewById(R.id.tv_login_register);
        this.etEmail = findViewById(R.id.et_login_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.btnLogin = findViewById(R.id.btn_login_confirm);

        this.pbLogin = findViewById(R.id.pb_login);
        this.pbLogin.setVisibility(View.GONE);
    }

    /**
     *  This function initializes the components related to Firebase
     */
    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }


    /**
     * This function adds the needed functionalities of the layout objects
     */
    private void initComponents(){

        initTvRegister();
        initBtnLogin();
    }

    /**
     * sets click listener for confirm log in button
     */
    private void initBtnLogin() {
        this.btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(!checkEmpty(email, password)) {
                    signIn(email, password);
                }
            }
        });
    }

    /**
     * sets click listener for create new account text view
     */
    private void initTvRegister() {
        this.tvRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * This function is called when the user successfully enters the email and password.
     * This signs in the user if credientials are correct
     *
     * @param email is the String entered by user in email edit text field
     * @param password is the String entered by user in password edit text field
     */
    private void signIn(String email, String password){
        this.pbLogin.setVisibility(View.VISIBLE);

        //signs in the user using credentials
        this.mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // redirects user to home screen after logging in
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                        pbLogin.setVisibility(View.GONE);
                    }
                });
    }

    /**
     *   This function checks if edit text fields to be filled out by user are empty
     *   @param email is the String entered by user in email edit text field
     *   @param password is the String entered by user in password edit text field
     *   @return hasEmpty Boolean if at least one of the fields is empty
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


}