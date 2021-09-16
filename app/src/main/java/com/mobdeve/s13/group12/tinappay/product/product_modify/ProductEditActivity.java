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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProductEditActivity extends AppCompatActivity {
    // Activity Elements
    private ImageView ivImg;
    private EditText etName;
    private EditText etType;
    private EditText etDescription;
    // TODO: Display for list of ingredients
    private TextView tvIngredients;
    private Button ibSelectIngredients;
    private Button btnSubmit;
    private ProgressBar pbLoad;
    private Button btnUploadImage;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String productId;
    private HashMap<String, String> names;
    private HashMap<String, Integer> quantities;

    //Storage elements
    private FirebaseStorage fbStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    // Boolean for checking if the user has uploaded an image
    private Boolean hasUploadedImage;

    private ActivityResultLauncher selectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();

                        quantities = (HashMap<String, Integer>)i.getSerializableExtra(Keys.KEY_SELECT_INGREDIENTS.name());
                        // TODO: get ingredients
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_modify);

        bindComponents();
        initComponents();
        initFirebase();
    }

    private void bindComponents() {
        this.ivImg = findViewById(R.id.iv_pm_img);
        this.etName = findViewById(R.id.et_pm_name);
        this.etType = findViewById(R.id.et_pm_type);
        this.etDescription = findViewById(R.id.et_pm_description);
        this.ibSelectIngredients = findViewById(R.id.btn_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
        this.pbLoad = findViewById(R.id.pb_pm);
        this.btnUploadImage = findViewById(R.id.btn_pm_upload_image);
    }

    private void initComponents() {
        // Changes layout template text
        TextView title = findViewById(R.id.tv_pm_title);
        title.setText (R.string.pm_edit);
        this.btnSubmit.setText(R.string.pm_apply);

        // Pre-places values into layout elements
        Intent i = getIntent();
        Product p = (Product)i.getSerializableExtra(Keys.KEY_PRODUCT.name());

        String id = p.getId();
        Bitmap img = p.getImg();
        String name = p.getName();
        String type = p.getType();
        String description = p.getDescription();

        this.productId = p.getId();
        this.ivImg.setImageBitmap(img);
        this.etName.setText(name);
        this.etType.setText(type);
        this.etDescription.setText(description);
        this.quantities = p.getIngredients();

        initBtnSelect();
        initBtnAdd();

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
            ivImg.setImageURI(imageUri);
            Log.v("URI", "IMAGE URI: " + imageUri);

            hasUploadedImage = true;
        }
    }

    private void initBtnSelect() {
        // Initialize ingredient selector button
        this.ibSelectIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductEditActivity.this, SelectIngredientsActivity.class);
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                selectActivityResultLauncher.launch(i);
            }
        });
    }

    private void initBtnAdd() {
        // Initialize submit button
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int img = R.drawable.placeholder; // TODO: Redesigned image assignment
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

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release

        //Firebase Cloud Storage methods
        this.fbStorage = FirebaseStorage.getInstance();
        this.storageReference = fbStorage.getReference();
    }

    private boolean isValid (String name, String type, String description, HashMap<String, Integer> ingredients) {
        boolean valid = true;

        if (ingredients.size() == 0) {
            this.tvIngredients.setError("No ingredients");
            this.tvIngredients.requestFocus();
            valid = false;
        }

        else if (description.isEmpty()) {
            this.etDescription.setError("Required field");
            this.etDescription.requestFocus();
            valid = false;
        }

        else if (type.isEmpty()) {
            this.etType.setError("Required field");
            this.etType.requestFocus();
            valid = false;
        }

        else if (name.isEmpty()) {
            this.etName.setError("Required field");
            this.etName.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void storeProduct (Product p) {
        this.pbLoad.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        update.put(this.productId, p);

        ProductModel pm;
        if(hasUploadedImage)
            pm = new ProductModel(p, userId, productId);
        else
            pm = new ProductModel(p);

        db.getReference(Collections.products.name())
            .child(this.userId)
            .updateChildren(update)
            .addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    if(hasUploadedImage)
                        uploadImage(pm.getImagePath());
                    updateSuccess(pm);
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
     */
    private void uploadImage(String productImagePath){

        StorageReference userProductRef = storageReference.child(productImagePath);

        //Uploads the image to the cloud storage
        userProductRef.putFile(this.imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProductEditActivity.this, "Upload photo success.", Toast.LENGTH_SHORT).show();
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

    private void updateSuccess(ProductModel pm) {
        Intent i = new Intent();

        String imagePath = pm.getImagePath();
        String name = pm.getName();
        String type = pm.getType();
        String description = pm.getDescription();
        HashMap<String, Integer> ingredients = pm.getIngredients();
        Product p = new Product(imagePath, name, type, description, ingredients);

        i.putExtra(Keys.KEY_PRODUCT.name(), p);
        //i.putExtra(KeysOld.KEY_PRODUCT, p);

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product updated.", Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product could not be updated.", Toast.LENGTH_SHORT).show();
    }
}