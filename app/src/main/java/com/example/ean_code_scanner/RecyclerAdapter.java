package com.example.ean_code_scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<Object> listRecyclerItem;

    private JsonController jsonController;

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView price;
        private Button deleteBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
            jsonController = new JsonController();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE:

            default:
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item, parent, false);

                return new ItemViewHolder((layoutView));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case TYPE:
            default:

                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Product product = (Product) listRecyclerItem.get(position);

                itemViewHolder.name.setText(product.getName().length() > 0 ? product.getName() : "Brak danych");
                itemViewHolder.price.setText(product.getPrice().length() > 0 ? product.getPrice() + "zł" : "0zł");
                itemViewHolder.deleteBtn.setOnClickListener(v -> {
                    jsonController.deleteEAN(product.getEANcode());
                    Toast.makeText(context, "Usunięto produkt o numerze EAN: " + product.getEANcode(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}
