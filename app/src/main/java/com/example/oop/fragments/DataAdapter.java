package com.example.oop.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oop.R;
import com.example.oop.db.Product;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<com.example.oop.db.Product> products = new ArrayList<>();

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }
    @Override
    public int getItemCount() {
        return products.size();
    }
    public void set(List<Product> list){
        this.products.clear();
        this.products.addAll(list);
        notifyDataSetChanged();
    }
}
