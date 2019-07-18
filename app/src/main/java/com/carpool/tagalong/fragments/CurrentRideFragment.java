package com.carpool.tagalong.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.activities.MainActivity;
import com.carpool.tagalong.activities.StripePaymentActivity;
import com.carpool.tagalong.adapter.OnBoardRidersAdapter;
import com.carpool.tagalong.adapter.TimelineAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelPaymentRequest;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.BitmapUtils;
import com.carpool.tagalong.utils.ProgressDialogLoader;
import com.carpool.tagalong.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentRideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentRideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentRideFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSIONS_REQUEST = 132;
    private static final int IMAGE_PICK_REQUEST = 134;
    private static String postPath = "";
    private com.carpool.tagalong.views.RegularTextView recent_ride_txt, userName, startLocationName, endLocationName, startRideTime, estimatedCostOfRide;
    private CircleImageView profilePic, postPic;
    private Context context;
    private Button cancelButton, requestedBtn,  addRideBtn;
    private ModelGetCurrentRideResponse modelGetCurrentRideResponse;
    private RecyclerView onBoardRecyclerView;
    private RecyclerView timeLineRecView;
    private OnBoardRidersAdapter onBoardRidersAdapter;
    private TimelineAdapter timelineAdapter;
    private List<ModelGetCurrentRideResponse.Timeline> timelineData = new ArrayList<>();
    private ImageView selectImageForPost, selectedImageForPost;
    private LinearLayout uploadPicLytBtn;
    private Button postImage;
    private RelativeLayout  dropmessage1;
    private CurrentRideFragment.OnFragmentInteractionListener listener;

    public CurrentRideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentRideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentRideFragment newInstance(String param1, String param2, OnFragmentInteractionListener listener) {
        CurrentRideFragment fragment = new CurrentRideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;

        if (DataManager.status == 0) {

            Toast.makeText(getActivity(), "You don't have any ride currently!!", Toast.LENGTH_LONG).show();
            view = inflater.inflate(R.layout.fragment_current_ride, container, false);
            addRideBtn = view.findViewById(R.id.add_ride_btn);
            addRideBtn.setOnClickListener(this);

            try {
                listener = (OnFragmentInteractionListener) getActivity();
                listener.hideShareIcon();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        } else {

            view = inflater.inflate(R.layout.fragment_current_ride2, container, false);
            recent_ride_txt     = view.findViewById(R.id.ride_timing);
            userName            = view.findViewById(R.id.userName);
            startLocationName   = view.findViewById(R.id.start_point_source_name);
            endLocationName     = view.findViewById(R.id.end_point_dest_name);
            startRideTime       = view.findViewById(R.id.startRideTime);
            estimatedCostOfRide = view.findViewById(R.id.estimated_cost);
            profilePic   = view.findViewById(R.id.user_image);
            postPic      = view.findViewById(R.id.image_user_1);
            cancelButton = view.findViewById(R.id.cancel_ride_txt);
            requestedBtn = view.findViewById(R.id.button_ride);
//            paynow = view.findViewById(R.id.button_payNow);
            uploadPicLytBtn = view.findViewById(R.id.post_image_layout);
            postImage = view.findViewById(R.id.post_image_btn);

//            paynow.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            requestedBtn.setOnClickListener(this);

            onBoardRecyclerView  = view.findViewById(R.id.onBoardRecView);
            timeLineRecView      = view.findViewById(R.id.timeline_recView);
            selectImageForPost   = view.findViewById(R.id.select_image_post);
            selectedImageForPost = view.findViewById(R.id.selected_image);
//            dropmessage  = view.findViewById(R.id.drop_message);
            dropmessage1 = view.findViewById(R.id.drop_message1);

            uploadPicLytBtn.setOnClickListener(this);
            postImage.setOnClickListener(this);

            getCurrentRide();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getCurrentRide() {

        try {

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.getCurrentRide(TagALongPreferenceManager.getToken(context)).enqueue(new Callback<ModelGetCurrentRideResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetCurrentRideResponse> call, Response<ModelGetCurrentRideResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {
                                int status = response.body().getStatus();

                                if (status == 1) {
                                    handleCurrentRide(response.body());
                                } else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetCurrentRideResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Log.e("Current Ride", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCurrentRide(ModelGetCurrentRideResponse data) {

        if (data != null) {

            this.modelGetCurrentRideResponse = data;

            if (data.getRideData().isDrive()) {

                handleCurrentRideForDriver();

            } else {
                handleCurrentRideForRider();
            }
        }
    }

    private void handleCurrentRideForRider() {

        recent_ride_txt.setVisibility(View.GONE);

        ModelGetCurrentRideResponse.DriverDetails driverDetails = modelGetCurrentRideResponse.getRideData().getDriverDetails();
        if (driverDetails != null) {
            String name = driverDetails.getUserName();
            userName.setText(name);
        }

        String startLocName  = modelGetCurrentRideResponse.getRideData().getStartLocation();
        String endLocation   = modelGetCurrentRideResponse.getRideData().getEndLocation();
        String rideTime      = modelGetCurrentRideResponse.getRideData().getRideDateTime();
        String estimatedcost = String.valueOf(modelGetCurrentRideResponse.getRideData().getEstimatedFare());

        startLocationName.setText(startLocName);
        endLocationName.setText(endLocation);
        startRideTime.setText(rideTime);
        estimatedCostOfRide.setText(estimatedcost);

        if (modelGetCurrentRideResponse.getRideData().getStatus() == Constants.REQUESTED) {
            requestedBtn.setVisibility(View.VISIBLE);
            dropmessage1.setVisibility(View.GONE);

        } else if (modelGetCurrentRideResponse.getRideData().getStatus() == Constants.ACCEPTED) {
            requestedBtn.setVisibility(View.GONE);
            dropmessage1.setVisibility(View.VISIBLE);
//
//            if (modelGetCurrentRideResponse.getRideData().isPayStatus()) {
//
//                dropmessage.setVisibility(View.GONE);
//                dropmessage1.setVisibility(View.VISIBLE);
//                paynow.setVisibility(View.GONE);
//
//            } else {
//                dropmessage1.setVisibility(View.GONE);
//                dropmessage.setVisibility(View.VISIBLE);
//                paynow.setVisibility(View.VISIBLE);
//            }
        }

        timelineAdapter = new TimelineAdapter(getActivity(), timelineData);
        onBoardRidersAdapter = new OnBoardRidersAdapter(getActivity(), modelGetCurrentRideResponse.getRideData().getOnBoard(), null);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        onBoardRecyclerView.setLayoutManager(mLayoutManager);
        onBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        onBoardRecyclerView.setAdapter(onBoardRidersAdapter);

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        timeLineRecView.setLayoutManager(mLayoutManager1);
        timeLineRecView.setItemAnimator(new DefaultItemAnimator());
        timeLineRecView.setAdapter(timelineAdapter);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_avatar_12)
                .error(R.drawable.avatar_avatar_12);

        Glide.with(getActivity())
                .load(modelGetCurrentRideResponse.getRideData().getDriverDetails().getProfile_pic())
                .apply(options)
                .into(profilePic);

       // profile image in the upload timeline section
        Glide.with(getActivity())
                .load(modelGetCurrentRideResponse.getRideData().getProfile_pic())
                .apply(options)
                .into(postPic);
    }

    private void handleCurrentRideForDriver() {
//        ((HomeActivity) getActivity()).handleCurrentRideDriver(modelGetCurrentRideResponse);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.cancel_ride_txt:
                showCancelAlert();
                break;

//            case R.id.button_payNow:
//                handlePayNow();
//                break;

            case R.id.add_ride_btn:
                handleAddRide();
                break;

            case R.id.post_image_layout:
                if (checkAndRequestPermissions()) {
                    uploadPic();
                }
                break;

            case R.id.post_image_btn:
                handlePostImage();
                break;
        }
    }

    private void uploadPic() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    private void handlePostImage() {

        addPost(postPath);
    }

    private void handleAddRide() {
        HomeActivity activity = ((HomeActivity) getActivity());
        activity.handleDrawer();
        activity.handleHomeLayoutClick();
    }

    private void handlePayNow() {

        Intent intent = new Intent(context, StripePaymentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ModelPaymentRequest modelPaymentRequest = new ModelPaymentRequest();
        modelPaymentRequest.setAmount(modelGetCurrentRideResponse.getRideData().getEstimatedFare());
        modelPaymentRequest.setDriverId(modelGetCurrentRideResponse.getRideData().getDriverDetails().getUserId());
        modelPaymentRequest.setRideId(modelGetCurrentRideResponse.getRideData().getDriverDetails().get_id());
        modelPaymentRequest.setRequestId(modelGetCurrentRideResponse.getRideData().get_id());
        intent.putExtra(getString(R.string.order_key), modelPaymentRequest);

        startActivity(intent);
    }

    private void showCancelAlert() {
        showCustomCancelRideDialog(getActivity());
    }

    private void handleCancelRide(String reason) {

        try {

            ModelCancelOwnRideRequest modelCancelOwnRideRequest = new ModelCancelOwnRideRequest();
            modelCancelOwnRideRequest.setRequestId(modelGetCurrentRideResponse.getRideData().get_id());
            modelCancelOwnRideRequest.setCancelReason(reason);

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    restClientRetrofitService.cancelRide(TagALongPreferenceManager.getToken(getActivity()), modelCancelOwnRideRequest).enqueue(new Callback<ModelDocumentStatus>() {

                        @Override
                        public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {

                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    DataManager.setStatus(0);
                                    HomeActivity activity = ((HomeActivity) getActivity());
                                    activity.handleDrawer();
                                    activity.handleHomeLayoutClick();
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomCancelRideDialog(Activity context) {

        com.carpool.tagalong.views.RegularTextView buttonPositive;
        com.carpool.tagalong.views.RegularTextView buttonNegative;
        AlertDialog alertDialog;
        final EditText reasonText;

        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.cancel_ride_alert_doalog_lyt, null);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(dialogView);

            reasonText = dialogView.findViewById(R.id.cancel_reason_edt);
            buttonNegative = dialogView.findViewById(R.id.no_cancel_ride);
            buttonPositive = dialogView.findViewById(R.id.cancel_ride);
            alertDialog = dialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;
            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCancelRide(reasonText.getText().toString());
                    finalAlertDialog.cancel();
                }
            });

            buttonNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.cancel();
                }
            });

            alertDialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void addPost(String mediaPath) {

//        String mimeType = getActivity().getContentResolver().getType(Uri.parse(mediaPath));

        File file = new File(getPath(Uri.parse(mediaPath)));

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), Constants.TYPE_IMAGE);

        RequestBody rideId = RequestBody.create(MediaType.parse("text/plain"), modelGetCurrentRideResponse.getRideData().get_id());

        // Create a request body with file and image/video media type

        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/png"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("media", file.getName(), fileReqBody);

        if (Utils.isNetworkAvailable(getActivity())) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                ProgressDialogLoader.progressDialogCreation(getActivity(), context.getString(R.string.please_wait));

                restClientRetrofitService.addPost(TagALongPreferenceManager.getToken(getActivity()), rideId, type, part).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        ProgressDialogLoader.progressDialogDismiss();

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                selectImageForPost.setVisibility(View.VISIBLE);
                                selectedImageForPost.setVisibility(View.GONE);
                                getPost();

                            } else {
                                Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                            }
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
                        Log.e("Upload Document", "FAILURE verification");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void getPost() {

        try {

            ModelGetTimelineRequest modelGetTimelineRequest = new ModelGetTimelineRequest();
            modelGetTimelineRequest.setRideId(modelGetCurrentRideResponse.getRideData().get_id());

            if (Utils.isNetworkAvailable(context)) {

                RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

                if (restClientRetrofitService != null) {

                    ProgressDialogLoader.progressDialogCreation(getActivity(), getActivity().getString(R.string.please_wait));

                    restClientRetrofitService.getPost(TagALongPreferenceManager.getToken(getActivity()), modelGetTimelineRequest).enqueue(new Callback<ModelGetTimelineResponse>() {

                        @Override
                        public void onResponse(Call<ModelGetTimelineResponse> call, Response<ModelGetTimelineResponse> response) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (response.body() != null) {

                                if (response.body().getStatus() == 1) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    timelineData = response.body().getData();
                                    timelineAdapter = new TimelineAdapter(context, timelineData);
                                    timeLineRecView.setAdapter(timelineAdapter);
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelGetTimelineResponse> call, Throwable t) {

                            ProgressDialogLoader.progressDialogDismiss();

                            if (t != null && t.getMessage() != null) {
                                t.printStackTrace();
                            }
                            Log.e("Cancel Rider Own Ride", "FAILURE verification");
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

            ProgressDialogLoader.progressDialogDismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("image_uri", uri.toString());
            startActivityForResult(intent, Constants.IMAGE_FILTER_CODE);

        } else if (requestCode == Constants.IMAGE_FILTER_CODE && resultCode == RESULT_OK && data != null) {

            if (data.getExtras() != null) {
                String path = data.getStringExtra("result_image");

                selectImageForPost.setVisibility(View.GONE);
                selectedImageForPost.setVisibility(View.VISIBLE);
                postPath = path;
                reduceImageAndSet();
            }
        }
    }

    private void reduceImageAndSet() {
        // we'll start with the original picture already open to a file

        try {

            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(getActivity(), Uri.parse(postPath), 100, 100);
            selectedImageForPost.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {
            switch (requestCode) {

                case MY_PERMISSIONS_REQUEST:

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        uploadPic();
                    } else {
                        showAlertDialog("This app needs storage permission", "Need Storage Permission", false, 1);
                    }
                    break;
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String message, String title, boolean cancelable, int code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        if (code == 1) {
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Fragment fragmentName);

        void hideShareIcon();
    }
}