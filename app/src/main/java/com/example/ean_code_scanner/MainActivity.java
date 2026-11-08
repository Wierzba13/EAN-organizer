package com.example.ean_code_scanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton scanBtn;
    private EditText eanInput, searchInp;

    private RecyclerView mRecyclerView;
    private final List<Product> viewItems = new ArrayList<>();
    private RecyclerAdapter mAdapter;

    private JsonController jsonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonController = new JsonController(this);

        scanBtn = findViewById(R.id.scanBtn);
        eanInput = findViewById(R.id.eanInput);
        searchInp = findViewById(R.id.searchByNameInp);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter(this, viewItems, jsonController, this::refreshList);
        mRecyclerView.setAdapter(mAdapter);

        refreshList();

        scanBtn.setOnClickListener(v -> scanCode());

        eanInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed()) {
                    String eanCode = eanInput.getText().toString().trim();
                    handleEAN(eanCode);
                    return true;
                }
            }
            return false;
        });

        searchInp.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed()) {
                    refreshList();
                    return true;
                }
            }
            return false;
        });
    }

    private void refreshList() {
        String query = searchInp != null ? searchInp.getText().toString().toLowerCase() : "";
        viewItems.clear();
        viewItems.addAll(jsonController.getProducts(query));
        mAdapter.notifyDataSetChanged();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void handleEAN(String eanCode) {
        if (jsonController.checkEAN(eanCode)) {
            JSONObject jsonObj = jsonController.getEANdata(eanCode);
            try {
                String name = jsonObj.getString("name");
                String price = jsonObj.getString("price");
                String additionalComments = jsonObj.optString("additionalComments", "");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Informacje o produkcie");
                View inputs = LayoutInflater.from(this).inflate(R.layout.add_product, (ViewGroup) findViewById(android.R.id.content), false);
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String newName = nameInp.getText().toString().trim();
                    String newPrice = priceInp.getText().toString().trim();
                    String newComments = additionalCommentsInp.getText().toString().trim();

                    if (!newName.isEmpty() && !newPrice.isEmpty()) {
                        jsonController.deleteEAN(eanCode);
                        jsonController.addEAN(eanCode, newName, newPrice, newComments);
                        refreshList();
                        dialog.dismiss();
                        Toast.makeText(this, "Edytowano produkt: " + newName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.cancel());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Dodaj produkt");
            View inputs = LayoutInflater.from(this).inflate(R.layout.add_product, (ViewGroup) findViewById(android.R.id.content), false);
            builder.setView(inputs);

            EditText nameInp = inputs.findViewById(R.id.nameInp);
            EditText eanInp = inputs.findViewById(R.id.eanInp);
            eanInp.setText(eanCode);
            EditText priceInp = inputs.findViewById(R.id.priceInp);
            EditText additionalCommentsInp = inputs.findViewById(R.id.additionalCommentsInp);

            builder.setPositiveButton("DODAJ", null);
            builder.setNegativeButton("ANULUJ", null);

            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = nameInp.getText().toString().trim();
                String eanCodeInp = eanInp.getText().toString().trim();
                String price = priceInp.getText().toString().trim();
                String comments = additionalCommentsInp.getText().toString().trim();

                if (!name.isEmpty() && !price.isEmpty() && !eanCodeInp.isEmpty()) {
                    if (jsonController.checkEAN(eanCodeInp)) {
                        Toast.makeText(this, "Produkt o tym kodzie EAN już istnieje!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    jsonController.addEAN(eanCodeInp, name, price, comments);
                    refreshList();
                    dialog.dismiss();
                    Toast.makeText(this, "Dodano nowy produkt: " + name, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                }
            });

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.cancel());
        }
    }

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String eanCode = result.getContents();
            handleEAN(eanCode);
        }
    });
}
