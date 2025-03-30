package com.ishant.merabills.presentation.home;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ComponentCaller;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import com.ishant.merabills.R;
import com.ishant.merabills.application.AppConstant;
import com.ishant.merabills.data.local.models.BankPaymentDetails;
import com.ishant.merabills.data.local.models.CashPaymentDetails;
import com.ishant.merabills.data.local.models.CreditCardPaymentDetails;
import com.ishant.merabills.data.local.models.PaymentDetail;
import com.ishant.merabills.databinding.ActivityMainBinding;
import com.ishant.merabills.databinding.AddPaymentDialogBinding;
import com.ishant.merabills.utils.Helper;
import com.ishant.merabills.utils.PermissionUtils;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private AddPaymentDialogBinding dialogBinding;
    private MainViewModel viewModel;
    private Dialog paymentDetailsDialog;
    private Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        helper = new Helper();
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(activity, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.add_payment_dialog, null, false);
        loadPaymentDetailsFromFile(binding);
        maintainClickListeners(binding);
    }

    private void showCustomDialog() {
        if (paymentDetailsDialog == null) {
            paymentDetailsDialog = new Dialog(activity);
            paymentDetailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            paymentDetailsDialog.setContentView(dialogBinding.getRoot());
            final Window window = paymentDetailsDialog.getWindow();
            assert window != null;
            window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            paymentDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
            window.setAttributes(wlp);
            paymentDetailsDialog.setCancelable(true);
        } else {
            relaunchDialogFromStart();
        }
        dialogBinding.closeBtn.setOnClickListener(view -> {
            paymentDetailsDialog.dismiss();
        });
        dialogBinding.amountEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                helper.hideKeyboard(activity, dialogBinding.amountEditText);
                return true;
            }
            return false;
        });
        dialogBinding.paymentTypeEdit.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, viewModel.supportedPaymentMethods));
        // Handle item selection
        dialogBinding.paymentTypeEdit.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = viewModel.supportedPaymentMethods.get(position);
            viewModel.setSelectedPaymentType(selectedItem);
            switch (selectedItem) {
                case AppConstant.BANK:
                    dialogBinding.bankDetailsLayout.setVisibility(View.VISIBLE);
                    dialogBinding.creditCardDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.addFundBtn.setVisibility(View.VISIBLE);
                    dialogBinding.transactionIdLay.setVisibility(View.VISIBLE);
                    break;
                case AppConstant.CREDIT_CARD:
                    dialogBinding.bankDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.creditCardDetailsLayout.setVisibility(View.VISIBLE);
                    dialogBinding.addFundBtn.setVisibility(View.VISIBLE);
                    dialogBinding.transactionIdLay.setVisibility(View.VISIBLE);
                    break;
                case AppConstant.CASH:
                    dialogBinding.bankDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.creditCardDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.transactionIdLay.setVisibility(View.GONE);
                    dialogBinding.addFundBtn.setVisibility(View.VISIBLE);
                    break;
                default:
                    dialogBinding.bankDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.creditCardDetailsLayout.setVisibility(View.GONE);
                    dialogBinding.transactionIdLay.setVisibility(View.GONE);
                    dialogBinding.addFundBtn.setVisibility(View.GONE);
                    break;
            }
        });

        dialogBinding.addFundBtn.setOnClickListener(view -> {
            switch (viewModel.getSelectedPaymentType()) {
                case AppConstant.BANK:
                    if (dialogBinding.bankNameEditText.getText().toString().isEmpty()) {
                        dialogBinding.bankNameEditText.setError(getString(R.string.please_enter_bank_name));
                    } else if (dialogBinding.transactionIdEditText.getText().toString().isEmpty()) {
                        dialogBinding.bankNameEditText.setError(null);
                        dialogBinding.transactionIdEditText.setError(getString(R.string.please_enter_transaction_id));
                    } else {
                        dialogBinding.bankNameEditText.setError(null);
                        dialogBinding.transactionIdEditText.setError(null);
                        viewModel.savePaymentDetails(new BankPaymentDetails(
                                dialogBinding.amountEditText.getText().toString(),
                                dialogBinding.bankNameEditText.getText().toString(),
                                dialogBinding.transactionIdEditText.getText().toString()
                        ));
                        showToast(getString(R.string.payment_details_saved_successfully));
                        viewModel.setSelectedPaymentType("");
                        paymentDetailsDialog.dismiss();
                    }
                    break;
                case AppConstant.CREDIT_CARD:
                    if (dialogBinding.creditCardNumberEditText.getText().toString().isEmpty()) {
                        dialogBinding.creditCardNumberEditText.setError(getString(R.string.please_enter_credit_card_number));
                    } else if (dialogBinding.expiryDateEditText.getText().toString().isEmpty()) {
                        dialogBinding.creditCardNumberEditText.setError(null);
                        dialogBinding.expiryDateEditText.setError(getString(R.string.please_enter_expiry_date));
                    } else if (dialogBinding.transactionIdEditText.getText().toString().isEmpty()) {
                        dialogBinding.expiryDateEditText.setError(null);
                        dialogBinding.creditCardNumberEditText.setError(null);
                        dialogBinding.transactionIdEditText.setError(getString(R.string.please_enter_transaction_id));
                    } else {
                        dialogBinding.creditCardNumberEditText.setError(null);
                        dialogBinding.expiryDateEditText.setError(null);
                        dialogBinding.transactionIdEditText.setError(null);
                        viewModel.savePaymentDetails(new CreditCardPaymentDetails(
                                dialogBinding.amountEditText.getText().toString(),
                                dialogBinding.creditCardNumberEditText.getText().toString(),
                                dialogBinding.expiryDateEditText.getText().toString(),
                                dialogBinding.transactionIdEditText.getText().toString()
                        ));
                        showToast(getString(R.string.payment_details_saved_successfully));
                        viewModel.setSelectedPaymentType("");
                        paymentDetailsDialog.dismiss();
                    }
                    break;
                case AppConstant.CASH:
                    viewModel.savePaymentDetails(new CashPaymentDetails(dialogBinding.amountEditText.getText().toString()));
                    showToast(getString(R.string.payment_details_saved_successfully));
                    paymentDetailsDialog.dismiss();
                    viewModel.setSelectedPaymentType("");
                    break;
                default:
                    showToast(getString(R.string.please_select_payment_type));
                    break;
            }
        });

        paymentDetailsDialog.show();
    }

    private void relaunchDialogFromStart() {
        dialogBinding.amountEditText.setText("");
        dialogBinding.bankNameEditText.setText("");
        dialogBinding.transactionIdEditText.setText("");
        dialogBinding.creditCardNumberEditText.setText("");
        dialogBinding.expiryDateEditText.setText("");
        dialogBinding.paymentTypeEdit.setText("");
        dialogBinding.bankDetailsLayout.setVisibility(View.GONE);
        dialogBinding.creditCardDetailsLayout.setVisibility(View.GONE);
        dialogBinding.transactionIdLay.setVisibility(View.GONE);
        dialogBinding.addFundBtn.setVisibility(View.GONE);
    }
    private void maintainClickListeners(ActivityMainBinding binding) {
        binding.addPaymentBtn.setOnClickListener(view -> showCustomDialog());
        binding.saveButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                PermissionUtils.requestPermissions(activity, viewModel.permissions, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        // Proceed with your work that requires these permissions
                        if (!viewModel.paymentMap.isEmpty()) {
                            if(helper.savePaymentDetails(activity, viewModel.paymentMap)){
                                showToast("Details Saved on external storage successfully");
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        showToast("Permissions denied: " + String.join(", ", deniedPermissions));
                    }
                });
            }else{
                if (!viewModel.paymentMap.isEmpty()) {
                    if(helper.savePaymentDetails(activity, viewModel.paymentMap)){
                        showToast("Details Saved on external storage successfully");
                    }
                }
            }
        });
    }

    private void loadPaymentDetailsFromFile(ActivityMainBinding binding) {
        viewModel.paymentMap = helper.loadPaymentDetails(this);
        if (viewModel.paymentMap != null) {
            viewModel.paymentDetailsLiveData.setValue(viewModel.paymentMap);
        } else {
            // Initialize with empty data if needed
            viewModel.paymentMap = new LinkedHashMap<>();
            viewModel.paymentDetailsLiveData.setValue( viewModel.paymentMap);
        }
        // Observe the LiveData for ui updates
        viewModel.paymentDetailsLiveData.observe(this, paymentData -> {
            Double finalAmount = 0.0;
            binding.paymentGroup.removeAllViews();
            if(paymentData.values().isEmpty()){
                binding.paymentGroup.setVisibility(View.GONE);
            }else{
                binding.paymentGroup.setVisibility(View.VISIBLE);
            }
            if(paymentData.values().size()==3){
                binding.addPaymentBtn.setVisibility(View.GONE);
            }else{
                binding.addPaymentBtn.setVisibility(View.VISIBLE);
            }
            for (PaymentDetail paymentDetail : paymentData.values()) {
                finalAmount = finalAmount + Double.valueOf(paymentDetail.getTotalAmount());
                addChip(paymentDetail, binding, viewModel);
            }
            binding.finalAmountTextView.setText(new BigDecimal(finalAmount).toPlainString());
        });
    }
    @SuppressLint("DefaultLocale")
    private void addChip(PaymentDetail paymentDetail, ActivityMainBinding binding, MainViewModel viewModel) {

        String formattedText = String.format("%s = ₹ %s", paymentDetail.getPaymentType(), paymentDetail.getTotalAmount());
        Chip chip = new Chip(this);
        chip.setText(formattedText);
        chip.setCloseIconResource(R.drawable.ic_close_icon);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.colorOnSurface);
        chip.setTextColor(activity.getResources().getColor(R.color.text_primary, null));
        chip.setChipStrokeWidth(1);
        chip.setChipStrokeColorResource(R.color.colorOutline);
        // Set chip close action
        chip.setOnCloseIconClickListener(v -> {
                    binding.paymentGroup.removeView(chip);
                    viewModel.remove(paymentDetail);
                }
        );
        binding.paymentGroup.addView(chip);

    }

    private void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            PermissionUtils.onRequestPermissionsResult(requestCode, viewModel.permissions, new int[]{});
        }
    }
}