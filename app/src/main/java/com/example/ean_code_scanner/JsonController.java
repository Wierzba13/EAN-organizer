package com.example.ean_code_scanner;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class JsonController extends MainActivity {

    private static String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/codes.json";
    private static final File jsonFile = new File(FILE_PATH);

    public JsonController() {
        super();
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        this.generateView("");
    }

    public void generateView(String searchName) {
        MainActivity.viewItems.clear();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }

            if(buffer.toString() != null) {
                JSONArray jsonArray = new JSONArray(buffer.toString());
                int max = jsonArray.length();

                for (int j = 0; j < max; j++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(j);

                    Product product = new Product(jsonObj.getString("EANcode"), jsonObj.getString("name"), jsonObj.getString("price"));

                    if(searchName.length() > 0) {
                        if(product.getName().toLowerCase().contains(searchName)) {
                            MainActivity.viewItems.add(product);
                        }
                    }
                    else {
                        MainActivity.viewItems.add(product);
                    }
                }
                MainActivity.mAdapter.notifyDataSetChanged(); // to refresh changes
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public JSONObject getEANdata(String eanCode) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }

            if(buffer.toString() != null) {
                JSONArray jsonArray = new JSONArray(buffer.toString());
                int max = jsonArray.length();

                for (int j = 0; j < max; j++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(j);

                    if(eanCode.equals(jsonObj.getString("EANcode"))) {
                        return jsonObj;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public boolean checkEAN(String eanCode) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }

            if(buffer.toString() != null) {
                JSONArray jsonArray = new JSONArray(buffer.toString());
                int max = jsonArray.length();

                for (int j = 0; j < max; j++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(j);
                    String eanCodeFromJSON = jsonObj.getString("EANcode");

                    if(eanCode.equals(eanCodeFromJSON)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void addEAN(String eanCode, String name, String price) {

        FileInputStream fileInputStream = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("EANcode", eanCode);
            jsonObject.put("name", name);
            jsonObject.put("price", price);

            fileInputStream = new FileInputStream(jsonFile);

            if (fileInputStream.available() > 0) {
                int i = -1;
                StringBuffer buffer = new StringBuffer();
                while ((i = fileInputStream.read()) != -1) {
                    buffer.append((char) i);
                }

                JSONArray jsonArray = new JSONArray(buffer.toString());
                jsonArray.put(jsonObject);
                fileInputStream.close();

                Writer output;
                File file = new File(FILE_PATH);
                output = new BufferedWriter(new FileWriter(file));
                output.write(jsonArray.toString());
                output.close();
                this.generateView("");
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                fileInputStream.close();

                Writer output;
                File file = new File(FILE_PATH);
                output = new BufferedWriter(new FileWriter(file));
                output.write(jsonArray.toString());
                output.close();
                this.generateView("");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteEAN(String eanCode) {
        FileInputStream fileInputStream = null;
        Writer output = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }

            if(buffer.toString() != null) {
                JSONArray jsonArray = new JSONArray(buffer.toString());
                int max = jsonArray.length();

                for (int j = 0; j < max; j++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(j);
                    String eanCodeFromJSON = jsonObj.getString("EANcode");

                    if(eanCode.equals(eanCodeFromJSON)) {
                        jsonArray.remove(j);
                        fileInputStream.close();
                        break;
                    }
                }
                File file = new File(FILE_PATH);
                output = new BufferedWriter(new FileWriter(file));
                output.write(jsonArray.toString());
                output.close();
                this.generateView("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
