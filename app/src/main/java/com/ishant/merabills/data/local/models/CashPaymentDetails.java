package com.ishant.merabills.data.local.models;

import com.google.gson.Gson;
import com.ishant.merabills.application.AppConstant;

public class CashPaymentDetails extends PaymentDetail {

     public CashPaymentDetails(String totalAmount) {
         super(AppConstant.CASH, totalAmount);
     }

     @Override
    public String getDetails() {
        return new Gson().toJson(this);
    }
}