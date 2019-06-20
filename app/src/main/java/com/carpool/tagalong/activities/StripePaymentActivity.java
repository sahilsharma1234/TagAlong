package com.carpool.tagalong.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelPaymentRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.SimpleAlertDialog;
import com.carpool.tagalong.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripePaymentActivity extends AppCompatActivity {

    private static final String TAG =  StripePaymentActivity.class.getSimpleName() ;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_total)
    TextView tv_total;
    @BindView(R.id.btn_order)
    TextView btn_order;
    @BindView(R.id.errorMsg)
    TextView errorMsg;
    @BindView(R.id.card_input_widget)
    CardInputWidget mCardInputWidget;
    @BindView(R.id.toolbar_stripe_payment)
    LinearLayout toolbarLayout;

    private ModelPaymentRequest modelPaymentRequest= null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        ButterKnife.bind(this);
        context = this;

        modelPaymentRequest = (ModelPaymentRequest) getIntent().getSerializableExtra(getString(R.string.order_key));

        if(modelPaymentRequest != null){

            tv_total.setText(modelPaymentRequest.getAmount()+"");
        }

        toolbarLayout = findViewById(R.id.toolbar_stripe_payment);
        TextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);

        title.setText("Payment");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
    }

    private void checkCardsDetail() {
        Card cardToSave = mCardInputWidget.getCard();
        if (cardToSave == null) {
            errorMsg.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Invalid Card Details", Toast.LENGTH_SHORT).show();
        } else {
            errorMsg.setVisibility(View.GONE);
            if (Utils.isNetworkAvailable(this)) {
                generateToken(cardToSave);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick({R.id.btn_order})
    public void OnClick(View view) {
        if (view.getId() == R.id.btn_order) {
            if (Utils.isNetworkAvailable(this)) {
                checkCardsDetail();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void generateToken(Card card) {
        Stripe stripe = new Stripe(StripePaymentActivity.this, Constants.STRIPE_KEY_DEBUG);
        ProgressDialogLoader.progressDialogCreation(StripePaymentActivity.this, getString(R.string.please_wait));
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.d(TAG, "---token" + token);
//                        jobOrderModel.setCardtoken(token.getId());
                        modelPaymentRequest.setStripeToken(token.getId());
                        postOrder();
                    }
                    public void onError(Exception error) {
                        handleError(error);
                    }
                }
        );
    }

    private void postOrder() {

        Log.d(TAG, "---Request is:" + modelPaymentRequest.toString());

        if (Utils.isNetworkAvailable(context)) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.payNow(TagALongPreferenceManager.getToken(StripePaymentActivity.this),modelPaymentRequest).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        ProgressDialogLoader.progressDialogDismiss();

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                handleResults(response.body());
                            }else{
                                Toast.makeText(context, "Some error occurred!! Please try again!!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                        ProgressDialogLoader.progressDialogDismiss();

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("Cancel Rider Own Ride", "FAILURE verification");
                    }
                });
            }
        } else {
            ProgressDialogLoader.progressDialogDismiss();
            Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void handleResults(ModelDocumentStatus userResponse) {
        ProgressDialogLoader.progressDialogDismiss();
        if (userResponse.getStatus() == 1) {

            SimpleAlertDialog.show(StripePaymentActivity.this,userResponse.getMessage() , new SimpleAlertDialog.SimpleDialogListener() {
                @Override
                public void onFinishDialog() {

                    finish();
                }
            });
        } else {
            SimpleAlertDialog.show(StripePaymentActivity.this, userResponse.getMessage());
        }
    }

    private void handleError(Throwable t) {
        ProgressDialogLoader.progressDialogDismiss();
        Log.e(TAG, "Post order Error" + t.toString());
        SimpleAlertDialog.show(StripePaymentActivity.this, "" + t.getMessage());
    }
}