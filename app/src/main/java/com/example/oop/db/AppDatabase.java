package com.example.oop.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class, BaseEntity.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductsDao productsDao();
}
