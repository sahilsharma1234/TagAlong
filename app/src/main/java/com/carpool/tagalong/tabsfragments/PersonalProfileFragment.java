package com.carpool.tagalong.tabsfragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfileData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PersonalProfileFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_PICTURE = 125;
    HashMap<String, String> genderMap = new HashMap<>();
    HashMap<String, String> poolgenderMap = new HashMap<>();
    private TextView nameTxt, emailTxt, mobileNumberTxt, addressTxt, profileMianName, profileMainAddress, poolGender, gender, drove, rating, trips;
    private EditText nameEdt, emailEdt, mobileNumberEdt, addressEdt;
    private TextView saveTxt, editTxt;
    private Spinner genderSpinner, poolPreferenceSpinner;
    private String[] genderArray = new String[]{"Select Gender", "Male", "Female", "Other"};
    private String[] poolPreferenceArray = new String[]{"Select Pool Preference", "All", "Male", "Female"};
    private ImageView uploadProfilePic, profilePic;

    public PersonalProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_profile_fragment, container, false);
        nameTxt = view.findViewById(R.id.name_user_profile);
        emailTxt = view.findViewById(R.id.email_id_user);
        mobileNumberTxt = view.findViewById(R.id.mobile_nuber_user);
        addressTxt = view.findViewById(R.id.address_user);
        gender = view.findViewById(R.id.gender_user_txt);
        poolGender = view.findViewById(R.id.pool_gender_txt);
        nameEdt = view.findViewById(R.id.name_user_profile_edt);
        emailEdt = view.findViewById(R.id.email_id_user_edt);
        mobileNumberEdt = view.findViewById(R.id.mobile_nuber_user_edt);
        addressEdt = view.findViewById(R.id.address_user_edt);
        saveTxt = view.findViewById(R.id.save_personal_details);
        editTxt = view.findViewById(R.id.edit_personal_details);
        uploadProfilePic = view.findViewById(R.id.upload_profile_pic_btn);
        profilePic = view.findViewById(R.id.profile_pic);
        profileMainAddress = view.findViewById(R.id.profile_main_address);
        profileMianName = view.findViewById(R.id.profile_main_name);
        genderSpinner = view.findViewById(R.id.gender_spinner);
        poolPreferenceSpinner = view.findViewById(R.id.pool_gender_spinner);

        drove = view.findViewById(R.id.drove_txt);
        rating = view.findViewById(R.id.rating);
        trips = view.findViewById(R.id.trips);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,
                        genderArray); //selected item will look like a spinner set from XML
        genderAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        ArrayAdapter<String> poolgenderAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,
                        poolPreferenceArray); //selected item will look like a spinner set from XML
        poolgenderAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);
        poolPreferenceSpinner.setAdapter(poolgenderAdapter);

        saveTxt.setOnClickListener(this);
        editTxt.setOnClickListener(this);
        uploadProfilePic.setOnClickListener(this);

        for (int i = 0; i < poolPreferenceArray.length; i++) {
            poolgenderMap.put(String.valueOf(i), poolPreferenceArray[i]);
        }

        for (int i = 0; i < genderArray.length; i++) {
            genderMap.put(String.valueOf(i), genderArray[i]);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ModelUserProfileData data = DataManager.modelUserProfileData;

        if (data != null) {

            nameTxt.setText(data.getUserName());
            emailTxt.setText(data.getEmail());
            mobileNumberTxt.setText(data.getMobileNo());
            addressTxt.setText(data.getAddress());
            profileMianName.setText(data.getUserName());
            profileMainAddress.setText(data.getAddress());
            drove.setText(data.getDrove());
            rating.setText(data.getRating() + "");
            trips.setText(data.getTrips() + "");

            if (data.getGender() != null) {

                Iterator it = genderMap.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    if (pair.getValue().toString().equalsIgnoreCase(data.getGender())) {

                        genderSpinner.setSelection(Integer.parseInt(pair.getKey().toString()));
                        gender.setText(pair.getValue().toString());
                    }

                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

            if (data.getGenderPrefrance() != null) {

                Iterator it = poolgenderMap.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    if (pair.getValue().toString().equalsIgnoreCase(data.getGenderPrefrance())) {

                        poolPreferenceSpinner.setSelection(Integer.parseInt(pair.getKey().toString()));
                        poolGender.setText(pair.getValue().toString());
                    }

                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

            Glide.with(getActivity())
                    .load(data.getProfile_pic())
                    .into(profilePic);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.save_personal_details:
                handleSavePersonalDetails();
                break;

            case R.id.edit_personal_details:
                editPersonalDetails();
                break;

            case R.id.upload_profile_pic_btn:
                uploadProfilePic();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == GALLERY_PICTURE) {
                Image_Selecting_Task(data, 0);
            }
        }
    }

    private void Image_Selecting_Task(Intent data, int code) {

        try {

            Bitmap bitmap = null;

            if (code == 0) {

                if (data != null) {

                    Uri uri = data.getData();

                    if (uri != null) {

                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                        if (bitmap != null) {
                            uploadProfilePicToServer(uri);
                        }
                    }
                }
            } else {

                if (data != null) {

                    if (data.getExtras() != null) {

                        Bundle extras = data.getExtras();
                        bitmap = (Bitmap) extras.get("data");
                    }
                }
            }
            if(bitmap != null)
            reduceImageAndSet(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reduceImageAndSet(Bitmap bitmap) {

        try {

            int origWidth = bitmap.getWidth();
            int origHeight = bitmap.getHeight();

            final int desHeight = 350;//or the width you need

            if (origHeight > desHeight) {
                // picture is wider than we want it, we calculate its target height
                int destWidth = origWidth / (origHeight / desHeight);
                // we create an scaled bitmap so it reduces the image, not just trim it
                final Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, desHeight, false);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                // compress to the format you want, JPEG, PNG...
                // 70 is the 0-100 quality percentage
                b2.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(b2).into(profilePic);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadProfilePicToServer(Uri uri) {

        File file = new File(getPath(uri));

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), Constants.PROFILE_PIC);

        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/png"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("documents", file.getName(), fileReqBody);

        if (Utils.isNetworkAvailable(getActivity())) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.uploadDocuments(TagALongPreferenceManager.getToken(getActivity()), part, type).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                ((HomeActivity) getActivity()).getUserProfile();

                            } else {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("Upload Profile PIC", "FAILURE Uploading profile pic");
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

    private void uploadProfilePic() {

        Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        picIntent.putExtra("return-data", true);
        startActivityForResult(picIntent, GALLERY_PICTURE);
    }

    private void handleSavePersonalDetails() {

        if (genderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_LONG).show();
            return;
        }

        editTxt.setVisibility(View.VISIBLE);
        saveTxt.setVisibility(View.GONE);

        nameTxt.setText(nameEdt.getText().toString());
        emailTxt.setText(emailEdt.getText().toString());
        mobileNumberTxt.setText(mobileNumberEdt.getText().toString());
        addressTxt.setText(addressEdt.getText().toString());

        gender.setText(genderSpinner.getSelectedItem().toString());
        poolGender.setText(poolPreferenceSpinner.getSelectedItem().toString());

        nameEdt.setVisibility(View.GONE);
        emailEdt.setVisibility(View.GONE);
        mobileNumberEdt.setVisibility(View.GONE);
        addressEdt.setVisibility(View.GONE);
        genderSpinner.setVisibility(View.GONE);
        poolPreferenceSpinner.setVisibility(View.GONE);

        nameTxt.setVisibility(View.VISIBLE);
        emailTxt.setVisibility(View.VISIBLE);
        mobileNumberTxt.setVisibility(View.VISIBLE);
        addressTxt.setVisibility(View.VISIBLE);
        gender.setVisibility(View.VISIBLE);
        poolGender.setVisibility(View.VISIBLE);

        savePersonalDetails();
    }

    private void editPersonalDetails() {

        saveTxt.setVisibility(View.VISIBLE);
        editTxt.setVisibility(View.GONE);

        nameEdt.setText(nameTxt.getText().toString());
        emailEdt.setText(emailTxt.getText().toString());
        mobileNumberEdt.setText(mobileNumberTxt.getText().toString());
        addressEdt.setText(addressTxt.getText().toString());

        nameEdt.setVisibility(View.VISIBLE);
        emailEdt.setVisibility(View.VISIBLE);
        mobileNumberEdt.setVisibility(View.VISIBLE);
        addressEdt.setVisibility(View.VISIBLE);
        genderSpinner.setVisibility(View.VISIBLE);
        poolPreferenceSpinner.setVisibility(View.VISIBLE);

        nameTxt.setVisibility(View.GONE);
        emailTxt.setVisibility(View.GONE);
        mobileNumberTxt.setVisibility(View.GONE);
        addressTxt.setVisibility(View.GONE);
        gender.setVisibility(View.GONE);
        poolGender.setVisibility(View.GONE);
    }

    private void savePersonalDetails() {

        if (Utils.isNetworkAvailable(getActivity())) {

            ModelUpdateProfileRequest modelUpdateProfileRequest = new ModelUpdateProfileRequest();
            modelUpdateProfileRequest.setUserName(nameTxt.getText().toString());
            modelUpdateProfileRequest.setEmail(emailTxt.getText().toString());
            modelUpdateProfileRequest.setMobileNo(mobileNumberTxt.getText().toString());
            modelUpdateProfileRequest.setAddress(addressTxt.getText().toString());

            if (genderSpinner.getSelectedItemPosition() != 0) {
                modelUpdateProfileRequest.setGender(genderSpinner.getSelectedItem().toString());
            } else {
                Toast.makeText(getActivity(), "Please select gender!", Toast.LENGTH_LONG).show();
                return;
            }

            if (poolPreferenceSpinner.getSelectedItemPosition() != 0) {
                modelUpdateProfileRequest.setGenderPrefrance(poolPreferenceSpinner.getSelectedItem().toString());
            } else {
                Toast.makeText(getActivity(), "Please select pool gender!", Toast.LENGTH_LONG).show();
                return;
            }

            Log.i("PERSONAL DETAILS", "PROFILE REQUEST: " + modelUpdateProfileRequest.toString());

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.updateProfile(TagALongPreferenceManager.getToken(getActivity()), modelUpdateProfileRequest).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

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

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SAVE PERSONAL DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
        }
    }
}