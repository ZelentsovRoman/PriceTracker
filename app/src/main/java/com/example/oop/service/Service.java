package com.example.oop.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.oop.MainActivity;
import com.example.oop.R;
import com.example.oop.db.AppDatabase;
import com.example.oop.db.BaseEntity;
import com.example.oop.db.Product;
import com.example.oop.db.ProductsDao;
import com.example.oop.fragments.Success;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Service extends JobService {
    private static AppDatabase database;
    public static ProductsDao productsDao;
    public static String url;
    public static int status;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return getData(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean getData(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "prodDB.db").allowMainThreadQueries().build();
        productsDao = database.productsDao();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        List<Product> productArrayList = productsDao.getProducts();
        boolean stat = false;
        if (isNetworkConnected(context)) {
            for (Product prod : productArrayList) {
                url = prod.Url;
                Product newProd = new Product();
                if (url.contains("citilink")) {
                    MyTask1 myTask1 = new MyTask1();
                    myTask1.execute();
                    try {
                        newProd = myTask1.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (url.contains("dns-shop")) {
                    MyTask myTask = new MyTask();
                    myTask.execute();
                    try {
                        newProd = myTask.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String dynamicTableName = "[" + url + "]";
                Success.addTable(dynamicTableName, database);
                BaseEntity model = Success.getLast(dynamicTableName, database);
                String dateModel = model.getDate();
                if (!prod.price.equals(newProd.price) || !dateModel.equals(formattedDate)) {
                    status = 1;
                    Success.addTable(dynamicTableName, database);
                    Success.addSomeDataOutsideOfRoom(dynamicTableName, newProd.Url, newProd.title, newProd.price, newProd.image, formattedDate, database);
                    productsDao.updateProduct(newProd);
                }
            }
            if (!MainActivity.isAppForeground() && status != 0) {
                sendNotification(context);
                stat=true;
            }
        } return stat;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification(Context context) {
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, new Intent(
                        context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.WHITE);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{0, 500, 500, 1000});
        notificationManager.createNotificationChannel(notificationChannel);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pendingIntent)
                .setContentTitle("Prices updated")
                .setContentText("Tap to open application");
        notificationManager.notify(56, notificationBuilder.build());
    }

    public static class MyTask extends AsyncTask<Void, Void, Product> {
        @Override
        protected Product doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Product product = new Product();
            product.Url = url;
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
                return product;
            }
            else return null;
        }
    }
    public static class MyTask1 extends AsyncTask<Void, Void, Product> {
        @Override
        protected Product doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Product product = new Product();
            product.Url = url;
            product.title=doc.getElementsByClass("Heading Heading_level_1 ProductHeader__title").first().text();
            product.price=doc.getElementsByClass("ProductHeader__price-default_current-price ").first().text();
            product.image=doc.getElementsByClass(" PreviewList__image Image").attr("src");
            if (product.price != null && product.title != null && product.image != null) {
                return product;
            }
            else return null;
        }
    }
}