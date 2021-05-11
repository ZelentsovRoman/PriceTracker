package com.example.oop.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oop.R;
import com.example.oop.db.Product;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView title,price;
    private String url;

    public ViewHolder(final View itemView) {
        super(itemView);
        imageView =itemView.findViewById(R.id.iconitem);
        title = itemView.findViewById(R.id.titleitem);
        price = itemView.findViewById(R.id.priceitem);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(SuccessDirections.actionSuccessToProduct().setUrl(url));
            }
        });
    }
    public void bind(Product product){
        Picasso.get().load(product.image).into(imageView);
        title.setText(product.title);
        price.setText(product.price+" ₽");
        url = "["+product.Url+"]";
    }
}
