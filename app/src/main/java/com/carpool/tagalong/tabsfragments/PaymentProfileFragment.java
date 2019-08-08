package com.carpool.tagalong.tabsfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.CardHolderActivity;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.adapter.wepayadapters.CreditCardAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfile;
import com.carpool.tagalong.models.ModelUserProfileData;
import com.carpool.tagalong.models.wepay.CreditCards;
import com.carpool.tagalong.models.wepay.ModelCard;
import com.carpool.tagalong.models.wepay.ModelGetKycStatusResposne;
import com.carpool.tagalong.models.wepay.ModelGetWePayIframeRequest;
import com.carpool.tagalong.models.wepay.ModelIframeResponse;
import com.carpool.tagalong.models.wepay.ModelRegisterMerchantWePayRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.UIUtils;
import com.carpool.tagalong.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentProfileFragment extends Fragment implements View.OnClickListener, CreditCardAdapter.RemoveCardListner {

    private static final int ADD_CARD = 111;
    private com.carpool.tagalong.views.RegularTextView account_number, short_code, routing_number, bank_name;
    private EditText account_number_edt, short_code_edt, routing_number_edt, bank_name_edt;
    private com.carpool.tagalong.views.RegularTextView saveTxt, editTxt, addCard;
    private RecyclerView savedCards;
    private List<CreditCards> creditCardsList = new ArrayList<>();
    private CreditCardAdapter creditCardAdapter;
    private Button doKycBtn;
    private boolean flag = false;
    private String url = "https://www.tagalongride.com/wepay_kyc.html?update_uri=";

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

        savedCards = view.findViewById(R.id.cardList);
        addCard = view.findViewById(R.id.addCard);
        doKycBtn = view.findViewById(R.id.kyc);

        doKycBtn.setOnClickListener(this);
        addCard.setOnClickListener(this);

        saveTxt.setOnClickListener(this);
        editTxt.setOnClickListener(this);

        if (checkPhonePermission()) {

            if (DataManager.getModelUserProfileData().getWepayDetails().getWePayAccessToken().equals("")) {
                saveMerchantPaymentDetails();
            } else {
                isKycDone();
            }
        }

        return view;
    }

    private void isKycDone() {

        try {

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelGetWePayIframeRequest modelRegisterMerchantWePayRequest = new ModelGetWePayIframeRequest();
                modelRegisterMerchantWePayRequest.setAccount_id(Long.valueOf(DataManager.getModelUserProfileData().getWepayDetails().getWePayAccountId()));
                RestClientInterface restClientRetrofitService = new ApiClient().getIframeApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.isKycDone("Bearer " + DataManager.getModelUserProfileData().getWepayDetails().getWePayAccessToken(), modelRegisterMerchantWePayRequest).enqueue(new Callback<ModelGetKycStatusResposne>() {

                        @Override
                        public void onResponse(Call<ModelGetKycStatusResposne> call, Response<ModelGetKycStatusResposne> response) {

                            if (response != null) {

                                if (response.code() == 200) {

                                    if (response.body().getState().equals("verified")) {
                                        doKycBtn.setVisibility(View.GONE);
                                    }else{
                                        getIframeURl();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetKycStatusResposne> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            creditCardsList = data.getCard();

            updateUI();
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
        saveMerchantPaymentDetails();
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

    private void saveMerchantPaymentDetails() {

        try {

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelRegisterMerchantWePayRequest modelRegisterMerchantWePayRequest = new ModelRegisterMerchantWePayRequest();
                modelRegisterMerchantWePayRequest.setLast_name(DataManager.getModelUserProfileData().last_name);
                modelRegisterMerchantWePayRequest.setOriginal_device(Utils.getDeviceImeiNumber(getActivity()));
                modelRegisterMerchantWePayRequest.setOriginal_ip("182.75.120.10");

                Log.i("Payment DETAILS", "PROFILE REQUEST: " + modelRegisterMerchantWePayRequest.toString());

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.registerMerchantOnWePay(TagALongPreferenceManager.getToken(getActivity()), modelRegisterMerchantWePayRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                // get user profile again to save updated profile
                                ((HomeActivity) getActivity()).getUserProfile();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getIframeURl();
                                    }
                                }, 1500);

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

            case R.id.kyc:
                handleKYC();
                break;

            case R.id.edit_payment_dtls:
                editPayemntDetails();
                break;

            case R.id.addCard:
                addCreditCard();
                break;
        }
    }

    private void handleKYC() {

        loadUrl();
    }

    private void addCreditCard() {
        Intent intent = new Intent(getActivity(), CardHolderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivityForResult(intent, ADD_CARD);
    }

    @Override
    public void removeCard(CreditCards creditCards) {

        try {

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelCard modelCard = new ModelCard();
                modelCard.setWepay_card_id(creditCards.getWepay_card_id());

                Log.i("CARD DETAILS", "PROFILE REQUEST: " + modelCard.toString());

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.removeCard(TagALongPreferenceManager.getToken(getActivity()), modelCard).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                getUserProfile(getActivity());
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

    @Override
    public void onResume() {
        super.onResume();
        getUserProfile(getActivity());
    }

    private void updateUI() {

        creditCardAdapter = new CreditCardAdapter(creditCardsList, getActivity(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        savedCards.setLayoutManager(mLayoutManager);
        savedCards.setAdapter(creditCardAdapter);
    }

    @Override
    public void showCard(CreditCards creditCards) {

        Intent intent = new Intent(getActivity(), CardHolderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CARD, creditCards);
        getActivity().startActivity(intent);
    }

    @Override
    public void setDefault(CreditCards card) {

        try {

            creditCardAdapter.notifyDataSetChanged();

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelCard modelCard = new ModelCard();
                modelCard.setWepay_card_id(card.getWepay_card_id());

                Log.i("CARD DETAILS", "PROFILE REQUEST: " + modelCard.toString());

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.makeCardDefault(TagALongPreferenceManager.getToken(getActivity()), modelCard).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                UIUtils.alertBox(getActivity(), response.body().getMessage());
                                // get user profile again to save updated profile
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

    private void getUserProfile(final Context context) {

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.getUserProfile(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelUserProfile>() {

                    @Override
                    public void onResponse(Call<ModelUserProfile> call, Response<ModelUserProfile> response) {

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Log.i("PERSONAL DETAILS", "PROFILE RESPONSE: " + response.body().getData().toString());
                                DataManager.modelUserProfileData = response.body().getData();

                                creditCardsList = response.body().getData().getCard();
                                updateUI();

                            } else {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelUserProfile> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SIGN UP", "FAILURE verification");
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_CARD) {
                getUserProfile(getActivity());
//                updateUI();
            }
        }
    }

    private boolean checkPhonePermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 231);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 231) {
            if (checkPhonePermission()) {
                if (DataManager.getModelUserProfileData().getWepayDetails().getWePayAccessToken().equals(""))
                    saveMerchantPaymentDetails();
            } else {
                Toast.makeText(getActivity(), "Read phone state permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getIframeURl() {

        try {

            if (Utils.isNetworkAvailable(getActivity())) {

                ModelGetWePayIframeRequest modelRegisterMerchantWePayRequest = new ModelGetWePayIframeRequest();
                modelRegisterMerchantWePayRequest.setAccount_id(Long.valueOf(DataManager.getModelUserProfileData().getWepayDetails().getWePayAccountId()));
                modelRegisterMerchantWePayRequest.setMode(Constants.IFRAME_MODE);
                RestClientInterface restClientRetrofitService = new ApiClient().getIframeApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.getIframeUrl("Bearer " + DataManager.getModelUserProfileData().getWepayDetails().getWePayAccessToken(), modelRegisterMerchantWePayRequest).enqueue(new Callback<ModelIframeResponse>() {

                        @Override
                        public void onResponse(Call<ModelIframeResponse> call, Response<ModelIframeResponse> response) {

                            if (response != null) {
                                if (response.code() == 200) {
                                    url = url + response.body().getUri();
                                    doKycBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelIframeResponse> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("SAVE Payment DETAILS", "FAILURE SAVING PROFILE");
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUrl() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        startActivity(browserIntent);
    }
}