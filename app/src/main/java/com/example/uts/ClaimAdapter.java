package com.example.uts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bendaku.model.Claim;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder> {

    public static final int VIEW_TYPE_LIST = 0;
    public static final int VIEW_TYPE_GRID = 1;
    public static final int VIEW_TYPE_CARDVIEW = 2;

    private List<Claim> claims;
    private OnClaimActionListener listener;
    private int layoutMode = VIEW_TYPE_CARDVIEW; // Default to CardView

    public interface OnClaimActionListener {
        void onApprove(Claim claim);
        void onReject(Claim claim);
        void onItemClick(Claim claim);
    }

    public ClaimAdapter(List<Claim> claims, OnClaimActionListener listener) {
        this.claims = claims;
        this.listener = listener;
    }

    public void setLayoutMode(int layoutMode) {
        this.layoutMode = layoutMode;
        notifyDataSetChanged();
    }

    public int getLayoutMode() {
        return layoutMode;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_LIST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_claim_list, parent, false);
                break;
            case VIEW_TYPE_GRID:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_claim_grid, parent, false);
                break;
            case VIEW_TYPE_CARDVIEW:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_claim_cardview, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Claim claim = claims.get(position);
        holder.bind(claim, listener);
    }

    @Override
    public int getItemCount() {
        return claims.size();
    }

    public void updateClaims(List<Claim> newClaims) {
        this.claims = newClaims;
        notifyDataSetChanged();
    }

    public void removeClaimByDocumentId(String documentId) {
        if (documentId == null || documentId.isEmpty()) return;
        for (int i = 0; i < claims.size(); i++) {
            Claim claim = claims.get(i);
            if (documentId.equals(claim.getDocumentId())) {
                claims.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProofImage;
        private TextView tvItemName;
        private TextView tvClaimerName;
        private TextView tvClaimDescription;
        private MaterialButton btnApprove;
        private MaterialButton btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProofImage = itemView.findViewById(R.id.ivProofImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvClaimerName = itemView.findViewById(R.id.tvClaimerName);
            tvClaimDescription = itemView.findViewById(R.id.tvClaimDescription);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        public void bind(Claim claim, OnClaimActionListener listener) {
            // Display item name if available, otherwise show item ID
            String itemDisplay = claim.getItemName() != null && !claim.getItemName().isEmpty() 
                    ? claim.getItemName() 
                    : (claim.getItemId() != null ? "Item ID: " + claim.getItemId() : "Item tidak diketahui");
            tvItemName.setText(itemDisplay);
            tvClaimerName.setText("Diklaim oleh: " + (claim.getClaimerName() != null ? claim.getClaimerName() : "N/A"));
            String description = claim.getDescription() != null && !claim.getDescription().isEmpty() 
                    ? claim.getDescription() 
                    : "Tidak ada deskripsi";
            tvClaimDescription.setText("Deskripsi : " + description);

            // Load proof image
            String imageUrl = claim.getProofImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(ivProofImage);
                ivProofImage.setVisibility(View.VISIBLE);
            } else {
                ivProofImage.setVisibility(View.GONE);
            }

            // Handle item click to open detail
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(claim);
                }
            });

            // Set button listeners only if buttons exist (not in list mode)
            if (btnApprove != null) {
                btnApprove.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onApprove(claim);
                    }
                });
            }

            if (btnReject != null) {
                btnReject.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReject(claim);
                    }
                });
            }
        }
    }
}
