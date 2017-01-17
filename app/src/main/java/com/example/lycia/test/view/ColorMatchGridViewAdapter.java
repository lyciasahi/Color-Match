package com.example.lycia.test.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.lycia.test.MainActivity;
import com.example.lycia.test.R;
import com.example.lycia.test.persistance.ReadWriteData;

import java.io.IOException;


public class ColorMatchGridViewAdapter extends BaseAdapter {

    private Context context;
    private static int colors[] = {
            R.mipmap.p0,
            R.mipmap.p1,
            R.mipmap.p2,
            R.mipmap.p3,
            R.mipmap.p4,
            R.mipmap.p5,
            R.mipmap.p6
    };
    private static int images[] = new int[100];

    public ColorMatchGridViewAdapter(Context context) {
        this.context = context;

        if(MainActivity.loadPlay) {
            ReadWriteData.openInputStream("gridStatus", context);
            ReadWriteData.readFromFile();
            for(int i = 0; i < 100; i++) {
                try {
                    images[i] = Integer.parseInt(ReadWriteData.getBufferedReader().readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ReadWriteData.closeinputStream();
        }
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(4, 4, 4, 4);

        if(!MainActivity.loadPlay) {
            int count = 0;

            int rand = (int) (Math.random() * 10000) % 5;
            int rand_color = (int) (Math.random() * 10000) % 7;
            if (rand != 0 || count >= 20) {
                imageView.setImageResource(colors[rand_color]);
                imageView.setId(colors[rand_color]);
            } else {
                imageView.setImageResource(0);
                imageView.setId(0);
                count++;
            }
        } else {
            imageView.setImageResource(images[position]);
            imageView.setId(images[position]);
        }
        return imageView;
    }
}
