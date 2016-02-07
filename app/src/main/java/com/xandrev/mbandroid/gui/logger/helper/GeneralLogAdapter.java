package com.xandrev.mbandroid.gui.logger.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.tiles.NotificationLog;

import java.util.List;

public class GeneralLogAdapter extends BaseAdapter {

    private Context context;
    private String[]  traces;

    public GeneralLogAdapter(Context context, String[] traces) {
        this.context = context;
        this.traces = traces;
    }

    @Override
    public int getCount() {
        if(traces != null) {
            return traces.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(traces != null) {
            return traces[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_general_list, parent,false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(traces != null) {
            String log = traces[position];
            if(log != null) {
                String items[] = log.split("-");
                if (items.length > 1) {
                    holder.text.setText(items[1]);
                    holder.date.setText(items[0]);
                }
                holder.itemImage.setImageResource(android.R.drawable.ic_menu_preferences);
            }
        }


        return convertView;
    }

    static class ViewHolder {
        TextView text;
        TextView date;
        ImageView itemImage;
    }
}