package com.example.oop.fragments;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oop.R;
import com.example.oop.db.AppDatabase;
import com.example.oop.db.BaseEntity;
import com.example.oop.db.ProductsDao;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import im.dacer.androidcharts.LineView;


public class Product extends Fragment {
    private AppDatabase database;
    public ProductsDao productsDao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String argument = getArguments().getString("url");
        database = Room.databaseBuilder(this.getContext(), AppDatabase.class, "prodDB.db").allowMainThreadQueries().build();
        productsDao = database.productsDao();
        String name = argument;
        Success.addTable(name,database);
        BaseEntity model = Success.getLast(name,database);
        TextView title = view.findViewById(R.id.titlePage);
        title.setText(model.getTitle());
        ImageView imageView = view.findViewById(R.id.imagePrice);
        Picasso.get().load(model.getImage()).into(imageView);
        TextView price = view.findViewById(R.id.pricePage);
        price.setText(model.getPrice()+" ₽");
        LineView lineView = view.findViewById(R.id.line_view);
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY);
        lineView.setBottomTextList(Success.getDates(name,database));
        lineView.setColorArray(new int[]{Color.BLACK,Color.GREEN,Color.GRAY,Color.CYAN});
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
        arrayLists.add(Success.getPrices(name,database));
        lineView.setDataList(arrayLists);
    }
}