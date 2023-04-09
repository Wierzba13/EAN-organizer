package com.example.ean_code_scanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button scanBtn;
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wyniki wyszukiwania");

                View display = LayoutInflater.from(this).inflate(R.layout.display_product, (ViewGroup) findViewById(android.R.id.content), false);
                builder.setView(display);

                TextView eanCode_tv = display.findViewById(R.id.eanCode_tv);
                TextView name_tv = display.findViewById(R.id.name_tv);
                TextView price_tv = display.findViewById(R.id.price_tv);

                eanCode_tv.setText("EAN: " + eanCode);
                name_tv.setText("Nazwa: " + name);
                price_tv.setText("Cena: " + price + "zł");

                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
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
            EditText priceInp = inputs.findViewById(R.id.priceInp);
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("ANULUJ", null);

            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Boolean wantToCloseDialog = false;
                String name = nameInp.getText().toString();
                String price = priceInp.getText().toString();
                if(name.length() > 0 && price.length() > 0) wantToCloseDialog = true;

                if(wantToCloseDialog) {
                    jsonController.addEAN(eanCode, nameInp.getText().toString(), priceInp.getText().toString());
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