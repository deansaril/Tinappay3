package com.mobdeve.s13.group12.tinappay.product.product_modify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductModel;
import com.mobdeve.s13.group12.tinappay.product.select_ingredients.SelectIngredientsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * This activity handles the editing of products
 */
public class ProductEditActivity extends AppCompatActivity {

    /* Class variables */
    // Activity Elements
    private ProgressBar pbLoad;
    private TextView tvTitle;
    private ImageView ivImg;
    private Button btnUploadImage;
    private EditText etName;
    private EditText etType;
    private EditText etDescription;
    private Button ibSelectIngredients;
    private Button btnSubmit;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private StorageReference storageReference;
    private Uri imageUri;
    private boolean hasUploadedImage;
    private String productId;
    private HashMap<String, Integer> quantities;



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
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.storageReference = FirebaseStorage.getInstance().getReference();
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
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
        this.ibSelectIngredients = findViewById(R.id.btn_pm_edit_ingredient);
        this.btnSubmit = findViewById(R.id.btn_pm_submit);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        // Local data
        this.hasUploadedImage = false;

        // Changes layout template text
        this.tvTitle.setText (R.string.pm_edit);
        this.btnSubmit.setText(R.string.pm_apply);

        // Pre-places values into layout elements
        Intent i = getIntent();
        Product p = (Product)i.getSerializableExtra(Keys.KEY_PRODUCT.name());

        String id = p.getId();
        Bitmap img = p.getImg();
        String name = p.getName();
        String type = p.getType();
        String description = p.getDescription();
        Log.d("PEA - oC", p.getIngredients().entrySet().toString());

        this.productId = id;
        this.ivImg.setImageBitmap(img);
        this.etName.setText(name);
        this.etType.setText(type);
        this.etDescription.setText(description);
        this.quantities = p.getIngredients();

        initBtnUpload();
        initBtnSelect();
        initBtnAdd();
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
    private void initBtnSelect() {
        this.ibSelectIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductEditActivity.this, SelectIngredientsActivity.class);
                i.putExtra(Keys.KEY_SELECT_INGREDIENTS.name(), quantities);

                startActivityForResult(i, 2);
            }
        });
    }

    /**
     * Initializes button to submit to database
     */
    private void initBtnAdd() {
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                // Sends update if values are valid
                if (isValid(name, type, description, quantities)) {
                    Product p = new Product(name, type, description, quantities);
                    updateProduct(p);
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
        if (ingredients.size() == 0) {
            //this.tvIngredients.setError("No ingredients");
            //this.tvIngredients.requestFocus();
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
     * Updates item data in database
     * @param p Product - item to be updated
     */
    private void updateProduct (Product p) {
        this.pbLoad.setVisibility(View.VISIBLE);

        // Creates a ProductModel depending if user saved an image
        ProductModel pm;
        if(hasUploadedImage)
            pm = new ProductModel(p, userId, productId);
        else
            pm = new ProductModel(p);

        // Prepares database update
        HashMap<String, Object> update = new HashMap<>();
        update.put(this.productId, pm);

        // Sends update to database
        db.getReference(Collections.products.name())
            .child(this.userId)
            .updateChildren(update)
            .addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    // If an image is selected, upload it
                    if(hasUploadedImage)
                        uploadImage(pm.getImagePath());
                    // If no image is selected, retrieve previous
                    else
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

    /**
     * Uploads image to cloud storage
     * @param productImagePath String - file path of image on cloud storage
     */
    private void uploadImage(String productImagePath) {
        StorageReference userProductRef = storageReference.child(productImagePath);

        //Uploads image to cloud storage
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

    /**
     * Follow-up processes if updates are successful
     * @param pm ProductModel - item to be updated
     */
    private void updateSuccess(ProductModel pm) {
        Intent oldIntent = getIntent();
        Intent i = new Intent();

        // Create item to be transferred across activities
        String imagePath = pm.getImagePath();
        String name = pm.getName();
        String type = pm.getType();
        String description = pm.getDescription();
        HashMap<String, Integer> ingredients = pm.getIngredients();
        Product p = new Product(imagePath, name, type, description, ingredients);

        // Copy over data from old item being transferred across activities
        String id = ((Product)oldIntent.getSerializableExtra(Keys.KEY_PRODUCT.name())).getId();
        Bitmap bmp = ((Product)oldIntent.getSerializableExtra(Keys.KEY_PRODUCT.name())).getImg();
        if (hasUploadedImage)
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (Exception e) {
                Log.e("PE", e.toString());
            }
        p.setId(id);
        p.setImg(bmp);

        i.putExtra(Keys.KEY_PRODUCT.name(), p);

        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product updated.", Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    /**
     * Displays prompt notifying update failure
     */
    private void updateFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductEditActivity.this, "Product could not be updated.", Toast.LENGTH_SHORT).show();
    }
}