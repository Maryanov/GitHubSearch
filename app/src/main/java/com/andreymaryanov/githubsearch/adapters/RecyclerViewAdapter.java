package com.andreymaryanov.githubsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andreymaryanov.githubsearch.R;
import com.andreymaryanov.githubsearch.model.Item;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Andrey on 12.02.2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;

    public RecyclerViewAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.fullName.setText(items.get(i).getFullName());
        viewHolder.description.setText(items.get(i).getDescription());
        viewHolder.language.setText("lang:"+items.get(i).getLanguage());
        if (i==(items.size()-1) && (items.size()%10)==0){
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.fullName.setVisibility(View.GONE);
            viewHolder.description.setVisibility(View.GONE);
            viewHolder.language.setVisibility(View.GONE);
            viewHolder.avatar.setVisibility(View.GONE);
        }
         else {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.fullName.setVisibility(View.VISIBLE);
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.language.setVisibility(View.VISIBLE);
            viewHolder.avatar.setVisibility(View.VISIBLE);
        }
        Glide.with(context).load(items.get(i).getOwner().getAvatarUrl())
                .into(viewHolder.avatar);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView fullName;
        private TextView description;
        private TextView language;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.imageAvatar);
            fullName = (TextView) itemView.findViewById(R.id.textFullName);
            description = (TextView) itemView.findViewById(R.id.textDescription);
            language = (TextView) itemView.findViewById(R.id.textLanguage);
            progressBar = (ProgressBar) itemView.findViewById(R.id.status_loading);
        }
    }

}
