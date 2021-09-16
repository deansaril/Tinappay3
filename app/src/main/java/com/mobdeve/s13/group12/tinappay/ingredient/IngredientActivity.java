package com.mobdeve.s13.group12.tinappay.ingredient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

/**
 * Handles the display of selected ingredient and setting up of buttons for editing, adding to checklist, and deleting
 */
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
    private ImageButton ibChecklist;
    private ImageButton ibDelete;
    private ProgressBar pbProgress;
    private Group gComponents;
    private Dialog diaConfirm;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String ingredientId;
    private String imagePath;

    //Firebase Cloud Storage Variables
    private FirebaseStorage fbStorage;
    private StorageReference storageReference;

    // Final variables

    //Retrieves the data resulting from the edited ingredient from IngredientEditActivity
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
                        String type = item.getType();
                        float price = item.getPrice();
                        String location = item.getLocation();
                        Bitmap imageBitmap = item.getImg();

                        tvTitle.setText(name);
                        tvName.setText(name);
                        tvType.setText(type);
                        tvPrice.setText(Float.toString(price));
                        tvLocation.setText(location);
                        ivImg.setImageBitmap(imageBitmap);
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

    /**
     *   This function initializes the components related to Firebase
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

    /**
     *This function binds the objects in the layout to the activity's variables for editing
     */
    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_i_title);

        this.ivImg = findViewById(R.id.iv_i_img);
        this.tvName = findViewById(R.id.tv_i_name);
        this.tvType = findViewById(R.id.tv_i_type);
        this.tvPrice = findViewById(R.id.tv_i_price);
        this.tvLocation = findViewById(R.id.tv_i_location);
        this.ibEdit = findViewById(R.id.ib_i_edit);
        this.ibChecklist = findViewById(R.id.ib_i_cart);
        this.ibDelete = findViewById(R.id.ib_i_delete);
        this.pbProgress = findViewById(R.id.pb_i_progress);
        this.gComponents = findViewById(R.id.g_i_components);

    }

    /**
     *   This function adds the needed functionalities of the layout objects
     */
    private void initComponents() {
        Intent i = getIntent();

        Ingredient item = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());
        String name = item.getName();
        String title = name;
        if (title.length() > 20)
            title = title.substring(0, 20) + "...";

        String type = item.getType();
        float price = item.getPrice();
        String location = item.getLocation();
        String ingredientId = item.getId();
        Bitmap ingredientBitMap = item.getImg();
        imagePath = item.getImagePath();

        this.tvTitle.setText(title);
        this.ivImg.setImageBitmap(ingredientBitMap);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvPrice.setText(Float.toString(price));
        this.tvLocation.setText(location);
        this.ingredientId = ingredientId;

        initEditButton();
        initChecklistButton();
        initDeleteButton();
        initDialogConfirm();
    }

    /**
     * initializes the confirm delete dialog when clicking delete ingredient button
     */
    private void initDialogConfirm() {
        // Sauce: https://www.youtube.com/watch?v=W4qqTcxqq48
        diaConfirm = new Dialog(IngredientActivity.this);
        diaConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        diaConfirm.setCancelable(true);
        diaConfirm.setContentView(R.layout.layout_confirmation);

        Button btnCancelDelete = diaConfirm.findViewById(R.id.btn_del_cancel);
        Button btnConfirmDelete = diaConfirm.findViewById(R.id.btn_del_confirm);

        //closes confirm dialog when user cancels
        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaConfirm.dismiss();
            }
        });

        //sets functionality for when user confirms to delete ingredient
        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIngredient();
                diaConfirm.dismiss();
            }
        });
    }

    /**
     * sets click listener for edit ingredient button, which redirects user to edit ingredient screen
     */
    private void initEditButton() {
        this.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(IngredientActivity.this, IngredientEditActivity.class);

                newIntent.putExtra(Keys.KEY_INGREDIENT.name(), oldIntent.getSerializableExtra(Keys.KEY_INGREDIENT.name()));
                editActivityResultLauncher.launch(newIntent);
            }
        });
    }

    /**
     * sets click listener for add to checklist button
     */
    private void initChecklistButton() {
        this.ibChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklist();
            }
        });
    }

    /**
     * sets click listener for delete ingredient button
     */
    private void initDeleteButton(){
        this.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("IA ImagePat DelButton", "ImagePath: " + imagePath);
                diaConfirm.show();
            }
        });
    }

    /**
     * Deletes the ingredient from the database
     */
    private void deleteIngredient(){
        this.pbProgress.setVisibility(View.VISIBLE);

        db.getReference(Collections.ingredients.name())
                .child(this.userId)
                .child(this.ingredientId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful())
                    deleteImage();
                else
                    deleteFail();
            }
        });


    }

    /**
     * Deletes the corresponding image of the ingredient from the Cloud Storage
     */
    private void deleteImage() {
        //deletes if the imagePath is not the default ingredient image
        if(!imagePath.equals("ingredient.png")) {
            StorageReference imageReference = storageReference.child(imagePath);
            imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(IngredientActivity.this, "Delete Image Success.", Toast.LENGTH_SHORT).show();
                    deleteSuccess();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("IA Delete Image Fail", "Error: " + e.getMessage());
                        }
                    });
        }
        else
            deleteSuccess();
    }

    /**
     * informs the user that deletion of ingredient is successful
     */
    private void deleteSuccess() {
        pbProgress.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Delete success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * informs the user that deletion of ingredient failed
     */
    private void deleteFail() {
        pbProgress.setVisibility(View.GONE);
        Toast.makeText(IngredientActivity.this, "Delete failed.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Adds the ingredient to the user's checklist
     */
    private void addChecklist() {
        Intent i = getIntent();

        Ingredient ingredient = (Ingredient)i.getSerializableExtra(Keys.KEY_INGREDIENT.name());
        String name = ingredient.getName();
        //String name = i.getStringExtra(KeysOld.KEY_INGREDIENT_NAME);
        String id = UUID.randomUUID().toString().replace("-","").substring(0,8);
        ChecklistItem item = new ChecklistItem(name, false);

        //adds instantiated checklist item to database
        db.getReference(Collections.checklist.name())
                .child(this.userId)
                .child(id)
                .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(IngredientActivity.this, "Add to Checklist Successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(IngredientActivity.this, "Add to Checklist Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}