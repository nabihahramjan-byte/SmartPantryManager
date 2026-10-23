package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter for the Suggested Recipes list.
public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Recipe> recipeData;
    private View.OnClickListener mOnItemClickListener;

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView textRecipeName;
        public TextView textRecipeInfo;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipeName = itemView.findViewById(R.id.textViewRecipeName);
            textRecipeInfo = itemView.findViewById(R.id.textViewRecipeInfo);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    public RecipeAdapter(ArrayList<Recipe> arrayList) {
        recipeData = arrayList;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder rvh = (RecipeViewHolder) holder;
        Recipe recipe = recipeData.get(position);
        rvh.textRecipeName.setText(recipe.getName());
        rvh.textRecipeInfo.setText("Uses " + recipe.getIngredients().size() + " ingredients - tap to view");
    }

    @Override
    public int getItemCount() {
        return recipeData.size();
    }
}
