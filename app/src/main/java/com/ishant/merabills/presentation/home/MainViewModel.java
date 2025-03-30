package com.ishant.merabills.presentation.home;

import android.Manifest;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.ishant.merabills.application.AppConstant;
import com.ishant.merabills.data.local.models.PaymentDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    ArrayList<String> supportedPaymentMethods = new ArrayList<>(Arrays.asList(AppConstant.CASH, AppConstant.BANK, AppConstant.CREDIT_CARD));

    private String selectedPaymentType = "";
    final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Map<String, PaymentDetail> paymentMap = new LinkedHashMap<>();
    MutableLiveData<Map<String, PaymentDetail>> paymentDetailsLiveData = new MutableLiveData<>();

    public void setSelectedPaymentType(String paymentType) {
        this.selectedPaymentType = paymentType;
    }

    public String getSelectedPaymentType() {
        return selectedPaymentType;
    }

    public void savePaymentDetails(PaymentDetail paymentDetail) {
        addPayment(paymentDetail);
        paymentDetailsLiveData.postValue(paymentMap);
    }

    public void addPayment(PaymentDetail payment) {
        paymentMap.put(payment.getPaymentType(), payment); // Replaces if exists
        supportedPaymentMethods.remove(payment.getPaymentType());
        Log.d("paymentMap after add", new Gson().toJson(paymentMap));
    }
    public void remove(PaymentDetail payment) {
        supportedPaymentMethods.add(payment.getPaymentType());
        paymentMap.remove(payment.getPaymentType(), payment); // remove if exists
        paymentDetailsLiveData.postValue(paymentMap);
        Log.d("paymentMap after remove", new Gson().toJson(paymentMap));
    }

}
