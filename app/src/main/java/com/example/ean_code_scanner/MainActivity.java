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
    ImageButton scanBtn;
    EditText eanInput, searchInp;

    private RecyclerView mRecyclerView;
    protected static List<Object> viewItems = new ArrayList<>();
    protected static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private JsonController jsonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonController = new JsonController();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerAdapter(this, viewItems);
        mRecyclerView.setAdapter(mAdapter);

        scanBtn = findViewById(R.id.scanBtn);
        eanInput = findViewById(R.id.eanInput);
        searchInp = findViewById(R.id.searchByNameInp);

        scanBtn.setOnClickListener(v -> {
            scanCode();
        });

        eanInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            {
                if (event == null || !event.isShiftPressed()) {
                    String eanCode = eanInput.getText().toString();
                    handleEAN(eanCode);

                    return true;
                }
            }
            return false;
        });
        searchInp.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            {
                if (event == null || !event.isShiftPressed()) {
                    jsonController.generateView(searchInp.getText().toString().toLowerCase());

                    return true;
                }
            }
            return false;
        });
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
        if(jsonController.checkEAN(eanCode)) {
            JSONObject jsonObj = jsonController.getEANdata(eanCode);
            try {
                String name = jsonObj.getString("name");
                String price = jsonObj.getString("price");
                String additionalComments = jsonObj.getString("additionalComments");

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
                    Boolean wantToCloseDialog = false;
                    if(nameInp.getText().length() > 0 && priceInp.getText().length() > 0) wantToCloseDialog = true;

                    if(wantToCloseDialog) {
                        jsonController.deleteEAN(eanCode);
                        jsonController.addEAN(eanCode, nameInp.getText().toString(), priceInp.getText().toString(), additionalCommentsInp.getText().toString());
                        dialog.dismiss();
                        Toast.makeText(this, "Edytowano produkt: " + nameInp.getText(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.cancel());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
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
                Boolean wantToCloseDialog = false;
                String name = nameInp.getText().toString();
                String eanCodeInp = eanInp.getText().toString();
                String price = priceInp.getText().toString();
                if(name.length() > 0 && price.length() > 0 && eanCodeInp.length() > 0) wantToCloseDialog = true;

                if(wantToCloseDialog) {
                    jsonController.addEAN(eanCode, nameInp.getText().toString(), priceInp.getText().toString(), additionalCommentsInp.getText().toString());
                    dialog.dismiss();
                    Toast.makeText(this, "Dodano nowy produkt: " + nameInp.getText(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Uzupełnij dane!", Toast.LENGTH_LONG).show();
                }
            });

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> dialog.cancel());
        }
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            String eanCode = result.getContents();
            handleEAN(eanCode);
        }
    });
}