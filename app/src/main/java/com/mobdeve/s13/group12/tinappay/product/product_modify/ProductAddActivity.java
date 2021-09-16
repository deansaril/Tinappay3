package com.mobdeve.s13.group12.tinappay.product.product_modify;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * This activity handles the adding of products
 */
public class ProductAddActivity extends AppCompatActivity {

    /* Class variables */
    // Activity Elements
    private ProgressBar pbLoad;
    private TextView tvTitle;
    private ImageView ivImg;
    private Button btnUploadImage;
    private EditText etName;
    private EditText etType;
    private EditText etDescription;
    private Button ibEditIngredients;
    private Button btnSubmit;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private StorageReference storageReference;
    private Uri imageUri;
    private Boolean hasUploadedImage;
    private HashMap<String, Integer> quantities;

    // Final variables
    private final ActivityResultLauncher selectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Gets list of selected ingredients and their quantities
                        Intent i = result.getData();

                        quantities = (HashMap<String, Integer>)i.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());
                    }
                }
            }
    );



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_modify);

        initFirebase();
        bindComponents();
        initComponents();
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
            ivImg.setImageURI(imageUri);
            Log.v("URI", "IMAGE URI: " + imageUri);

            hasUploadedImage = true;
        }
        else if(requestCode == 2 && resultCode == RESULT_OK) {
            quantities = (HashMap<String, Integer>) data.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());
            Log.d("PEA - AR", quantities.entrySet().toString());
        }
    }



    /* Class functions */
    /**
     * Initializes connection to firebase database and cloud storage
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.userId = this.mAuth.getCurrentUser().getUid();
    }

    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
    private void bindComponents() {
        this.pbLoad = findViewById(R.id.pb_pm);
        this.tvTitle = findViewById(R.id.tv_pm_title);
        this.ivImg = findViewById(R.id.iv_pm_img);
        this.btnUploadImage = findViewById(R.id.btn_pm_upload_image);
        this.etName = findViewById(R.id.et_pm_name);
        this.etType = findViewById(R.id.et_pm_type);
        this.etDescription = findViewById(R.id.et_pm_description);
        this.ibEditIngredients = findViewById(R.id.btn_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        // Local data
        this.hasUploadedImage = false;

        // Changes layout template text;
        this.tvTitle.setText (R.string.pm_add);
        this.btnSubmit.setText(R.string.pm_add);

        this.quantities = new HashMap<>();

        initBtnUpload();
        initSelectBtn();
        initAddBtn();
    }

    /**
     * Initializes button for moving to image select
     */
    private void initBtnUpload() {
        this.btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Initializes button for moving to ingredient select
     */
    private void initSelectBtn() {
        this.ibEditIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAddActivity.this, SelectIngredientsActivity.class);
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                startActivityForResult(i, 2);
            }
        });
    }

    /**
     * Initializes button to submit to database
     */
    private void initAddBtn() {
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                // Sends update if values are valid
                if (isValid(name, type, description, quantities)) {
                    Product p = new Product(name, type, description, quantities);
                    storeProduct(p);
                }
            }
        });
    }

    /**
     * Checks if inputted values are valid
     * @param name String - product name
     * @param type String - product type
     * @param description String - product description
     * @param ingredients HashMap - list of product ingredients and quantity
     * @return true if values are valid, otherwise false
     */
    private boolean isValid (String name, String type, String description, HashMap<String, Integer> ingredients) {
        boolean valid = true;

        // If no ingredients are selected
        if (ingredients.isEmpty()) {
            Toast.makeText(ProductAddActivity.this, "No ingredients have been selected!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        // If no description is given
        if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
            valid = false;
        }

        // If no type is given
        if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            valid = false;
        }

        // If no name is given
        if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            valid = false;
        }

        return valid;
    }

    /**
     * Saves item data in database
     * @param p Product - item to be saved
     */
    private void storeProduct (Product p) {
        this.pbLoad.setVisibility(View.VISIBLE);

        // Prepares database storage
        String productId = UUID.randomUUID().toString().replace("-","").substring(0,8);

        // Creates a ProductModel depending if user saved an image
        ProductModel pm;
        if(hasUploadedImage)
            pm = new ProductModel(p, userId, productId);
        else
            pm = new ProductModel(p);

        // Sends data to database
        db.getReference(Collections.products.name())
                .child(this.userId)
                .child(productId)
                .setValue(pm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // If an image is selected, upload it
                    if (hasUploadedImage)
                        uploadImage(pm.getImagePath());
                    // If no image is selected, retrieve previous
                    else
                        addSuccess();
                }
                else
                    addFail();
            }
        });
    }

    /**
     * Uploads image to cloud storage
     * @param productImagePath String - file path of image on cloud storage
     */
    private void uploadImage(String productImagePath){
        StorageReference userProductRef = storageReference.child(productImagePath);

        //Uploads image to cloud storage
        userProductRef.putFile(this.imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProductAddActivity.this, "Upload photo success.", Toast.LENGTH_SHORT).show();
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

    /**
     * Displays prompt notifying save success
     */
    private void addSuccess() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductAddActivity.this, "Add success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Displays prompt notifying save failure
     */
    private void addFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductAddActivity.this, "Add failed.", Toast.LENGTH_SHORT).show();
    }
}