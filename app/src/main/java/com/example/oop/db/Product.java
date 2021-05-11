package com.example.oop.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Product {
    @PrimaryKey
    @NotNull
    public String Url;
    public String title;
    public String image;
    public String price;
}
