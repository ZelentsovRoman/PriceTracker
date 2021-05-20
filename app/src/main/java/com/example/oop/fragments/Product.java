package com.example.oop.fragments;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oop.R;
import com.example.oop.db.AppDatabase;
import com.example.oop.db.BaseEntity;
import com.example.oop.db.ProductsDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

import im.dacer.androidcharts.LineView;


public class Product extends Fragment implements View.OnClickListener{
    private AppDatabase database;
    public ProductsDao productsDao;
    TextView text;
    Button yes,no;
    FloatingActionButton delete;
    HorizontalScrollView horizontalScrollView;
    public String name;
    public String del;
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
        name = argument;
        Success.addTable(name,database);
        BaseEntity model = Success.getLast(name,database);
        del = model.getUrl();
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
        horizontalScrollView = view.findViewById(R.id.horizontalScrollViewFloat);
        horizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
            }
        },100);
        delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(this);
        yes = view.findViewById(R.id.yes);
        yes.setOnClickListener(this);
        no = view.findViewById(R.id.no);
        no.setOnClickListener(this);
        text = view.findViewById(R.id.text);
        text.setVisibility(View.GONE);
        yes.setVisibility(View.GONE);
        no.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete: {
                delete.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                yes.setVisibility(View.VISIBLE);
                no.setVisibility(View.VISIBLE);
            } break;
            case R.id.no: {
                text.setVisibility(View.GONE);
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
            } break;
            case R.id.yes: {
                Success.deleteTable(name,database);
                productsDao.delete(del);
                text.setVisibility(View.GONE);
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                Navigation.findNavController(v).navigate(ProductDirections.actionProductToSuccess2());
            } break;
        }
    }
}