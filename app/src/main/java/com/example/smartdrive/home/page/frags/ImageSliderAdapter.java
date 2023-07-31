package com.example.smartdrive.home.page.frags;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdrive.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.ImageSliderViewHolder> {

    private List<String> imageUrls;
    private List<String> descriptions;

    public ImageSliderAdapter() {
        // Initialize your list of image URLs
        imageUrls = new ArrayList<>();
        imageUrls.add("android.resource://com.example.smartdrive/" + R.drawable.image_slider11);
        imageUrls.add("android.resource://com.example.smartdrive/" + R.drawable.image_slider22);
        imageUrls.add("android.resource://com.example.smartdrive/" + R.drawable.image_slider33);
        imageUrls.add("android.resource://com.example.smartdrive/" + R.drawable.image_slider44);
        // Add more image URLs as needed

        // Initialize your list of descriptions
        descriptions = new ArrayList<>();
        descriptions.add("consulter le statistiques de congestion");
        descriptions.add("consulter les smart feux proches de vous");
        descriptions.add("vous pouvez activer activer le mode d'urgence si necessaire");
        descriptions.add("vous pouvez connaitre la situation routiere actuelle");
        // Add more descriptions as needed
    }

    @Override
    public ImageSliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_layout, null);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageSliderViewHolder viewHolder, int position) {
        // Load and display the image using a library like Picasso or Glide
        Picasso.get().load(imageUrls.get(position)).into(viewHolder.imageView);

        // Set the description text
        viewHolder.descriptionTextView.setText(descriptions.get(position));
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    static class ImageSliderViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;

        public ImageSliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
