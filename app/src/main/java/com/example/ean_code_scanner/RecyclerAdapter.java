package com.example.ean_code_scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private final Context context;
    private final List<Product> listRecyclerItem;
    private final JsonController jsonController;
    private final Runnable onDataChanged;

    public RecyclerAdapter(Context context, List<Product> listRecyclerItem, JsonController jsonController, Runnable onDataChanged) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
        this.jsonController = jsonController;
        this.onDataChanged = onDataChanged;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView price;
        private final ImageButton deleteBtn;
        private final View parentLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            parentLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Product product = listRecyclerItem.get(position);

                String eanCode = product.getEANcode();
                JSONObject jsonObj = jsonController.getEANdata(eanCode);
                if (jsonObj == null) {
                    return;
                }

                try {
                    String name = jsonObj.getString("name");
                    String price = jsonObj.getString("price");
                    String additionalComments = jsonObj.optString("additionalComments", "");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Informacje o produkcie");
                    View inputs = LayoutInflater.from(context).inflate(R.layout.add_product, null, false);
                    builder.setView(inputs);

                    EditText nameInp = inputs.findViewById(R.id.nameInp);
                    nameInp.setText(name);

                    EditText eanInp = inputs.findViewById(R.id.eanInp);
                    eanInp.setText(eanCode);
                    eanInp.setEnabled(false);

                    EditText priceInp = inputs.findViewById(R.id.priceInp);
                    priceInp.setText(price);

                    EditText additionalCommentsInp = inputs.findViewById(R.id.additionalCommentsInp);
                    additionalCommentsInp.setText(additionalComments);

                    builder.setPositiveButton("ZAPISZ", null);
                    builder.setNegativeButton("WYJDŹ", null);

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(c -> {
                        String newName = nameInp.getText().toString().trim();
                        String newPrice = priceInp.getText().toString().trim();
                        String newComments = additionalCommentsInp.getText().toString().trim();

                        if (!newName.isEmpty() && !newPrice.isEmpty()) {
                            jsonController.deleteEAN(eanCode);
                            jsonController.addEAN(eanCode, newName, newPrice, newComments);
                            onDataChanged.run();
                            dialog.dismiss();
                            Toast.makeText(context, "Edytowano produkt: " + newName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                        }
                    });

                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(c -> dialog.cancel());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        Product product = listRecyclerItem.get(position);

        itemViewHolder.name.setText(!product.getName().isEmpty() ? product.getName() : "Brak danych");
        itemViewHolder.price.setText(!product.getPrice().isEmpty() ? product.getPrice() + "zł" : "0zł");
        itemViewHolder.deleteBtn.setOnClickListener(v -> {
            jsonController.deleteEAN(product.getEANcode());
            onDataChanged.run();
            Toast.makeText(context, "Usunięto produkt o numerze EAN: " + product.getEANcode(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}
