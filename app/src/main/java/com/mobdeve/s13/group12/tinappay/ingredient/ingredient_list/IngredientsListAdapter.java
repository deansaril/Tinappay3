package com.mobdeve.s13.group12.tinappay.ingredient.ingredient_list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s13.group12.tinappay.R;
import com.mobdeve.s13.group12.tinappay.ingredient.IngredientActivity;
import com.mobdeve.s13.group12.tinappay.objects.Ingredient;
import com.mobdeve.s13.group12.tinappay.objects.Keys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListViewHolder>{
    private ArrayList<Ingredient> data;

    //Firebase Elements
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    private String userId;

    private FirebaseStorage fbStorage;
    private StorageReference storageReference;

    //TODO DEAN: REMOVE THIS
    //private Uri imageUri;
    private Bitmap imageBitmap;

    public IngredientsListAdapter (ArrayList<Ingredient> data) {
        this.data = data;
        initFirebase();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance("https://tinappay-default-rtdb.asia-southeast1.firebasedatabase.app");
        this.userId = this.mAuth.getCurrentUser().getUid();
        //this.userId = "MuPi9kffqtRAZzVx2e3zizQFHAq2"; // TODO: Remove in final release

        //Firebase Cloud Storage methods
        this.fbStorage = FirebaseStorage.getInstance();
        this.storageReference = fbStorage.getReference();
    }

    @NonNull
    @NotNull
    @Override
    public IngredientsListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.data_linear_item, parent, false);

        IngredientsListViewHolder ingredientsListViewHolder = new IngredientsListViewHolder(itemView);

        ingredientsListViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (v.getContext(), IngredientActivity.class);

                Ingredient item = data.get(ingredientsListViewHolder.getBindingAdapterPosition());
                i.putExtra(Keys.KEY_INGREDIENT.name(), item);

                v.getContext().startActivity(i);
            }
        });

        return ingredientsListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IngredientsListViewHolder holder, int position) {

        //TODO DEAN: data.get().getImagePath. Check if path points to a file. If not, set image path to ingredient.png
        //getImageUri(data.get(position).getImagePath());
        //holder.setItemImage(imageUri);
        //holder.setItemImage(data.get(position).getImageId());

        //gets the bitmap of the image of the currently selected ingredient
        getImageBitmap(data.get(position).getImagePath());
        Log.v("DATA IMAGEPATH", "Data Position: " + position + "| Image Path: "+ data.get(position).getImagePath());
        //sets the bitmap of the imageView using the bitmap retrieved from getImageBitmap()
        holder.setItemImage(imageBitmap);

        holder.setItemField1(data.get(position).getName());
        holder.setItemField2(data.get(position).getType());
        holder.setItemField3(Float.toString(data.get(position).getPrice()));
        holder.setItemField4(data.get(position).getLocation());
    }

    private void getImageBitmap(String imagePath){
        //maximum number of bytes of image
        long MAXBYTES = 1024*1024;
        StorageReference imageReference = storageReference.child(imagePath);

        imageReference.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                String errorMessage = e.getMessage();
                Log.v("ERROR MESSAGE", "ERROR: " + imagePath + " " + errorMessage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setData (ArrayList<Ingredient> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
