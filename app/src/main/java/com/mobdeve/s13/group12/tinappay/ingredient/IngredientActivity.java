package com.mobdeve.s13.group12.tinappay.ingredient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.mobdeve.s13.group12.tinappay.R;

import com.mobdeve.s13.group12.tinappay.ingredient.ingredient_modify.IngredientEditActivity;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class IngredientActivity extends AppCompatActivity {

    /* Class variables */
    // Activity elements
    private TextView tvTitle;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvLocation;
    private ImageButton ibEdit;
    private ImageButton ibCart;
    private ImageButton ibDelete;
    private ProgressBar pbProgress;
    private Group gComponents;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String ingredientId;

    //Firebase Cloud Storage Variables
    private FirebaseStorage fbStorage;
    private StorageReference storageReference;
    private Bitmap imageBitmap;

    // Final variables
    private final ActivityResultLauncher editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        setIntent(i);

                        Ingredient item = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());
                        String name = item.getName();
                        String type = item.getName();
                        float price = item.getPrice();
                        String location = item.getLocation();

                        tvTitle.setText(name);
                        tvName.setText(name);
                        tvType.setText(type);
                        tvPrice.setText(Float.toString(price));
                        tvLocation.setText(location);
                        Log.v("IN ARL IMAGE VIEW", "imagePath: " + item.getImagePath());
                        setImageView(item.getImagePath());
                    }
                }
            }
    );



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        initFirebase();
        bindComponents();
        initComponents();

    }

    /* Class functions */


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
        this.tvTitle = findViewById(R.id.tv_i_title);

        this.ivImg = findViewById(R.id.iv_i_img);
        this.tvName = findViewById(R.id.tv_i_name);
        this.tvType = findViewById(R.id.tv_i_type);
        this.tvPrice = findViewById(R.id.tv_i_price);
        this.tvLocation = findViewById(R.id.tv_i_location);
        this.ibEdit = findViewById(R.id.ib_i_edit);
        this.ibCart = findViewById(R.id.ib_i_cart);
        this.ibDelete = findViewById(R.id.ib_i_delete);
        this.pbProgress = findViewById(R.id.pb_i_progress);
        this.gComponents = findViewById(R.id.g_i_components);

    }

    private void initComponents() {
        Intent i = getIntent();

        Ingredient item = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());
        String name = item.getName();
        String type = item.getType();
        float price = item.getPrice();
        String location = item.getLocation();
        String ingredientId = item.getId();

        this.tvTitle.setText(name);
        //this.ivImg.setImageResource(img);
        setImageView(item.getImagePath());
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
        this.tvLocation.setText(location);
        this.ingredientId = ingredientId;

        initEditButton();
        initCartButton();
        initDeleteButton();
    }

    private void setImageView(String imagePath){
        //TODO DEAN: COMPLETE THIS
        //maximum number of bytes of image
        pbProgress.setVisibility(View.VISIBLE);
        gComponents.setVisibility(View.GONE);
        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(imagePath);

        imageReference.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivImg.setImageBitmap(imageBitmap);
                pbProgress.setVisibility(View.GONE);
                gComponents.setVisibility(View.VISIBLE);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        String errorMessage = e.getMessage();
                        Log.v("ERROR MESSAGE", "ERROR: " + imagePath + " " + errorMessage);
                        pbProgress.setVisibility(View.GONE);
                        gComponents.setVisibility(View.VISIBLE);
                    }
                });


    }

    private void initEditButton() {
        this.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(IngredientActivity.this, IngredientEditActivity.class);

                newIntent.putExtra(Keys.KEY_INGREDIENT.name(), oldIntent.getSerializableExtra(Keys.KEY_INGREDIENT.name()));

                /*
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_ID, oldIntent.getStringExtra(KeysOld.KEY_INGREDIENT_ID));
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_IMG, oldIntent.getIntExtra(KeysOld.KEY_INGREDIENT_IMG, 0));
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_NAME, oldIntent.getStringExtra(KeysOld.KEY_INGREDIENT_NAME));
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_TYPE, oldIntent.getStringExtra(KeysOld.KEY_INGREDIENT_TYPE));
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_PRICE, oldIntent.getFloatExtra(KeysOld.KEY_INGREDIENT_PRICE, 0));
                newIntent.putExtra(KeysOld.KEY_INGREDIENT_LOCATION, oldIntent.getStringExtra(KeysOld.KEY_INGREDIENT_LOCATION));
                 */

                editActivityResultLauncher.launch(newIntent);
            }
        });
    }

    private void initCartButton() {
        this.ibCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklist();
            }
        });
    }

    private void initDeleteButton(){
        this.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIngredient();
            }
        });
    }

    private void deleteIngredient(){
        this.pbProgress.setVisibility(View.VISIBLE);

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .child(this.ingredientId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful())
                    deleteSuccess();
                else
                    deleteFail();
            }
        });
    }

    private void deleteSuccess() {
        pbProgress.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Delete success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void deleteFail() {
        pbProgress.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Delete failed.", Toast.LENGTH_SHORT).show();
    }

    private void addChecklist() {
        Intent i = getIntent();

        Ingredient ingredient = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());
        String name = ingredient.getName();
        //String name = i.getStringExtra(KeysOld.KEY_INGREDIENT_NAME);
        String id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        ChecklistItem item = new ChecklistItem(name, false);
        db.getReference(Collections.checklist.name())
                .child(this.userId)
                .child(id)
                .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}