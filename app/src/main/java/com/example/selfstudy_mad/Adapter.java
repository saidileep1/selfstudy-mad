package com.example.selfstudy_mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class Adapter extends PagerAdapter {

    private ArrayList<Integer> banner;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(ArrayList<Integer> banner, Context context) {
        this.banner=banner;
        this.context = context;
    }

    @Override
    public int getCount() { return banner.size(); }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.banner_cards, container, false);

        ImageView imageView;
        imageView = view.findViewById(R.id.image);
        imageView.setImageResource(banner.get(position));

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
