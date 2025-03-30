package com.ishant.merabills.data.local.models;

import com.google.gson.Gson;
import com.ishant.merabills.application.AppConstant;

public class BankPaymentDetails extends PaymentDetail {
    private String bankName;
    private String transactionId;

    public BankPaymentDetails(String totalAmount,String bankName, String transactionId) {
        super(AppConstant.BANK, totalAmount);
        this.bankName = bankName;
        this.transactionId = transactionId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String getDetails() {
        return new Gson().toJson(this);
    }
}