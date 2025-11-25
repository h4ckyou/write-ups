package com.example.picobank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList2) {
        this.transactionList = transactionList2;
    }

    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false));
    }

    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = this.transactionList.get(position);
        holder.transactionName.setText(transaction.getName());
        holder.transactionDate.setText(transaction.getDate());
        holder.transactionAmount.setText(transaction.getAmount());
        if (transaction.isIncoming()) {
            holder.transactionAmount.setTextColor(holder.itemView.getContext().getResources().getColor(17170453));
            holder.transactionIcon.setImageResource(R.drawable.down_arrow);
            return;
        }
        holder.transactionAmount.setTextColor(holder.itemView.getContext().getResources().getColor(17170455));
        holder.transactionIcon.setImageResource(R.drawable.up_arrow);
    }

    public int getItemCount() {
        return this.transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionAmount;
        TextView transactionDate;
        ImageView transactionIcon;
        TextView transactionName;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            this.transactionName = (TextView) itemView.findViewById(R.id.transactionName);
            this.transactionDate = (TextView) itemView.findViewById(R.id.transactionDate);
            this.transactionAmount = (TextView) itemView.findViewById(R.id.transactionAmount);
            this.transactionIcon = (ImageView) itemView.findViewById(R.id.transactionIcon);
        }
    }
}
