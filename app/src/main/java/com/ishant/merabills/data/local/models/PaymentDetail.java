package com.ishant.merabills.data.local.models;


public abstract class PaymentDetail {
    protected String paymentType;
    private final String totalAmount;

    public PaymentDetail(String paymentType , String totalAmount) {
        this.paymentType = paymentType;
        this.totalAmount = totalAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }
    public String getTotalAmount() {
        return totalAmount;
    }

    public abstract String getDetails(); // Enforce details method for each subclass
}
