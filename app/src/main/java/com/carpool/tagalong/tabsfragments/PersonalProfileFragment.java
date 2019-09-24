package com.carpool.tagalong.tabsfragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.glide.GlideApp;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfileData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PersonalProfileFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int GALLERY_PICTURE = 125;
    HashMap<String, String> genderMap = new HashMap<>();
    HashMap<String, String> poolgenderMap = new HashMap<>();
    private com.carpool.tagalong.views.RegularTextView nameTxt, lastNameTxt, emailTxt, mobileNumberTxt, addressTxt, profileMianName, profileMainAddress, poolGender, gender, drove, rating, trips, zipCodeTxt, dobTxt;
    private EditText nameEdt, emailEdt, mobileNumberEdt, addressEdt, zipCodeEdt, dobEdt, lastNameEdt;
    private com.carpool.tagalong.views.RegularTextView saveTxt, editTxt;
    private Spinner genderSpinner, poolPreferenceSpinner;
    private String[] genderArray = new String[]{"Select Gender", "Male", "Female", "Other"};
    private String[] poolPreferenceArray = new String[]{"Select Pool Preference", "All", "Male", "Female"};
    private ImageView uploadProfilePic, profilePic;
    private ProgressBar profileLoader;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String txtDate, txtTime;
    private CountryCodePicker countryCodePickerProfile;

    public PersonalProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_profile_fragment, container, false);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

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
        zipCodeEdt = view.findViewById(R.id.zipCodeUserEdt);
        zipCodeTxt = view.findViewById(R.id.zipCodeUserTxt);
        dobEdt = view.findViewById(R.id.dobEdt);
        profileLoader = view.findViewById(R.id.profileLoader);
//        cityEdt = view.findViewById(R.id.city_edt);
//        cityTxt = view.findViewById(R.id.city_txt);
//        regionTxt = view.findViewById(R.id.region_txt);
//        regionEdt = view.findViewById(R.id.region_edt);
        dobEdt.setOnClickListener(this);

        dobTxt = view.findViewById(R.id.dobTxt);
        dobEdt.setOnClickListener(this);
        dobTxt.setOnClickListener(this);
        lastNameEdt = view.findViewById(R.id.lastnameEdt);
        lastNameTxt = view.findViewById(R.id.lastNameTxt);

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

        countryCodePickerProfile = view.findViewById(R.id.countryCodeSignIn);
        countryCodePickerProfile.setCustomMasterCountries("us");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < poolPreferenceArray.length; i++) {
                    poolgenderMap.put(String.valueOf(i), poolPreferenceArray[i]);
                }

                for (int i = 0; i < genderArray.length; i++) {
                    genderMap.put(String.valueOf(i), genderArray[i]);
                }
            }
        }).start();

        if (DataManager.getModelUserProfileData() != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    GlideApp.with(getActivity())
                            .load(DataManager.modelUserProfileData.getProfile_pic())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profilePic);
                }
            });
        }
    }

    private boolean checkStoragePermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            final ModelUserProfileData data = DataManager.modelUserProfileData;

            if (data != null) {

                nameTxt.setText(data.getUserName());
                lastNameTxt.setText(data.getLast_name());
                dobTxt.setText(data.getDob());
                zipCodeTxt.setText(data.getZipcode() + "");
                emailTxt.setText(data.getEmail());
                mobileNumberTxt.setText(data.getMobileNo());
                addressTxt.setText(data.getAddress());
                profileMianName.setText(data.getUserName());
                profileMainAddress.setText(data.getAddress());
                drove.setText(data.getDrove());
                rating.setText(data.getRating() + "");
                trips.setText(data.getTrips() + "");

                handleGenderPreferences(data);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGenderPreferences(ModelUserProfileData data) {

        try {

            if (data.getGender() != null) {

                Iterator it = genderMap.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    if (pair.getValue().toString().equalsIgnoreCase(data.getGender())) {

                        genderSpinner.setSelection(Integer.parseInt(pair.getKey().toString()));
                        gender.setText(pair.getValue().toString());
                    }

                    // System.out.println(pair.getKey() + " = " + pair.getValue());
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
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            case R.id.dobEdt:
                handleDateTimePickerAction();
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
            if (bitmap != null)
                reduceImageAndSet(bitmap);
        } catch (Exception e) {
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
                        profilePic.setImageBitmap(b2);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadProfilePicToServer(Uri uri) {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
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

        if (checkStoragePermission()) {

            Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            picIntent.putExtra("return-data", true);
            startActivityForResult(picIntent, GALLERY_PICTURE);
        }
    }

    private void handleSavePersonalDetails() {

        try {

            if (genderSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select a gender!!", Toast.LENGTH_LONG).show();
                return;
            }

            if (lastNameEdt.getText().toString().trim().equals("")) {
                Toast.makeText(getActivity(), "Last name is mandatory!!", Toast.LENGTH_LONG).show();
                return;
            }

            editTxt.setVisibility(View.VISIBLE);
            saveTxt.setVisibility(View.GONE);

            nameTxt.setText(nameEdt.getText().toString().trim());
            lastNameTxt.setText(lastNameEdt.getText().toString().trim());
            emailTxt.setText(emailEdt.getText().toString().trim());
            mobileNumberTxt.setText(mobileNumberEdt.getText().toString().trim());
            addressTxt.setText(addressEdt.getText().toString().trim());
            zipCodeTxt.setText(zipCodeEdt.getText().toString().trim());
            dobTxt.setText(dobEdt.getText().toString().trim());

            gender.setText(genderSpinner.getSelectedItem().toString().trim());
            poolGender.setText(poolPreferenceSpinner.getSelectedItem().toString().trim());
            nameEdt.setVisibility(View.GONE);
            lastNameEdt.setVisibility(View.GONE);
            zipCodeEdt.setVisibility(View.GONE);
            dobEdt.setVisibility(View.GONE);
            emailEdt.setVisibility(View.GONE);
            mobileNumberEdt.setVisibility(View.GONE);
            addressEdt.setVisibility(View.GONE);
            genderSpinner.setVisibility(View.GONE);
            poolPreferenceSpinner.setVisibility(View.GONE);
            nameTxt.setVisibility(View.VISIBLE);
            emailTxt.setVisibility(View.VISIBLE);
            lastNameTxt.setVisibility(View.VISIBLE);
            dobTxt.setVisibility(View.VISIBLE);
            zipCodeTxt.setVisibility(View.VISIBLE);
            mobileNumberTxt.setVisibility(View.VISIBLE);
            addressTxt.setVisibility(View.VISIBLE);
            gender.setVisibility(View.VISIBLE);
            poolGender.setVisibility(View.VISIBLE);

            savePersonalDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editPersonalDetails() {

        try {

            saveTxt.setVisibility(View.VISIBLE);
            editTxt.setVisibility(View.GONE);

            nameEdt.setText(nameTxt.getText().toString());
            lastNameEdt.setText(lastNameTxt.getText().toString());
            dobEdt.setText(dobTxt.getText().toString());
            emailEdt.setText(emailTxt.getText().toString());
            mobileNumberEdt.setText(mobileNumberTxt.getText().toString());
            addressEdt.setText(addressTxt.getText().toString());
            zipCodeEdt.setText(zipCodeTxt.getText().toString());

            nameEdt.setVisibility(View.VISIBLE);
            lastNameEdt.setVisibility(View.VISIBLE);
            dobEdt.setVisibility(View.VISIBLE);
            zipCodeEdt.setVisibility(View.VISIBLE);
            emailEdt.setVisibility(View.VISIBLE);
            mobileNumberEdt.setVisibility(View.VISIBLE);
            addressEdt.setVisibility(View.VISIBLE);
            genderSpinner.setVisibility(View.VISIBLE);
            poolPreferenceSpinner.setVisibility(View.VISIBLE);

            nameTxt.setVisibility(View.GONE);
            lastNameTxt.setVisibility(View.GONE);

            dobTxt.setVisibility(View.GONE);
            zipCodeTxt.setVisibility(View.GONE);
            emailTxt.setVisibility(View.GONE);
            mobileNumberTxt.setVisibility(View.GONE);
            addressTxt.setVisibility(View.GONE);
            gender.setVisibility(View.GONE);
            poolGender.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePersonalDetails() {

        try {
            if (Utils.isNetworkAvailable(getActivity())) {

                try {
                    ModelUpdateProfileRequest modelUpdateProfileRequest = new ModelUpdateProfileRequest();
                    modelUpdateProfileRequest.setUserName(nameTxt.getText().toString());
                    modelUpdateProfileRequest.setEmail(emailTxt.getText().toString());
                    modelUpdateProfileRequest.setAddress(addressTxt.getText().toString());
                    modelUpdateProfileRequest.setLast_name(lastNameTxt.getText().toString());
                    modelUpdateProfileRequest.setDob(dobTxt.getText().toString());

                    if (genderSpinner.getSelectedItemPosition() != 0) {
                        modelUpdateProfileRequest.setGender(genderSpinner.getSelectedItem().toString());
                    } else {
                        Toast.makeText(getActivity(), "Please select gender!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (mobileNumberTxt.getText().toString().length() < 10) {
                        Toast.makeText(getActivity(), "Please enter valid mobile number!", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        modelUpdateProfileRequest.setMobileNo(countryCodePickerProfile.getSelectedCountryCode() + "" + mobileNumberTxt.getText().toString());
                    }

                    if (zipCodeTxt.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Please enter valid zipCode", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        modelUpdateProfileRequest.setZipcode(Integer.parseInt(zipCodeTxt.getText().toString()));
                    }

                    if (!isValidEmaillId(emailTxt.getText().toString())) {
                        Toast.makeText(getActivity(), "Please enter valid email!", Toast.LENGTH_LONG).show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Please check internet connection!!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (checkStoragePermission()) {
                Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                picIntent.putExtra("return-data", true);
                startActivityForResult(picIntent, GALLERY_PICTURE);
            } else {
                Toast.makeText(getActivity(), "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void handleDateTimePickerAction() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.CustomDatePickerDialog, this, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;
        if (month < 10) {
            txtDate = (year + "/" + "0" + month + "/" + dayOfMonth);
        } else
            txtDate = (year + "/" + month + "/" + dayOfMonth);

        dobEdt.setText(txtDate);
    }
}