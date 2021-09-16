package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.IngredientModel;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*
    This activity handles  the functionality of adding ingredients.
    This includes the entering of ingredient fields and its images
 */
public class IngredientAddActivity extends AppCompatActivity {

    // TODO DEAN: CHANGE THIS BASED ON JAN'S NEW ADD
    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_modify);

        initFirebase();
        bindComponents();
        initComponents();
    }

    /* Class variables */
    // Activity elements
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etLocation;
    private Button btnAdd;
    private Button btnUploadImage;
    private ProgressBar pbLoad;
    private ImageView ivIngredientImage;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    private String userId;

    private FirebaseStorage fbStorage;
    private StorageReference storageReference;
    private Uri imageUri;

    // Boolean for checking if the user has uploaded an image
    private Boolean hasUploadedImage;

    /* Class functions */

    /*
        This function initializes the components related to Firebase
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release

        //Firebase Cloud Storage methods
        this.fbStorage = FirebaseStorage.getInstance();
        this.storageReference = fbStorage.getReference();
    }

    /*
        This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        this.etName = findViewById(R.id.et_im_name);
        this.etType = findViewById(R.id.et_im_type);
        this.etPrice = findViewById(R.id.et_im_price);
        this.etLocation = findViewById(R.id.et_im_location);
        this.btnAdd = findViewById(R.id.btn_im_modify);
        this.pbLoad = findViewById(R.id.pb_im);
        this.ivIngredientImage = findViewById(R.id.iv_im_image);
        this.btnUploadImage = findViewById(R.id.btn_im_upload_image);
    }

    /*
        This function adds the needed functionalities of the layout objects
     */
    private void initComponents() {

        //Initializes uploaded image boolean to false;
        this.hasUploadedImage = false;

        TextView title = findViewById(R.id.tv_im_title);
        title.setText (R.string.im_add);
        this.btnAdd.setText(R.string.add);


        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                String location = etLocation.getText().toString().trim();
                float price = 0;

                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);


                if (!checkEmpty(name, type, location, price)){
                    Ingredient i = new Ingredient(name, type, location, price);
                    storeIngredient(i);
                }
            }
        });

        // adds image retrieval from gallery functionality to upload image button
        this.btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    /*
        This function allows the user to choose an image from the gallery when the Upload Image button is clicked.
        Called inside initialization of Upload Image button
     */
    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /*
       This function sets the Image View with the image the user has selected from the gallery
       This function sets the Image View with the image the user has selected from the gallery
       @param requestCode received from chooseImage() function
       @param resultCode is the status of the result
       @param data contains the image being chosen
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checks if the user has selected an image
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            ivIngredientImage.setImageURI(imageUri);
            Log.v("URI", "IMAGE URI: " + imageUri);

            hasUploadedImage = true;
        }

    }

    /*
        This function checks for empty edit text fields to be filled by the user.
        Called when the user clicks the add button
     */
    private boolean checkEmpty (String name, String type, String location, float price) {
        boolean hasEmpty = false;

        if (location.isEmpty()) {
            this.etLocation.setError("Required field");
            this.etLocation.requestFocus();
            hasEmpty = true;
        }

        if (price <= 0) {
            this.etPrice.setError("Invalid price");
            this.etPrice.requestFocus();
            hasEmpty = true;
        }

        if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            hasEmpty = true;
        }

        if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }

    /*
        This function stores the instantiated ingredient to the Realtime Database.
        It also calls the uplaodImage() function, which uploads the image the user has chosen, if there is one, to the Cloud Storage
        @param ingredient
     */
    private void storeIngredient (Ingredient ingredient) {
        this.pbLoad.setVisibility(View.VISIBLE);
        String ingredientId = UUID.randomUUID().toString().replace("-","").substring(0,8);
        IngredientModel im;

        if(hasUploadedImage)
            im = new IngredientModel(ingredient, userId, ingredientId);
        else
            im = new IngredientModel(ingredient);

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .child(ingredientId)
                .setValue(im).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (hasUploadedImage)
                        uploadImage(im.getImagePath());
                    else
                        addSuccess();
                }
                else
                    addFail();
            }
        });
    }

    /*
        This function handles the uploading of the image the user has chosen to the Cloud Storage.
        This is called when the user clicks the add button has filled needed data and image.
        @param ingredientImagePath is the path to the photo to be uploaded to the cloud storage.
     */
    private void uploadImage(String ingredientImagePath){

        StorageReference userIngredientRef = storageReference.child(ingredientImagePath);

        //Uploads the image to the cloud storage
        userIngredientRef.putFile(this.imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(IngredientAddActivity.this, "Upload photo success.", Toast.LENGTH_SHORT).show();
                        addSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //Handle unsuccessful Uploads
                        addFail();
                    }
                });

    }

    /*
        This function is called when the adding of ingredient is successful.
     */
    private void addSuccess() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientAddActivity.this, "Add success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    /*
        This function is called when the adding of ingredient is unsuccessful.
     */
    private void addFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientAddActivity.this, "Add failed.", Toast.LENGTH_SHORT).show();
    }
}