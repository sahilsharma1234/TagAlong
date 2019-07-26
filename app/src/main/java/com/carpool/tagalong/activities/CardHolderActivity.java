package com.carpool.tagalong.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.wepay.CreditCards;
import com.carpool.tagalong.models.wepay.ModelAddCardRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardHolderActivity extends AppCompatActivity {

    private EditText step1;
    private EditText step2;
    private EditText step3;
    private EditText step4;
    private EditText month;
    private EditText year;
    private EditText cvv;
    private EditText cardNumber;
    private Button addCard;
    private CreditCards card;
    private  String crd1,crd2,crd3,crd4, mnth,yearString,name,cvvString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.card_holder);

        step1   =   findViewById(R.id.step1);
        step2   =   findViewById(R.id.step2);
        step3   =   findViewById(R.id.step3);
        step4   =   findViewById(R.id.step4);
        month   =   findViewById(R.id.month);
        year    =   findViewById(R.id.year);
        addCard =   findViewById(R.id.add_card);
        cvv     =   findViewById(R.id.cvv);
        cardNumber = findViewById(R.id.card_number);

        if(getIntent().getExtras()!=null){

            if(getIntent().getExtras().containsKey(Constants.CARD)){
                card = (CreditCards) getIntent().getSerializableExtra(Constants.CARD);

                if(card != null){

                    step1.setVisibility(View.GONE);
                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                    step4.setVisibility(View.GONE);

                    cardNumber.setText(card.getNumber());
                    month.setText(card.getExp_month());
                    year.setText(card.getExp_year());
                    addCard.setVisibility(View.GONE);

                }
            }
        }

        step1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() >= 4) {
                    step2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        step2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() >= 4) {
                    step3.requestFocus();
                } else if (charSequence.toString().trim().length() == 0) {
                    step1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        step3.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() >= 4) {
                    step4.requestFocus();
                } else if (charSequence.toString().trim().length() == 0) {
                    step2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        step4.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    step3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        month.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() >= 2) {
                    year.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                editable.toString().trim();
            }
        });

        year.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    month.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prepareCard();
            }
        });
    }

    private boolean validateSignUpData() {

        crd1        = step1.getText().toString();
        crd2        = step2.getText().toString();
        crd3        = step3.getText().toString();
        crd4        = step4.getText().toString();
        mnth        = month.getText().toString();
        yearString  = year.getText().toString();
        cvvString   = cvv.getText().toString().trim();

        if(crd1.equals("")){
            return false;
        }
        if(crd2.equals("")){
            return false;
        }
        if(crd3.equals("")){
            return false;
        }
        if(crd4.equals("")){
            return false;
        }
        if(mnth.equals("")){
            return false;
        }
        if(yearString.equals("")) {
            return false;
        }
        if(cvvString.equals("")){
            return false;
        }
        return true;
    }

    private void prepareCard() {

        if(validateSignUpData()){

            ModelAddCardRequest modelAddCardRequest = new ModelAddCardRequest();
            modelAddCardRequest.setNumber(crd1+""+crd2+""+crd3+""+crd4);
            modelAddCardRequest.setCvv(cvvString);
            modelAddCardRequest.setExp_month(Integer.valueOf(mnth));
            modelAddCardRequest.setExp_year(Integer.valueOf(yearString));

            addCard(modelAddCardRequest);
        }
    }

    private void addCard(ModelAddCardRequest modelAddCardRequest){

        try {
            if (Utils.isNetworkAvailable(this)) {

                Log.i("CARD DETAILS", "PROFILE REQUEST: " + modelAddCardRequest.toString());

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(this, getString(R.string.please_wait));

                    restClientRetrofitService.addCard(TagALongPreferenceManager.getToken(this), modelAddCardRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                Toast.makeText(CardHolderActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                Utils.getUserProfile(CardHolderActivity.this);
                                finish();

                            } else {
                                Toast.makeText(CardHolderActivity.this, response.message(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(CardHolderActivity.this, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}