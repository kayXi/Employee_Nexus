package com.example.employee_nexus.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.employee_nexus.ui.DayBean;

import java.util.List;

public class DayAdapter extends BaseAdapter {

    private List<DayBean> list;
    private Context context;

    public DayAdapter(List<DayBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DayBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TextView textView;
        if (view == null) {
            textView = new TextView(context);
            textView.setPadding(5, 5, 5, 5);
            view = textView;
        } else {
            textView = (TextView) view;
        }

        DayBean bean = getItem(position);

        textView.setText(bean.getDay() + "");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        if (bean.isCurrentDay()) {
            textView.setBackgroundColor(Color.parseColor("#fd5f00"));
            textView.setTextColor(Color.WHITE);
        } else if (bean.isCurrentMonth()) {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setBackgroundColor(Color.parseColor("#aaaaaa"));
            textView.setTextColor(Color.BLACK);
        }
        return textView;
    }
}

