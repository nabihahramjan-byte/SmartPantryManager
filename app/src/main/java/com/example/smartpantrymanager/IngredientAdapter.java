package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Adapter: takes the list of Ingredient objects and shows each one in the RecyclerView.
public class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Ingredient> ingredientData;
    private View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private boolean showExpiryAlerts;
    private Context parentContext;

    // The ViewHolder holds the widgets of ONE row (item_ingredient.xml).
    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textQuantity;
        public TextView textExpiry;
        public Button buttonDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textViewItemName);
            textQuantity = itemView.findViewById(R.id.textViewItemQuantity);
            textExpiry = itemView.findViewById(R.id.textViewItemExpiry);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteItem);
            itemView.setTag(this);                          // lets the activity find out which row was tapped
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    public IngredientAdapter(ArrayList<Ingredient> arrayList, Context context) {
        ingredientData = arrayList;
        parentContext = context;
        isDeleting = false;
        showExpiryAlerts = true;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void setDelete(boolean b) {
        isDeleting = b;
    }

    public void setShowExpiryAlerts(boolean b) {
        showExpiryAlerts = b;
    }

    // Creates the visual row from the XML layout.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(v);
    }

    // Fills one row with the data of the ingredient at this position.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final IngredientViewHolder ivh = (IngredientViewHolder) holder;
        Ingredient item = ingredientData.get(position);

        ivh.textName.setText(item.getName());
        ivh.textQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        // Expiry text
        ivh.textExpiry.setTextColor(Color.GRAY);
        if (item.getExpiry().isEmpty()) {
            ivh.textExpiry.setText("No expiry date");
        } else {
            String text = "Expires: " + item.getExpiry();
            int daysLeft = daysUntil(item.getExpiry());
            if (showExpiryAlerts && daysLeft != Integer.MAX_VALUE) {
                if (daysLeft < 0) {
                    text = "EXPIRED: " + item.getExpiry();
                    ivh.textExpiry.setTextColor(Color.RED);
                } else if (daysLeft <= 3) {
                    text = text + " (expiring soon!)";
                    ivh.textExpiry.setTextColor(Color.RED);
                }
            }
            ivh.textExpiry.setText(text);
        }

        // The Delete button only shows when "Delete mode" is switched on.
        if (isDeleting) {
            ivh.buttonDelete.setVisibility(View.VISIBLE);
            ivh.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(ivh.getAdapterPosition());
                }
            });
        } else {
            ivh.buttonDelete.setVisibility(View.INVISIBLE);
        }
    }

    // Tells the RecyclerView how many rows there are.
    @Override
    public int getItemCount() {
        return ingredientData.size();
    }

    // Deletes from the database first, then from the list on screen.
    private void deleteItem(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        Ingredient ingredient = ingredientData.get(position);
        PantryDataSource ds = new PantryDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteIngredient(ingredient.getId());
            ds.close();
            if (didDelete) {
                ingredientData.remove(position);
                notifyDataSetChanged();
                if (parentContext instanceof MainActivity) {
                    ((MainActivity) parentContext).updateEmptyMessage();
                }
            } else {
                Toast.makeText(parentContext, "Delete failed!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(parentContext, "Delete failed!", Toast.LENGTH_LONG).show();
        }
    }

    // 2.0 -> "2",  2.5 -> "2.5"
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    // Number of days from today until the expiry date (negative = already expired).
    // Returns Integer.MAX_VALUE if the date cannot be read.
    private int daysUntil(String expiry) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date expiryDate = format.parse(expiry);
            Date today = format.parse(format.format(new Date()));   // today at midnight
            long diffMillis = expiryDate.getTime() - today.getTime();
            return (int) Math.round(diffMillis / (24.0 * 60 * 60 * 1000));
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
