package com.example.oop.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductsDao {
    @Update
    void updateProduct(Product product);
    @Query("SELECT * FROM product")
    List<Product> getProducts();
    @Query("SELECT * FROM product WHERE Url=:url")
    List<Product> getByUrl(String url);
    @Insert
    void insert(Product product);
    @Query("DELETE FROM product WHERE Url=:url")
    void delete(String url);
}
