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

import java.util.ArrayList;
import java.util.List;

public class NotificationLogAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationLog> traces;

    public NotificationLogAdapter(Context context, List<NotificationLog> traces) {
        this.context = context;
        this.traces = traces;
    }

    @Override
    public int getCount() {
        return traces.size();
    }

    @Override
    public Object getItem(int position) {
        return traces.get(position);
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
            convertView = inflater.inflate(R.layout.layout_notification_list, parent,false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NotificationLog log = traces.get(position);
        holder.text.setText(log.getText());
        holder.date.setText(log.getTimestamp().toString());
        holder.title.setText(log.getTitle());
        Drawable icon = null;
        try {
            icon = convertView.getContext().getPackageManager().getApplicationIcon(log.getPackage());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.itemImage.setImageDrawable(icon);


        return convertView;
    }

    static class ViewHolder {
        TextView text;
        TextView title;
        TextView date;
        ImageView itemImage;
    }
}