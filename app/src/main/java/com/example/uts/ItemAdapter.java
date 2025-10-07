package com.example.uts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bendaku.model.Item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost_found, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemImage;
        private TextView tvItemName;
        private TextView tvItemDescription;
        private TextView tvItemLocation;
        private TextView tvItemDate;
        private TextView tvItemType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            tvItemLocation = itemView.findViewById(R.id.tvItemLocation);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
            tvItemType = itemView.findViewById(R.id.tvItemType);
        }

        public void bind(Item item, OnItemClickListener listener) {
            tvItemName.setText(item.getName());
            tvItemDescription.setText(item.getDescription());
            tvItemLocation.setText(item.getLocation());

            // Format date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                tvItemDate.setText(item.getDateTime());
            } catch (Exception e) {
                tvItemDate.setText(item.getDateTime());
            }

            // Set type with color
            if ("lost".equals(item.getType())) {
                tvItemType.setText("HILANG");
                tvItemType.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            } else {
                tvItemType.setText("DITEMUKAN");
                tvItemType.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            }

            // Load image with Glide
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(ivItemImage);
            } else {
                ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
