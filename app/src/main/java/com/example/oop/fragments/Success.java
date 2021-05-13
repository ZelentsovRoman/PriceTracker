package com.example.oop.fragments;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.oop.R;
import com.example.oop.db.AppDatabase;
import com.example.oop.db.BaseEntity;
import com.example.oop.db.Product;
import com.example.oop.db.ProductsDao;
import com.example.oop.service.Service;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Success extends Fragment implements View.OnClickListener {
    Button add;
    FloatingActionButton plus;
    public EditText editText;
    public TextView textlink;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Product>  arrayList = new ArrayList<>();
    private DataAdapter adapter;
    RecyclerView recyclerView;
    private AppDatabase database;
    public ProductsDao productsDao;
    public int status = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_items, container, false);
    }

    public AppDatabase getDatabase() {
        return database;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = Room.databaseBuilder(this.getContext(), AppDatabase.class, "prodDB.db").allowMainThreadQueries().build();
        productsDao = database.productsDao();
        adapter = new DataAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        plus = view.findViewById(R.id.plus);
        plus.setOnClickListener(this);
        add = view.findViewById(R.id.addNew);
        add.setOnClickListener(this);
        add.setVisibility(View.GONE);
        textlink = view.findViewById(R.id.textlink);
        textlink.setVisibility(View.GONE);
        editText = view.findViewById(R.id.url);
        editText.setVisibility(View.GONE);
        swipeContainer = view.findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(swipe);
        if(isNetworkConnected(getContext())) {
            database = getDatabase();
            productsDao = database.productsDao();
            List<Product> arrayList = productsDao.getProducts();
            adapter.set(arrayList);
            if (status==0) {
                swipe.onRefresh();
                status++;
            }
        } else {
            database = getDatabase();
            productsDao = database.productsDao();
            List<Product> arrayList = productsDao.getProducts();
            adapter.set(arrayList);
            Toast.makeText(Success.this.getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    SwipeRefreshLayout.OnRefreshListener swipe = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            swipeContainer.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (isNetworkConnected(getContext())) {
                        JobInfo job = new JobInfo.Builder(1, new ComponentName(getContext(), Service.class)).build();
                        JobScheduler scheduler = (JobScheduler) getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        scheduler.schedule(job);
                        database = getDatabase();
                        productsDao = database.productsDao();
                        List<Product> arrayList = productsDao.getProducts();
                        adapter.set(arrayList);
                    } else {
                        Toast.makeText(Success.this.getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    swipeContainer.setRefreshing(false);
                }
            }, 3000);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.plus: {
                if (editText.getVisibility()==View.GONE) {
                    editText.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    textlink.setVisibility(View.VISIBLE);
                }
                else {
                    editText.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    textlink.setVisibility(View.GONE);
                }
            } break;
            case R.id.addNew: {
                database = getDatabase();
                productsDao = database.productsDao();
                if (!productsDao.getByUrl(editText.getText().toString()).isEmpty()){
                    Toast.makeText(this.getActivity(),"Product already added", Toast.LENGTH_LONG).show();
                } else {
                    if (editText.getText().toString().contains("citilink")){
                        AddCTProduct addCTProduct = new AddCTProduct();
                        addCTProduct.execute();
                    } else if (editText.getText().toString().contains("dns-shop")){
                        AddDNSProduct addDNSProduct = new AddDNSProduct();
                        addDNSProduct.execute();
                    } else {
                        Toast.makeText(this.getActivity(),"Invalid link", Toast.LENGTH_LONG).show();
                    }
                }
                editText.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                textlink.setVisibility(View.GONE);
            } break;
        }
    }
    public static boolean addTable(String tableName, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        try {
            sdb.execSQL(BaseEntity.BASETABLE_CREATE_SQL.replace(BaseEntity.BASETABLE_NAME_PLACEHOLDER, tableName));
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }
    public static boolean deleteTable(String tableName, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        try {
            sdb.execSQL(BaseEntity.BASETABLE_DELETE_SQL.replace(BaseEntity.BASETABLE_NAME_PLACEHOLDER, tableName));
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public static void addSomeDataOutsideOfRoom(String tableName, String url, String title, String price, String image, String date, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        BaseEntity.insertRow(sdb,tableName,url,title,price,date,image);
    }
    public static BaseEntity getLast(String tableName, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        return BaseEntity.getLast(sdb,tableName);
    }
    public static ArrayList<String> getDates(String tableName, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        return BaseEntity.getDates(sdb,tableName);
    }
    public static ArrayList<Integer> getPrices(String tableName, AppDatabase mDB) {
        SupportSQLiteDatabase sdb = mDB.getOpenHelper().getWritableDatabase();
        return BaseEntity.getPrices(sdb,tableName);
    }

    public class AddDNSProduct extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(editText.getText().toString()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Product product = new Product();
                product.Url = editText.getText().toString();
                Elements elements = doc.getElementsByClass("product-card-top product-card-top_full");
                product.title = elements.select("h1.product-card-top__title").text();
                Elements script = doc.select("script");
                Pattern p = Pattern.compile("\"(?is)price\":(\\d+)");
                Matcher m = p.matcher(script.html());
                while (m.find()) {
                    product.price = m.group(1);
                }
                p = Pattern.compile("\"(?is)desktop\":\"(.+?)\"");
                m = p.matcher(script.html());
                m.find();
                product.image = m.group(1);
                if (product.price != null && product.title != null && product.image != null) {
                    productsDao.insert(product);
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c);
                    String dynamicTableName = "["+product.Url+"]";
                    addTable(dynamicTableName, database);
                    addSomeDataOutsideOfRoom(dynamicTableName, product.Url, product.title, product.price, product.image, formattedDate,database);
                    return true;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalStateException e){
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
                database = getDatabase();
                productsDao = database.productsDao();
                List<Product> arrayList = productsDao.getProducts();
                adapter.set(arrayList);
                editText.getText().clear();
            }
            else Toast.makeText(getContext(), "Failed to add new product", Toast.LENGTH_SHORT).show();
        }
    }
    public class AddCTProduct extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(editText.getText().toString()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Product product = new Product();
                product.Url = editText.getText().toString();
                product.title=doc.getElementsByClass("Heading Heading_level_1 ProductHeader__title").first().text();
                product.price=doc.getElementsByClass("ProductHeader__price-default_current-price ").first().text();
                product.image=doc.getElementsByClass(" PreviewList__image Image").attr("src");
                if (product.price != null && product.title != null && product.image != null) {
                    productsDao.insert(product);
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(c);
                    String dynamicTableName = "["+product.Url+"]";
                    addTable(dynamicTableName, database);
                    addSomeDataOutsideOfRoom(dynamicTableName, product.Url, product.title, product.price, product.image, formattedDate,database);
                    return true;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalStateException e){
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
                database = getDatabase();
                productsDao = database.productsDao();
                List<Product> arrayList = productsDao.getProducts();
                adapter.set(arrayList);
                editText.getText().clear();
            }
            else Toast.makeText(getContext(), "Failed to add new product", Toast.LENGTH_SHORT).show();
        }
    }
}