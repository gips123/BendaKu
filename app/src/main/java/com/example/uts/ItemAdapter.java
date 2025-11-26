package com.example.uts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bendaku.model.Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
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
        private TextView tvDescription;
        private TextView tvLocation;
        private TextView tvDate;
        private TextView tvStatus;
        private MaterialCardView statusBadge;
        private MaterialButton btnContact;
        private MaterialButton btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            btnContact = itemView.findViewById(R.id.btnContact);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        public void bind(Item item, OnItemClickListener listener) {
            tvItemName.setText(item.getName() != null ? item.getName() : "");
            tvDescription.setText(item.getDescription() != null ? item.getDescription() : "");
            tvLocation.setText(item.getLocation() != null ? item.getLocation() : "");

            try {
                if (item.getDateTime() != null && !item.getDateTime().isEmpty()) {
                    tvDate.setText(getRelativeTimeString(item.getDateTime()));
                } else {
                    tvDate.setText("");
                }
            } catch (Exception e) {
                tvDate.setText(item.getDateTime() != null ? item.getDateTime() : "");
            }

            String itemType = item.getType() != null ? item.getType() : "lost";
            if ("lost".equals(itemType)) {
                tvStatus.setText("HILANG");
                statusBadge.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.error));
            } else {
                tvStatus.setText("DITEMUKAN");
                statusBadge.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
            }

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                if (item.getImageUrl().startsWith("drawable://")) {
                    String drawableName = item.getImageUrl().replace("drawable://", "");
                    int drawableId = getDrawableResourceId(drawableName);
                    if (drawableId != 0) {
                        Glide.with(itemView.getContext())
                                .load(drawableId)
                                .centerCrop()
                                .placeholder(R.color.surface_variant)
                                .error(R.color.surface_variant)
                                .into(ivItemImage);
                    } else {
                        ivItemImage.setImageResource(R.color.surface_variant);
                    }
                } else {
                    Glide.with(itemView.getContext())
                            .load(item.getImageUrl())
                            .centerCrop()
                            .placeholder(R.color.surface_variant)
                            .error(R.color.surface_variant)
                            .into(ivItemImage);
                }
            } else {
                ivItemImage.setImageResource(R.color.surface_variant);
            }

            btnContact.setOnClickListener(v -> {
                if (listener != null) {
                }
            });

            btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }

        private int getDrawableResourceId(String drawableName) {
            switch (drawableName) {
                case "dompet":
                    return R.drawable.dompet;
                case "hoodie":
                    return R.drawable.hoodie;
                case "kunci":
                    return R.drawable.kunci;
                case "powerbank":
                    return R.drawable.powerbank;
                case "samsung":
                    return R.drawable.samsung;
                case "sus":
                    return R.drawable.sus;
                default:
                    return R.drawable.ic_item;
            }
        }

        private String getRelativeTimeString(String dateTime) {
            return dateTime;
        }
    }
}
