package com.ishant.merabills.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ishant.merabills.data.local.deserialzation.PaymentDetailDeserializer;
import com.ishant.merabills.data.local.models.PaymentDetail;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    private static final String FILE_NAME = "LastPayment.txt";

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Save payment details to internal storage in JSON format.
    public boolean savePaymentDetails(Context context, Map<String, PaymentDetail> paymentDetails) {
        if (paymentDetails == null) return false;
        File externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (externalDir == null) {
            Log.e("HELPER", "External directory is null");
        }
        if (!externalDir.exists()) {
            boolean created = externalDir.mkdirs();
            if (!created) {
                Log.e("HELPER", "Failed to create external directory");
            }
        }
        File file = new File(externalDir, FILE_NAME);
        if (!file.exists()) {
            Log.i("HELPER", "File does not exist: " + file.getAbsolutePath());
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, PaymentDetail>>() {}.getType();
        String json = gson.toJson(paymentDetails,type);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(json.getBytes());
            fos.flush();
            Log.i("HELPER", "File  saved " + file.getAbsolutePath());
            return true ;
        } catch (IOException e) {
            e.printStackTrace();
            // You may want to show a message to the user
            Log.i("HELPER", "File not saved " + file.getAbsolutePath());
            return false ;
        }
    }

    // Load payment details from internal storage.
    public  Map<String, PaymentDetail> loadPaymentDetails(Context context) {
        File externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (externalDir == null) {
            Log.e("HELPER", "External directory is null");
            return null;
        }
        File file = new File(externalDir, FILE_NAME);
        if (!file.exists()) {
            Log.i("HELPER", "File does not exist: " + file.getAbsolutePath());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            String json = new String(buffer);
            // Create a Gson instance with our custom deserializer
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PaymentDetail.class, new PaymentDetailDeserializer())
                    .create();
            Type type = new TypeToken<Map<String, PaymentDetail>>() {}.getType();
            try {
                Map<String, PaymentDetail> paymentDetails = gson.fromJson(json, type);
                return paymentDetails;
            } catch (Exception e) {
                Log.e("HELPER", "Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
