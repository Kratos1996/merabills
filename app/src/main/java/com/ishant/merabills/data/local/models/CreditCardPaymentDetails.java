package com.ishant.merabills.data.local.models;

import com.google.gson.Gson;
import com.ishant.merabills.application.AppConstant;

public class CreditCardPaymentDetails extends PaymentDetail {
    private String creditCardNumber;
    private String expiryDate;
    private String transactionId;

    public CreditCardPaymentDetails(String totalAmount,String creditCardNumber, String expiryDate, String transactionId) {
        super(AppConstant.CREDIT_CARD, totalAmount);
        this.creditCardNumber = creditCardNumber;
        this.expiryDate = expiryDate;
        this.transactionId = transactionId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String getDetails() {
        return new Gson().toJson(this);
    }
}
