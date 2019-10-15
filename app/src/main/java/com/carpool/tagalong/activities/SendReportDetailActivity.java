package com.carpool.tagalong.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.carpool.tagalong.R;
import com.carpool.tagalong.utils.Utils;
import com.carpool.tagalong.views.RegularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendReportDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.img_back)
    ImageView back_image;

    @BindView(R.id.img_close)
    ImageView closeImg;

    @BindView(R.id.img_parent)
    ImageView parentImageView;

    @BindView(R.id.parent_img_text)
    RegularTextView parentImageText;

    @BindView(R.id.firstIcon)
    ImageView firstThumbImage;

    @BindView(R.id.secondthumbnail)
    ImageView secondThumbImage;

    @BindView(R.id.thumbnail)
    ImageView thirdThumbImage;

    @BindView(R.id.firstItemName)
    RegularTextView firstthumbtext;

    @BindView(R.id.second_item_name)
    RegularTextView seconthumbText;

    @BindView(R.id.item_name)
    RegularTextView thirdThumbText;

    @BindView(R.id.reportComments)
    AppCompatEditText comments;

    @BindView(R.id.laterReportAction)
    Button laterButton;

    @BindView(R.id.sendReportActionButton)
    Button sendReportbutton;

    @BindView(R.id.firstTick)
    ImageView firstSelection;

    @BindView(R.id.secondTick)
    ImageView secondSelection;

    @BindView(R.id.thirdTick)
    ImageView thirdSelection;

    private String selectedItem = "Moderate";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendtraffic_report);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey("type")) {

                type = getIntent().getExtras().getString("type");
            }
        }

        back_image.setOnClickListener(this);
        closeImg.setOnClickListener(this);
        laterButton.setOnClickListener(this);
        sendReportbutton.setOnClickListener(this);
        firstThumbImage.setOnClickListener(this);
        secondThumbImage.setOnClickListener(this);
        thirdThumbImage.setOnClickListener(this);

        if (!type.equals("")) {

            switch (type) {

                case "Traffic":

                    parentImageView.setImageResource(R.drawable.ic_ic_traffic_jam);
                    parentImageText.setText("Traffic jam");

                    firstThumbImage.setImageResource(R.drawable.ic_moderate_traffic);
                    secondThumbImage.setImageResource(R.drawable.ic_moving_traffic);
                    thirdThumbImage.setImageResource(R.drawable.ic_standstill_traffic);

                    firstthumbtext.setText("Moderate");
                    seconthumbText.setText("Heavy");
                    thirdThumbText.setText("Standstill");

                    break;

                case "Police":

                    parentImageView.setImageResource(R.drawable.ic_ic_police);
                    parentImageText.setText("Police");

                    firstThumbImage.setImageResource(R.drawable.ic_visible);
                    secondThumbImage.setImageResource(R.drawable.ic_hidden);
                    thirdThumbImage.setImageResource(R.drawable.ic_otherside);

                    firstthumbtext.setText("Visible");
                    seconthumbText.setText("Hidden");
                    thirdThumbText.setText("Otherside");

                    break;

            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.img_back:
                finish();
                break;

            case R.id.img_close:
                finishAffinity();
                break;

            case R.id.laterReportAction:

                finish();
                break;

            case R.id.firstIcon:

                handleSelectionDeselection("1");
                break;

            case R.id.secondthumbnail:
                handleSelectionDeselection("2");
                break;

            case R.id.thumbnail:
                handleSelectionDeselection("3");
                break;

            case R.id.sendReportActionButton:
                Utils.sendReportToServer(SendReportDetailActivity.this, "5d9ecfea41bd442ce00ea5dd", parentImageText.getText().toString(), selectedItem);
                break;
        }
    }

    private void handleSelectionDeselection(String selected) {

        switch (selected) {

            case "1":

                selectedItem = firstthumbtext.getText().toString();
                firstSelection.setVisibility(View.VISIBLE);
                secondSelection.setVisibility(View.INVISIBLE);
                thirdSelection.setVisibility(View.INVISIBLE);
                break;

            case "2":

                selectedItem = seconthumbText.getText().toString();
                firstSelection.setVisibility(View.INVISIBLE);
                secondSelection.setVisibility(View.VISIBLE);
                thirdSelection.setVisibility(View.INVISIBLE);
                break;


            case "3":

                selectedItem = thirdThumbText.getText().toString();
                firstSelection.setVisibility(View.INVISIBLE);
                secondSelection.setVisibility(View.INVISIBLE);
                thirdSelection.setVisibility(View.VISIBLE);
                break;

        }
    }
}