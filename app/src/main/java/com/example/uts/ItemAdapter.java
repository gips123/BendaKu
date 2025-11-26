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
        private TextView tvCategoryIcon;
        private MaterialCardView statusBadge;
        private MaterialButton btnContact;
        private MaterialButton btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views with correct IDs from new layout
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            btnContact = itemView.findViewById(R.id.btnContact);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        public void bind(Item item, OnItemClickListener listener) {
            // Set basic item data with null safety
            tvItemName.setText(item.getName() != null ? item.getName() : "");
            tvDescription.setText(item.getDescription() != null ? item.getDescription() : "");
            tvLocation.setText(item.getLocation() != null ? item.getLocation() : "");

            // Format and set date
            try {
                if (item.getDateTime() != null && !item.getDateTime().isEmpty()) {
                tvDate.setText(getRelativeTimeString(item.getDateTime()));
                } else {
                    tvDate.setText("");
                }
            } catch (Exception e) {
                tvDate.setText(item.getDateTime() != null ? item.getDateTime() : "");
            }

            // Set status badge with appropriate colors
            String itemType = item.getType() != null ? item.getType() : "lost";
            if ("lost".equals(itemType)) {
                tvStatus.setText("HILANG");
                statusBadge.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.error));
                tvCategoryIcon.setText(getCategoryIcon(item.getName(), "lost"));
            } else {
                tvStatus.setText("DITEMUKAN");
                statusBadge.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
                tvCategoryIcon.setText(getCategoryIcon(item.getName(), "found"));
            }

            // Load image with proper drawable handling
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                if (item.getImageUrl().startsWith("drawable://")) {
                    // Handle local drawable images
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
                    // Handle URL images
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

            // Set button click listeners
            btnContact.setOnClickListener(v -> {
                // Handle contact functionality
                if (listener != null) {
                    // You can implement contact logic here
                }
            });

            btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // Set click listener for the whole item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }

        private int getDrawableResourceId(String drawableName) {
            // Map drawable names to resource IDs
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
                    return R.drawable.ic_item; // Default fallback
            }
        }

        private String getCategoryIcon(String itemName, String type) {
            // Return category icons based on item name/type
            if (itemName == null || itemName.isEmpty()) {
                return "📦"; // Default icon for null/empty name
            }
            
            String name = itemName.toLowerCase();
            if (name.contains("dompet") || name.contains("wallet")) {
                return "💳";
            } else if (name.contains("laptop") || name.contains("computer")) {
                return "💻";
            } else if (name.contains("kunci") || name.contains("key")) {
                return "🔑";
            } else if (name.contains("handphone") || name.contains("phone") || name.contains("samsung")) {
                return "📱";
            } else if (name.contains("jaket") || name.contains("hoodie")) {
                return "👕";
            } else if (name.contains("powerbank") || name.contains("charger")) {
                return "🔋";
            } else {
                return "📦"; // Default icon
            }
        }

        private String getRelativeTimeString(String dateTime) {
            // Simple relative time formatting
            return dateTime; // For now, just return the original date
        }
    }
}
