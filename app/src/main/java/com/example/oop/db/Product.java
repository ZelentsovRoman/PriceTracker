package com.example.oop.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Product {
    @PrimaryKey
    @NotNull
    public String Url;
    @NotNull
    public String title;
    @NotNull
    public String image;
    @NotNull
    public String price;
}
