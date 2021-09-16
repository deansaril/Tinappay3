package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class IngredientEditActivity extends AppCompatActivity {

    /* Class variables */
    // Activity Elements
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etPrice;
    private EditText etLocation;
    private Button btnSubmit;
    private ProgressBar pbLoad;
    private Button btnUploadImage;
    private Group gComponents;

    // Back-end code
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String ingredientId;
    private String ingredientImagePath;

    //Firebase Cloud Storage Variables
    private FirebaseStorage fbStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    private Bitmap imageBitmap;

    // Boolean for checking if the user has uploaded an image
    private Boolean hasUploadedImage;

    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_modify);

        initFirebase();
        bindComponents();
        initComponents();
    }

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
        this.ivImg = findViewById(R.id.iv_im_image);
        this.etName = findViewById(R.id.et_im_name);
        this.etType = findViewById(R.id.et_im_type);
        this.etPrice = findViewById(R.id.et_im_price);
        this.etLocation = findViewById(R.id.et_im_location);
        this.btnSubmit = findViewById(R.id.btn_im_modify);
        this.pbLoad = findViewById(R.id.pb_im);
        this.btnUploadImage = findViewById(R.id.btn_im_upload_image);
        this.gComponents = findViewById(R.id.g_im_components);
    }

    /*
        This function adds the needed functionalities of the layout objects
    */
    private void initComponents() {

        //Initializes uploaded image boolean to false;
        this.hasUploadedImage = false;

        // Changes layout template text
        TextView title = findViewById(R.id.tv_im_title);
        title.setText (R.string.im_edit);
        this.btnSubmit.setText(R.string.apply);

        // Pre-places values into layout elements
        Intent i = getIntent();
        Ingredient item = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());

        this.ingredientId = item.getId();
        this.ingredientImagePath = item.getImagePath();
        String name = item.getName();
        String type = item.getType();
        float price = item.getPrice();
        String location = item.getLocation();
        String imagePath = item.getImagePath();

        this.etName.setText(name);
        this.etType.setText(type);
        this.etPrice.setText(Float.toString(price));
        this.etLocation.setText(location);
        setImageView(imagePath);

        // Initialize submit button
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String sPrice = etPrice.getText().toString().trim();
                String location = etLocation.getText().toString().trim();

                float price = 0;
                if (!sPrice.isEmpty())
                    price = Float.parseFloat(sPrice);

                // Sends update if values are valid
                if (!checkEmpty(name, type, location, price)) {
                    Ingredient ingredient;

                    if(hasUploadedImage)
                        ingredient = new Ingredient(userId,name, type, location, price);
                    else
                        ingredient = new Ingredient(name, type, location, price);

                    // ingredient.setImagePath(ingredientImagePath);

                    updateIngredient(ingredient);
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

    private void setImageView(String imagePath){
        //maximum number of bytes of image
        pbLoad.setVisibility(View.VISIBLE);
        gComponents.setVisibility(View.GONE);

        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(imagePath);

        imageReference.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivImg.setImageBitmap(imageBitmap);
                pbLoad.setVisibility(View.GONE);
                gComponents.setVisibility(View.VISIBLE);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        String errorMessage = e.getMessage();
                        Log.v("ERROR MESSAGE", "ERROR: " + imagePath + " " + errorMessage);
                        pbLoad.setVisibility(View.GONE);
                        gComponents.setVisibility(View.VISIBLE);
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
            ivImg.setImageURI(imageUri);
            Log.v("URI", "IMAGE URI: " + imageUri);

            hasUploadedImage = true;
        }

    }

    /*
        This function checks for empty edit text fields to be filled by the user.
        Called when the user clicks the add button
        @param name is the String retrieved from name edit text element
        @param type is the String retrieved from type edit text element
        @param location is the String retrieved from location edit text element
        @param price is the float retrieved from price edit text element
        @return hasEmpty- whether there are empty fields
     */
    private boolean checkEmpty (String name, String type, String location, float price) {
        boolean hasEmpty = false;

        if (location.isEmpty()) {
            this.etLocation.setError("Please Enter Location");
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
        This function updates the ingredient to the Realtime Database.
        It also calls the uploadImage() function, which uploads the image the user has chosen, if there is one, to the Cloud Storage
        @param ingredient is the ingredient containing the edited fields
     */

    private void updateIngredient (Ingredient ingredient) {
        this.pbLoad.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        update.put(this.ingredientId, ingredient);

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        if(hasUploadedImage) {
                            Log.v("HAS UPLOADED", "proceeding to uploadImage. imagepath: " +ingredient.getImagePath());
                            uploadImage(ingredient.getImagePath());
                        }
                        updateSuccess(ingredient);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateFail();
                    }
                });
    }

    /*
        This function handles the uploading of the image the user has chosen to the Cloud Storage.
        This is called when the user clicks the add button has filled needed data and image.
        @param ingredientId is the unique id of the ingredient that will have the said image
     */
    private void uploadImage(String ingredientImagePath){

        StorageReference userIngredientRef = storageReference.child(ingredientImagePath);

        //Uploads the image to the cloud storage
        userIngredientRef.putFile(this.imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(IngredientEditActivity.this, "Upload photo success.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //Handle unsuccessful Uploads
                        updateFail();
                    }
                });

    }

    /*
      This function is called when the updating of ingredient is successful.
      @param ingredient is the Ingredient being added
    */
    private void updateSuccess(Ingredient ingredient) {
        Intent i = new Intent();

        i.putExtra(Keys.KEY_INGREDIENT.name(), ingredient);

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientEditActivity.this, "INGREDIENT UPDATED", Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    /*
      This function is called when the updating of ingredient is unsuccessful.
    */
    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(IngredientEditActivity.this, "INGREDIENT CANNOT BE UPDATED", Toast.LENGTH_SHORT).show();
    }

}