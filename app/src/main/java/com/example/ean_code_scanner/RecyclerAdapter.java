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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<Object> listRecyclerItem;

    private JsonController jsonController;

    public RecyclerAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView price;
        private ImageButton deleteBtn;
        private View parentLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.deleteBtn);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            parentLayout.setOnClickListener(this);
            jsonController = new JsonController();
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Product product = (Product) listRecyclerItem.get(position);

                String eanCode = product.getEANcode();
                JSONObject jsonObj = jsonController.getEANdata(eanCode);

                try {
                    String name = jsonObj.getString("name");
                    String price = jsonObj.getString("price");
                    String additionalComments = jsonObj.getString("additionalComments");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Informacje o produkcie");
                    View inputs = LayoutInflater.from(context).inflate(R.layout.add_product, (ViewGroup) parentLayout.findViewById(android.R.id.content), false);
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
                        Boolean wantToCloseDialog = false;
                        if(nameInp.getText().length() > 0 && priceInp.getText().length() > 0) wantToCloseDialog = true;

                        if(wantToCloseDialog) {
                            jsonController.deleteEAN(eanCode);
                            jsonController.addEAN(eanCode, nameInp.getText().toString(), priceInp.getText().toString(), additionalCommentsInp.getText().toString());
                            dialog.dismiss();
                            Toast.makeText(context, "Edytowano produkt: " + nameInp.getText(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                        }
                    });

                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(c -> dialog.cancel());
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
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