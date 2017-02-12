package com.andreymaryanov.githubsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreymaryanov.githubsearch.R;
import com.andreymaryanov.githubsearch.model.Item;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Andrey on 09.02.2017.
 */

public class DataAdapter extends BaseAdapter {

    private Context context;
    private List<Item> data;

    public DataAdapter(Context context, List<Item> data) {
        this.context = context;
        this.data = data;
    }

    static class ViewHolder {
        ImageView avatar;
        TextView fullName;
        TextView description;
        TextView language;
    }

    @Override
    public Item getItem(int i) {
        return data.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.imageAvatar);
            viewHolder.fullName = (TextView) convertView.findViewById(R.id.textFullName);
            viewHolder.description = (TextView) convertView.findViewById(R.id.textDescription);
            viewHolder.language = (TextView) convertView.findViewById(R.id.textLanguage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fullName.setText(getItem(position).getFullName());
        viewHolder.description.setText(getItem(position).getDescription());
        viewHolder.language.setText("lang:"+getItem(position).getLanguage());

        Glide.with(context).load(getItem(position).getOwner().getAvatarUrl())
                .into(viewHolder.avatar);

        return convertView;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}