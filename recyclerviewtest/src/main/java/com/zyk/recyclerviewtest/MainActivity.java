package com.zyk.recyclerviewtest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

    RecyclerView mRecyclerView;

    private String data[] ={"name","性别","职业","职位","工作年限","工作性质","兴趣爱好","年龄","等等"};
    private String data1[] ={"name1","性别1","职业1","职位1","工作年限1","工作性质1","兴趣爱好1","年龄1","等等1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
//        GridLayoutManager grid = new GridLayoutManager(this, 2);
//        mRecyclerView.setLayoutManager(grid);
        int spanCount = 3;//跟布局里面的spanCount属性是一致的
        int spacing = 5;//每一个矩形的间距
        boolean includeEdge = true;//如果设置成false那边缘地带就没有间距
        //设置每个item间距
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount,spacing,includeEdge));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(
                    R.layout.item, parent, true));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mContent.setText(data[position]);
        }


        @Override
        public int getItemCount() {
            return data.length;
        }
    }
    private class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mContent;
        public TextView mContent1;

        public ViewHolder(View v) {
            super(v);
            mContent = (TextView) v.findViewById(R.id.item_text);
            mContent1 = (TextView) v.findViewById(R.id.item_text1);
        }
    }

}
