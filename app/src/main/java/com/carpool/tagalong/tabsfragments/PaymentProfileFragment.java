package com.carpool.tagalong.tabsfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfileData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentProfileFragment extends Fragment implements View.OnClickListener {

    private com.carpool.tagalong.views.RegularTextView account_number, short_code, routing_number, bank_name;
    private EditText account_number_edt, short_code_edt, routing_number_edt, bank_name_edt;
    private com.carpool.tagalong.views.RegularTextView saveTxt, editTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.payment_profile_fragment, container, false);

        account_number = view.findViewById(R.id.account_number);
        short_code = view.findViewById(R.id.short_code);
        routing_number = view.findViewById(R.id.routing_number);
        bank_name = view.findViewById(R.id.bank_name);

        account_number_edt = view.findViewById(R.id.account_number_edt);
        short_code_edt = view.findViewById(R.id.short_code_edt);
        routing_number_edt = view.findViewById(R.id.routing_no_edt);
        bank_name_edt = view.findViewById(R.id.bank_name_edt);

        saveTxt = view.findViewById(R.id.save_payment_dtls);
        editTxt = view.findViewById(R.id.edit_payment_dtls);

        saveTxt.setOnClickListener(this);
        editTxt.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ModelUserProfileData data = DataManager.modelUserProfileData;

        if (data != null) {

            account_number.setText(data.getPaymentDetails().getAccountNumber());
            short_code.setText(data.getPaymentDetails().getShortCode());
            routing_number.setText(data.getPaymentDetails().getRoutingNumber());
            bank_name.setText(data.getPaymentDetails().getBankName());
        }
    }

    private void handleSavePaymentDetails() {

        editTxt.setVisibility(View.VISIBLE);
        saveTxt.setVisibility(View.GONE);

        account_number.setText(account_number_edt.getText().toString());
        short_code.setText(short_code_edt.getText().toString());
        routing_number.setText(routing_number_edt.getText().toString());
        bank_name.setText(bank_name_edt.getText().toString());

        account_number_edt.setVisibility(View.GONE);
        short_code_edt.setVisibility(View.GONE);
        routing_number_edt.setVisibility(View.GONE);
        bank_name_edt.setVisibility(View.GONE);

        account_number.setVisibility(View.VISIBLE);
        short_code.setVisibility(View.VISIBLE);
        routing_number.setVisibility(View.VISIBLE);
        bank_name.setVisibility(View.VISIBLE);

        savePaymentDetails();
    }

    private void savePaymentDetails() {

        try {

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelUpdateProfileRequest modelUpdateProfileRequest = new ModelUpdateProfileRequest();
                modelUpdateProfileRequest.setAccountNumber(account_number.getText().toString());
                modelUpdateProfileRequest.setShortCode(short_code.getText().toString());
                modelUpdateProfileRequest.setRoutingNumber(routing_number.getText().toString());
                modelUpdateProfileRequest.setBankName(bank_name.getText().toString());

                Log.i("Payment DETAILS", "PROFILE REQUEST: " + modelUpdateProfileRequest.toString());

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.updateProfile(TagALongPreferenceManager.getToken(getActivity()), modelUpdateProfileRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                // get user profile again to save updated profile
                                ((HomeActivity) getActivity()).getUserProfile();
                            } else {
                                Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                ProgressDialogLoader.progressDialogDismiss();
                Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editPayemntDetails() {

        saveTxt.setVisibility(View.VISIBLE);
        editTxt.setVisibility(View.GONE);

        account_number_edt.setText(account_number.getText().toString());
        short_code_edt.setText(short_code.getText().toString());
        routing_number_edt.setText(routing_number.getText().toString());
        bank_name_edt.setText(bank_name.getText().toString());

        account_number_edt.setVisibility(View.VISIBLE);
        short_code_edt.setVisibility(View.VISIBLE);
        routing_number_edt.setVisibility(View.VISIBLE);
        bank_name_edt.setVisibility(View.VISIBLE);

        account_number.setVisibility(View.GONE);
        short_code.setVisibility(View.GONE);
        routing_number.setVisibility(View.GONE);
        bank_name.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.save_payment_dtls:
                handleSavePaymentDetails();
                break;

            case R.id.edit_payment_dtls:
                editPayemntDetails();
                break;
        }
    }
}