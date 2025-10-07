package com.example.uts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bendaku.model.Claim;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder> {

    private List<Claim> claims;
    private OnClaimActionListener listener;

    public interface OnClaimActionListener {
        void onApprove(Claim claim);
        void onReject(Claim claim);
    }

    public ClaimAdapter(List<Claim> claims, OnClaimActionListener listener) {
        this.claims = claims;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_claim, parent, false);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvClaimerName;
        private TextView tvClaimDescription;
        private MaterialButton btnApprove;
        private MaterialButton btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvClaimerName = itemView.findViewById(R.id.tvClaimerName);
            tvClaimDescription = itemView.findViewById(R.id.tvClaimDescription);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        public void bind(Claim claim, OnClaimActionListener listener) {
            // Note: Item name would need to be fetched from the Item API or included in the Claim response
            tvItemName.setText("Item ID: " + claim.getItemId());
            tvClaimerName.setText("Diklaim oleh: " + claim.getClaimerName());
            tvClaimDescription.setText(claim.getDescription());

            btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(claim);
                }
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(claim);
                }
            });
        }
    }
}
