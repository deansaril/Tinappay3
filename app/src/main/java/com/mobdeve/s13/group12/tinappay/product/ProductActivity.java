package com.mobdeve.s13.group12.tinappay.product;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group12.tinappay.ProgressBarRunnable;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.objects.ChecklistItem;
import com.mobdeve.s13.group12.tinappay.objects.Collections;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;
import com.mobdeve.s13.group12.tinappay.objects.Product;
import com.mobdeve.s13.group12.tinappay.objects.ProductIngredient;
import com.mobdeve.s13.group12.tinappay.product.product_modify.ProductEditActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This activity handles the displaying of product information
 */
public class ProductActivity extends AppCompatActivity {

    /* Class variables */
    // Activity elements
    private ConstraintLayout clLoad;
    private TextView tvTitle;
    private ImageButton ibSettings;
    private ImageButton ibCart;
    private ImageButton ibDelete;
    private Dialog diaConfirm;
    private ScrollView svProduct;
    private ImageView ivImg;
    private TextView tvName;
    private TextView tvType;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvIngredients;
    private RecyclerView rvIngredientPricesList;

    // Loading screen
    private ProgressBar pbLoad;
    private TextView tvLoad;
    private long curProgress, totalProgress;

    // RecyclerView
    private LinearLayoutManager llmManager;
    private HashMap<String, Object> data;
    private HashMap<String, Integer> quantities;
    private ProductAdapter productAdapter;

    // Back-end data
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private String userId;
    private String itemId;

    // Final variables
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);

            Bundle bundle = message.getData();

            // Set current progress
            int progress = bundle.getInt(Keys.KEY_LOAD.name());
            pbLoad.setProgress(progress);

            // If all items have been queried, proceed to display
            if (pbLoad.getProgress() == 100) {
                clLoad.setVisibility(View.GONE);
                loadList();
                productAdapter.setData(data);
                svProduct.setVisibility(View.VISIBLE);
            }
        }
    };

    private ActivityResultLauncher editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        setIntent(i);

                        // Upon receiving edits, update UI
                        loadDetails();
                        fetchIngredients();
                    }
                }
            }
    );



    /* Function overrides */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initFirebase();
        bindComponents();
        initComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clLoad.setVisibility(View.VISIBLE);
        svProduct.setVisibility(View.GONE);
        fetchIngredients();
    }



    /* Class functions */
    /**
     * Initializes connection to firebase database
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        //this.userId = this.mAuth.getCurrentUser().getUid();
        this.userId = "BUvwKWF7JDa8GSbqtUcJf8dYcJ42"; // TODO: Remove in final release
    }

    /**
     * Retrieves activity elements from layout and binds them to the activity
     */
    private void bindComponents() {
        this.tvTitle = findViewById(R.id.tv_p_title);
        this.ibSettings = findViewById(R.id.ib_p_settings);
        this.ibCart = findViewById(R.id.ib_p_cart);
        this.ibDelete = findViewById(R.id.ib_p_delete);
        this.ivImg = findViewById(R.id.iv_p_img);
        this.tvName = findViewById(R.id.tv_p_name);
        this.tvType = findViewById(R.id.tv_p_type);
        this.tvPrice = findViewById(R.id.tv_p_price);
        this.tvDescription = findViewById(R.id.tv_p_description);
        this.tvIngredients = findViewById(R.id.tv_p_ingredients);
        this.svProduct = findViewById(R.id.sv_product);
        this.rvIngredientPricesList = findViewById(R.id.rv_p_ingredients);
    }

    /**
     * Initializes variables used in the activity
     * Initializes functionality of activity
     */
    private void initComponents() {
        // Loading screen
        clLoad = findViewById(R.id.cl_p_loading);
        pbLoad = findViewById(R.id.pb_loading);
        tvLoad = findViewById(R.id.tv_l_description);

        // Local data
        data = new HashMap<>();
        quantities = new HashMap<>();
        curProgress = 0;

        loadDetails();
        initEditButton();
        initCartButton();
        initDialogConfirm();
        initDeleteButton();
        initRecyclerView();
    }

    /**
     * Initializes button for moving to product edit actiivty
     */
    private void initEditButton() {
        this.ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                Intent newIntent = new Intent(ProductActivity.this, ProductEditActivity.class);

                newIntent.putExtra(Keys.KEY_PRODUCT.name(), oldIntent.getSerializableExtra(Keys.KEY_PRODUCT.name()));

                editActivityResultLauncher.launch(newIntent);
            }
        });
    }

    /**
     * Initializing button for adding product ingredients to checklist
     */
    private void initCartButton() {
        this.ibCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklist();
            }
        });
    }

    /**
     * Initializes dialog for delete confirmation
     */
    private void initDialogConfirm() {
        // Sauce: https://www.youtube.com/watch?v=W4qqTcxqq48
        diaConfirm = new Dialog(ProductActivity.this);
        diaConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        diaConfirm.setCancelable(true);
        diaConfirm.setContentView(R.layout.layout_confirmation);

        Button btnCancelDelete = diaConfirm.findViewById(R.id.btn_del_cancel);
        Button btnConfirmDelete = diaConfirm.findViewById(R.id.btn_del_confirm);

        // Sets listener for cancelling delete
        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaConfirm.dismiss();
            }
        });

        // Sets listener for submitting delete
        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSuccess();
                diaConfirm.dismiss();
            }
        });
    }

    /**
     * Initializes listener for displaying delete confirmation
     */
    private void initDeleteButton() {
        this.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaConfirm.show();
            }
        });
    }

    /**
     * Initializes functionality recycler view (Layout and adapter)
     */
    private void initRecyclerView() {
        this.llmManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvIngredientPricesList.setLayoutManager(this.llmManager);

        this.productAdapter = new ProductAdapter(this.data);
        this.rvIngredientPricesList.setAdapter(this.productAdapter);
    }

    /**
     * Fetches ingredients and their prices from database
     */
    private void fetchIngredients() {
        curProgress = 0;
        totalProgress = quantities.size();

        pbLoad.setProgress(0);
        tvLoad.setText(R.string.fetch_prices);

        // For each ingredient, fetch its name and price
        for (Object ingredientId : quantities.keySet())
            db.getReference(Collections.ingredients.name())
                    .child(this.userId)
                    .child(ingredientId.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    try {
                        // Retrieve item from database
                        Ingredient i = snapshot.getValue(Ingredient.class);

                        // Create item to be transferred across activities
                        String name = i.getName();
                        int quantity = quantities.get(ingredientId);
                        float price = i.getPrice() * quantity;
                        ProductIngredient item = new ProductIngredient(name, quantity, price);

                        // Add item to list of items to display
                        data.put(ingredientId.toString(), item);

                        // Send progressbar value
                        curProgress++;
                        int progress = (int)(100 * (float)curProgress / totalProgress);
                        ProgressBarRunnable runnable = new ProgressBarRunnable(handler, progress);
                        scheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        Log.e ("PA", e.toString());
                    }
                    productAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    /* Note: E/PA: java.lang.NullPointerException:
                            Attempt to invoke virtual method 'int java.lang.Integer.intValue()'
                            on a null object reference
                        Doesn't crash and idk why but don't touch it maybe -Jan
                     */
                    Log.e("PA", "Failed to retrieve items.");
                }
            });
    }

    /**
     * Loads the product information to the UI
     */
    private void loadDetails() {
        Intent i = getIntent();
        Product p = (Product)i.getSerializableExtra(Keys.KEY_PRODUCT.name());

        this.itemId = p.getId();
        Bitmap img = p.getImg();
        String name = p.getName();
        String title = name;
        // Trims title for excess characters
        if (title.length() > 20)
            title = title.substring(0, 20) + "...";
        String type = p.getType();
        String description = p.getDescription();
        this.quantities = p.getIngredients();

        this.tvTitle.setText(title);
        this.ivImg.setImageBitmap(img);
        this.tvName.setText(name);
        this.tvType.setText(type);
        this.tvDescription.setText(description);
    }

    /**
     * Loads information from lists to the UI
     */
    private void loadList() {
        float totalPrice = 0;
        String ingredients = new String();
        int index = 0;

        // Summates price of all ingredients
        for (Object key : quantities.keySet())
            totalPrice += ((ProductIngredient)data.get(key.toString())).getPrice();

        // Concatenates all ingredients
        for (Object key : quantities.keySet()) {
            String ingredient = ((ProductIngredient)data.get(key.toString())).getName();
            ingredients += ingredient;
            if (++index != quantities.size())
                ingredients += ", ";
        }

        tvPrice.setText(String.valueOf(totalPrice));
        tvIngredients.setText(ingredients);
    }

    /**
     * Adds ingredients of the product to checklist
     */
    private void addChecklist() {
        this.pbLoad.setVisibility(View.VISIBLE);

        // For each ingredient of the product
        for (Object ingredient : data.values()) {
            // Creates an item to be stored in the database
            String name = ((ProductIngredient)ingredient).getName();
            name += " x" + ((ProductIngredient)ingredient).getQuantity();
            String id = UUID.randomUUID().toString().replace("-","").substring(0,8);
            ChecklistItem item = new ChecklistItem(name, false);

            // Save item to database
            db.getReference(Collections.checklist.name())
                    .child(this.userId)
                    .child(id)
                    .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        addSuccess();
                    else
                        addFail();
                }
            });
        }
    }

    /**
     * Displays prompt notifying success of adding to checklist
     */
    private void addSuccess() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Product ingredients added to checklist.", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Displays prompt notifying failure of adding to checklist
     */
    private void addFail() {
        this.pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Could not add to checklist.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes product from the database
     */
    private void deleteProduct() {
        this.pbLoad.setVisibility(View.VISIBLE);

        // Remove item from database
        db.getReference(Collections.products.name())
                .child(this.userId)
                .child(this.itemId)
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

    /**
     * Displays prompt notifying success of deleting product
     */
    private void deleteSuccess() {
        pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Delete success.", Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Displays prompt notifying failure of deleting product
     */
    private void deleteFail() {
        pbLoad.setVisibility(View.GONE);
        Toast.makeText(ProductActivity.this, "Delete failed.", Toast.LENGTH_SHORT).show();
    }
}