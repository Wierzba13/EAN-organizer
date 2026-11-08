package com.example.ean_code_scanner;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonController {

    private static final String FILE_NAME = "codes.json";
    private final File jsonFile;

    public JsonController(Context context) {
        this.jsonFile = new File(context.getFilesDir(), FILE_NAME);
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray readJsonArray() {
        if (!jsonFile.exists() || jsonFile.length() == 0) {
            return new JSONArray();
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String content = builder.toString().trim();
            if (!content.isEmpty()) {
                return new JSONArray(content);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private void writeJsonArray(JSONArray jsonArray) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(jsonFile, false), StandardCharsets.UTF_8))) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Product> getProducts(String searchName) {
        List<Product> products = new ArrayList<>();
        JSONArray jsonArray = readJsonArray();
        String query = searchName != null ? searchName.trim().toLowerCase() : "";

        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(j);
                Product product = new Product(
                        jsonObj.optString("EANcode", ""),
                        jsonObj.optString("name", ""),
                        jsonObj.optString("price", ""),
                        jsonObj.optString("additionalComments", "")
                );

                if (query.isEmpty() || product.getName().toLowerCase().contains(query)) {
                    products.add(product);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    public JSONObject getEANdata(String eanCode) {
        JSONArray jsonArray = readJsonArray();
        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(j);
                if (eanCode.equals(jsonObj.optString("EANcode"))) {
                    return jsonObj;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean checkEAN(String eanCode) {
        return getEANdata(eanCode) != null;
    }

    public void addEAN(String eanCode, String name, String price, String additionalComments) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("EANcode", eanCode);
            jsonObject.put("name", name);
            jsonObject.put("price", price);
            jsonObject.put("additionalComments", additionalComments);

            JSONArray jsonArray = readJsonArray();
            jsonArray.put(jsonObject);
            writeJsonArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteEAN(String eanCode) {
        JSONArray jsonArray = readJsonArray();
        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(j);
                if (eanCode.equals(jsonObj.optString("EANcode"))) {
                    jsonArray.remove(j);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        writeJsonArray(jsonArray);
    }
}
