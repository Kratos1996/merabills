package com.ishant.merabills.data.local.deserialzation;
import com.google.gson.*;
import com.ishant.merabills.application.AppConstant;
import com.ishant.merabills.data.local.models.BankPaymentDetails;
import com.ishant.merabills.data.local.models.CashPaymentDetails;
import com.ishant.merabills.data.local.models.CreditCardPaymentDetails;
import com.ishant.merabills.data.local.models.PaymentDetail;

import java.lang.reflect.Type;

public class PaymentDetailDeserializer implements JsonDeserializer<PaymentDetail> {

    @Override
    public PaymentDetail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        // Get the paymentType and totalAmount fields (both as strings)
        String paymentType = jsonObj.get("paymentType").getAsString();
        String totalAmount = jsonObj.get("totalAmount").getAsString();

        // Based on paymentType, instantiate the proper subclass.
        switch (paymentType) {
            case AppConstant.CASH:
                return new CashPaymentDetails(totalAmount);

            case AppConstant.BANK:
                String bankName = jsonObj.has("bankName") ? jsonObj.get("bankName").getAsString() : null;
                String transactionIdBank = jsonObj.has("transactionId") ? jsonObj.get("transactionId").getAsString() : null;
                return new BankPaymentDetails(totalAmount, bankName, transactionIdBank);

            case AppConstant.CREDIT_CARD:
                String creditCardNumber = jsonObj.has("creditCardNumber") ? jsonObj.get("creditCardNumber").getAsString() : null;
                String expiryDate = jsonObj.has("expiryDate") ? jsonObj.get("expiryDate").getAsString() : null;
                String transactionIdCC = jsonObj.has("transactionId") ? jsonObj.get("transactionId").getAsString() : null;
                return new CreditCardPaymentDetails(totalAmount, creditCardNumber, expiryDate, transactionIdCC);

            default:
                throw new JsonParseException("Unknown payment type: " + paymentType);
        }
    }
}