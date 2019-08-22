package com.carpool.tagalong.tabsfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.adapter.DocumentListAdapter;
import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.managers.DataManager;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelDocuments;
import com.carpool.tagalong.models.ModelGetCarBrandModelResponse;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfileData;
import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.carpool.tagalong.retrofit.ApiClient;
import com.carpool.tagalong.retrofit.RestClientInterface;
import com.carpool.tagalong.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class DrivingProfileFragment extends Fragment implements View.OnClickListener, DocumentListAdapter.drivinginteraction {

    private static final int MY_PERMISSIONS_REQUEST = 133;
    private static final int IMAGE_PICK_REQUEST = 135;
    private static final int GALLERY_PICTURE = 125;
    private static final int CAMERA_PICTURE = 126;
    private com.carpool.tagalong.views.RegularTextView vehicleTxt, vehicleRegistrationTxt, vehicleYearTxt, vehicleColorTxt, vehicleModelTxt;
    private EditText vehicleRegNumEdt;
    private com.carpool.tagalong.views.RegularTextView saveTxt, editTxt;
    private RelativeLayout uploadImage, mainDocuLyt, progressBarLyt;
    private CheckBox smokeCheckBox, kidsCheckBox, bagsCheckBox;
    private Spinner vehicleBrandSpinner, vehicleModelSpinner, vehicleColorSpinner, vehicleYearSpinner;

    private List<String> vehicleBrandArrayList, vehicleModelArrayList, vehicleColorArrayList, vehicleYearArrayListString;
    private List<Integer> vehicleYearArrayList;

    private HashMap<String, List<String>> brandModelListMap = new HashMap<>();
    private HashMap<String, String> modelIdmap = new HashMap<>();
    private ModelUserProfileData data = null;
    private String mCurrentPhotoPath;
    private RecyclerView recyclerViewDocuments;
    private List<ModelDocuments> documentsList;
    private DocumentListAdapter documentListAdapter;
    private Uri photoURi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.driving_profile_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        vehicleTxt = view.findViewById(R.id.vehicle);
        vehicleRegistrationTxt = view.findViewById(R.id.vehicle_number);
        vehicleYearTxt = view.findViewById(R.id.vehicle_year);
        vehicleColorTxt = view.findViewById(R.id.vehicle_color);
        vehicleModelTxt = view.findViewById(R.id.vehicle_model_txt);
        vehicleRegNumEdt = view.findViewById(R.id.vehicle_number_edt);
        saveTxt = view.findViewById(R.id.save_driving_preferences);
        editTxt = view.findViewById(R.id.edit_driving_preferences);
        uploadImage = view.findViewById(R.id.upload_document_action);
        mainDocuLyt = view.findViewById(R.id.document_uploaded_container);
        progressBarLyt = view.findViewById(R.id.lytBarDriving);
        smokeCheckBox = view.findViewById(R.id.smoke_prefe_chck);
        kidsCheckBox = view.findViewById(R.id.kids_travelling_chck);
        bagsCheckBox = view.findViewById(R.id.carry_bags_pref_chck);
        vehicleBrandSpinner = view.findViewById(R.id.vehicle_brand_spinner);
        vehicleModelSpinner = view.findViewById(R.id.vehicle_model_spinner);
        vehicleColorSpinner = view.findViewById(R.id.vehicle_color_spinner);
        vehicleYearSpinner = view.findViewById(R.id.vehicle_year_spinner);
        recyclerViewDocuments = view.findViewById(R.id.documentRecyclerView);

        saveTxt.setOnClickListener(this);
        editTxt.setOnClickListener(this);
        uploadImage.setOnClickListener(this);

        vehicleBrandArrayList = new ArrayList<>();
        vehicleModelArrayList = new ArrayList<>();
        vehicleColorArrayList = new ArrayList<>();
        vehicleYearArrayList = new ArrayList<>();
        vehicleYearArrayListString = new ArrayList<>();
        vehicleBrandArrayList.add(0, "Select brand");
        vehicleModelArrayList.add(0, "Select model");
        vehicleColorArrayList.add(0, "Select color");

    }

    private void handleStartSetup() {

        if (DataManager.getYearsList() != null) {

            for (int year : DataManager.getYearsList()) {
                vehicleYearArrayListString.add(year + "");
                vehicleYearArrayList.add(year);
            }
        } else {
            Utils.getYearsList(getActivity());
        }

        if (DataManager.getColorList() != null) {
            for (String color : DataManager.getColorList()) {
                vehicleColorArrayList.add(color);
            }
        } else {
            Utils.getColorList(getActivity());
        }

        // model spinner adapter setup
        final ArrayAdapter<String> modelAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item,
                        vehicleModelArrayList); //selected item will look like a spinner set from XML
        modelAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        vehicleModelSpinner.setAdapter(modelAdapter);

        // vehicle year adapter setup
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item,

                        vehicleYearArrayListString); //selected item will look like a spinner set from XML
        yearAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        vehicleYearSpinner.setAdapter(yearAdapter);

        // vehicle color adapter
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,
                        vehicleColorArrayList); //selected item will look like a spinner set from XML
        colorAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        vehicleColorSpinner.setAdapter(colorAdapter);

        vehicleBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String brand = vehicleBrandArrayList.get(position);

                if (vehicleModelArrayList != null && brandModelListMap != null) {

                    if (position == 0) {
                        vehicleModelArrayList.clear();
                        vehicleModelArrayList.add(0, "Select model");
                    } else {
                        vehicleModelArrayList.clear();
                        List<String> list = brandModelListMap.get(brand);
                        // addall was not working here thats why used this
                        for (String s : list)
                            vehicleModelArrayList.add(s);
                    }
                    modelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

//    private void reloadFragent(){
//
//        // Reload current fragment
//        Fragment frg = null;
//        frg = getActivity().getSupportFragmentManager().findFragmentByTag("Driving");
//        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
//    }

    public void Image_Picker_Dialog() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Pictures Option");
        myAlertDialog.setMessage("Select Picture Mode");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent,IMAGE_PICK_REQUEST);
                picIntent.putExtra("return-data", true);
                startActivityForResult(picIntent, GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        photoURi = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
//                        Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURi);
//                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, CAMERA_PICTURE);
                    }
                }
            }
        });
        myAlertDialog.show();
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean checkAndRequestPermissions() {

        int permissionStorage = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        try {

            switch (requestCode) {

                case MY_PERMISSIONS_REQUEST:

                    if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    } else {
                        showAlertDialog("This app needs storage permission", "Need Storage Permission", false, 1);
                    }
                    break;
            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
                Image_Picker_Dialog();
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

    private void handleSaveDrivingPreferences() {

        try {
            String brand = vehicleBrandSpinner.getSelectedItem().toString();
            String model = vehicleModelSpinner.getSelectedItem().toString();
            String year = vehicleYearSpinner.getSelectedItem().toString();
            String color = vehicleColorSpinner.getSelectedItem().toString();

            if (brand.equalsIgnoreCase("Select Brand") || model.equalsIgnoreCase("Select model") || year.equalsIgnoreCase("select model year") || color.equalsIgnoreCase("Select color")) {
                Toast.makeText(getActivity(), "Please fill all the details!!", Toast.LENGTH_SHORT).show();
                return;
            }

            editTxt.setVisibility(View.VISIBLE);
            saveTxt.setVisibility(View.GONE);

            vehicleTxt.setText(brand);
            vehicleRegistrationTxt.setText(vehicleRegNumEdt.getText().toString());
            vehicleYearTxt.setText(year);
            vehicleColorTxt.setText(color);
            vehicleModelTxt.setText(model);

            vehicleBrandSpinner.setVisibility(View.GONE);
            vehicleRegNumEdt.setVisibility(View.GONE);
            vehicleYearSpinner.setVisibility(View.GONE);
            vehicleColorSpinner.setVisibility(View.GONE);
            vehicleModelSpinner.setVisibility(View.GONE);

            vehicleTxt.setVisibility(View.VISIBLE);
            vehicleRegistrationTxt.setVisibility(View.VISIBLE);
            vehicleYearTxt.setVisibility(View.VISIBLE);
            vehicleColorTxt.setVisibility(View.VISIBLE);
            vehicleModelTxt.setVisibility(View.VISIBLE);

            saveDrivingDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editDrivingPreferences() {

        saveTxt.setVisibility(View.VISIBLE);
        editTxt.setVisibility(View.GONE);

        if (data != null) {
            //brand set
            for (int i = 0; i < vehicleBrandArrayList.size(); i++) {

                if (data.getDriverDetails().getVehicleBrand().equalsIgnoreCase(vehicleBrandArrayList.get(i))) {

                    vehicleBrandSpinner.setSelection(i);
                    break;
                }
            }

            // model set
            for (int i = 0; i < vehicleModelArrayList.size(); i++) {

                if(data.getDriverDetails().getVehicleModel() != null) {

                    if (data.getDriverDetails().getVehicleModel().equalsIgnoreCase(vehicleModelArrayList.get(i))) {

                        vehicleModelSpinner.setSelection(i);
                        break;
                    }
                }
            }

            // year set
            for (int i = 0; i < vehicleYearArrayList.size(); i++) {

                if (data.getDriverDetails().getVehicleYear() == vehicleYearArrayList.get(i)) {
                    vehicleYearSpinner.setSelection(i);
                    break;
                }
            }

            // color set
            for (int i = 0; i < vehicleColorArrayList.size(); i++) {

                if (data.getDriverDetails().getVehicleColor().equalsIgnoreCase(vehicleColorArrayList.get(i))) {

                    vehicleColorSpinner.setSelection(i);
                    break;
                }
            }

            vehicleRegNumEdt.setText(vehicleRegistrationTxt.getText().toString());
        }

        vehicleBrandSpinner.setVisibility(View.VISIBLE);
        vehicleRegNumEdt.setVisibility(View.VISIBLE);
        vehicleYearSpinner.setVisibility(View.VISIBLE);
        vehicleColorSpinner.setVisibility(View.VISIBLE);
        vehicleModelSpinner.setVisibility(View.VISIBLE);

        vehicleTxt.setVisibility(View.GONE);
        vehicleRegistrationTxt.setVisibility(View.GONE);
        vehicleYearTxt.setVisibility(View.GONE);
        vehicleColorTxt.setVisibility(View.GONE);
        vehicleModelTxt.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.save_driving_preferences:
                handleSaveDrivingPreferences();
                break;

            case R.id.edit_driving_preferences:
                editDrivingPreferences();
                break;

            case R.id.upload_document_action:
                if (checkAndRequestPermissions()) {
                    Image_Picker_Dialog();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == GALLERY_PICTURE) {
                Image_Selecting_Task(data, 0);
            } else {
                Image_Selecting_Task(data, 1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                getCarModelBrandListFromServer();
                handleStartSetup();
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                createDocumentsList();
            }
        });
    }

    private void createDocumentsList() {

        data = DataManager.modelUserProfileData;

        if (data != null) {

            vehicleColorTxt.setText(data.getDriverDetails().getVehicleColor());
            vehicleTxt.setText(data.getDriverDetails().getVehicleBrand());
            vehicleModelTxt.setText(data.getDriverDetails().getVehicleModel());
            vehicleRegistrationTxt.setText(data.getDriverDetails().getVehicleNumber());
            vehicleYearTxt.setText(data.getDriverDetails().getVehicleYear() + "");

            smokeCheckBox.setChecked(data.getDriverDetails().isSmoke());
            kidsCheckBox.setChecked(data.getDriverDetails().isAllowKids());
            bagsCheckBox.setChecked(data.getDriverDetails().getBags() == 0 ? false : true);

            if (data.getDocuments() != null && data.getDocuments().size() > 0) {

                documentsList = data.getDocuments();

                documentListAdapter = new DocumentListAdapter(getActivity(), documentsList, this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewDocuments.setLayoutManager(mLayoutManager);
                recyclerViewDocuments.setItemAnimator(new DefaultItemAnimator());
                recyclerViewDocuments.setAdapter(documentListAdapter);
            }
        }
    }

    private void Image_Selecting_Task(Intent data, int code) {

        try {
            Bitmap bitmap;

            if (code == 0) {

                Uri uri = data.getData();

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                if (bitmap != null) {

                    uploadDocToServer(uri);
                }

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", new File(mCurrentPhotoPath)));
                if (bitmap != null) {

                    uploadDocToServer(null);
                }
            }
//            reduceImageAndSet(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void reduceImageAndSet(Bitmap bitmap) {
//        // we'll start with the original picture already open to a file
//
//        try {
//
//            int origWidth = bitmap.getWidth();
//            int origHeight = bitmap.getHeight();
//
//            final int desHeight = 250;//or the width you need
//
//            if (origHeight > desHeight) {
//                // picture is wider than we want it, we calculate its target height
//                int destWidth = origWidth / (origHeight / desHeight);
//                // we create an scaled bitmap so it reduces the image, not just trim it
//                Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, desHeight, false);
//                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//                // compress to the format you want, JPEG, PNG...
//                // 70 is the 0-100 quality percentage
//                b2.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                addImageLayout(b2);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void addImageLayout(Bitmap bitmap) {

//        mainDocuLyt.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View inflatedLayout = inflater.inflate(R.layout.uploaded_document_review, null, false);

        ImageView document_image = inflatedLayout.findViewById(R.id.document);
        ImageView delete_document = inflatedLayout.findViewById(R.id.delete);
        ImageView refresh_document = inflatedLayout.findViewById(R.id.reload);

        delete_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlertDialog("Delete Document", true, 0);
            }
        });

        refresh_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (bitmap != null)
            document_image.setImageBitmap(bitmap);
        mainDocuLyt.addView(inflatedLayout);
    }

    private void showDeleteAlertDialog(String title, boolean cancelable, int code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage("Are you sure you want to delete this document?");
        builder.setCancelable(cancelable);

        if (code == 0) {
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    mainDocuLyt.removeAllViews();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.show();
    }

    private void uploadDocToServer(Uri uri) {

        File file;

        if (uri == null) {
            file = new File(mCurrentPhotoPath); // camera pic
        } else {
            file = new File(getPath(uri)); // gallery pic
        }

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), Constants.TYPE_DOCUMENT);

        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/png"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("documents", file.getName(), fileReqBody);

        if (Utils.isNetworkAvailable(getActivity())) {

            progressBarLyt.setVisibility(View.VISIBLE);
            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {
                restClientRetrofitService.uploadDocuments(TagALongPreferenceManager.getToken(getActivity()), part, type).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {
                        progressBarLyt.setVisibility(View.GONE);

                        if (response.body() != null) {

                            if (response.body().getStatus() == 1) {

                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                DataManager.isDocumentUploaded = true;

                                TagALongPreferenceManager.setDocumentUploadedStatus(getActivity(), true);
                                ((HomeActivity) getActivity()).getUserProfile();
                                // here we again creates the list of documents
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        createDocumentsList();
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {
                        progressBarLyt.setVisibility(View.GONE);

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

    private void saveDrivingDetails() {

        if (Utils.isNetworkAvailable(getActivity())) {

            ModelUpdateProfileRequest modelUpdateProfileRequest = new ModelUpdateProfileRequest();
            modelUpdateProfileRequest.setSmoke(smokeCheckBox.isChecked() ? true : false);
            modelUpdateProfileRequest.setBags(bagsCheckBox.isChecked() ? 1 : 0);
            modelUpdateProfileRequest.setAllowKids(kidsCheckBox.isChecked() ? true : false);
            modelUpdateProfileRequest.setVehicleBrand(vehicleTxt.getText().toString());
            modelUpdateProfileRequest.setVehicleModel(vehicleModelTxt.getText().toString());
            modelUpdateProfileRequest.setVehicleNumber(vehicleRegistrationTxt.getText().toString());
            modelUpdateProfileRequest.setVehicleYear(Integer.parseInt(vehicleYearTxt.getText().toString()));
            modelUpdateProfileRequest.setVehicleColor(vehicleColorTxt.getText().toString());

            Log.i("Driving DETAILS", "DRIVE PROFILE REQUEST: " + modelUpdateProfileRequest.toString());

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
                        Log.e("SAVE DRIVING DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void getCarModelBrandListFromServer() {

        if (Utils.isNetworkAvailable(getActivity())) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.getCarBrandModels().enqueue(new Callback<ModelGetCarBrandModelResponse>() {

                    @Override
                    public void onResponse(Call<ModelGetCarBrandModelResponse> call, Response<ModelGetCarBrandModelResponse> response) {

                        if (response.body() != null) {
//                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            handleResponse(response.body());
                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelGetCarBrandModelResponse> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("SAVE DRIVING DETAILS", "FAILURE SAVING PROFILE");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }

    private void handleResponse(ModelGetCarBrandModelResponse body) {

        if (body.getData() != null && body.getData().size() > 0) {

            List<ModelGetCarBrandModelResponse.ModelGetCarBrandModelResponseData> data = body.getData();
            List<String> modelList = new ArrayList<>();

            for (int i = 0; i < data.size(); i++) {

                for (ModelGetCarBrandModelResponse.ModelGetCarBrandModelResponseData carData : data) {

                    vehicleBrandArrayList.add(carData.getCarBrand()); // vehicle brand list gets populated

                    List<ModelGetCarBrandModelResponse.Models> models = carData.getModels();

                    for (ModelGetCarBrandModelResponse.Models model : models) {
                        modelList.add(model.getName()); // vehicle models for particular brand
                        modelIdmap.put(model.getName(), model.get_id()); // map for spinner getting id of model
                    }
                    brandModelListMap.put(carData.getCarBrand(), modelList); // map for populating spinner according to value changed
                    modelList = new ArrayList<>();
                }
            }

            ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            vehicleBrandArrayList); //selected item will look like a spinner set from XML

            brandAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);

            vehicleBrandSpinner.setAdapter(brandAdapter);
        }
    }

    @Override
    public void deleteDocument(String documentId, int index) {

        deleteDocumentFromServer(documentId);
        documentsList.remove(index);
        documentListAdapter.notifyDataSetChanged();
    }

    private void deleteDocumentFromServer(String docId) {

        if (Utils.isNetworkAvailable(getActivity())) {

            RestClientInterface restClientRetrofitService = new ApiClient().getApiService();

            if (restClientRetrofitService != null) {

                restClientRetrofitService.deleteDocument(TagALongPreferenceManager.getToken(getActivity()), docId).enqueue(new Callback<ModelDocumentStatus>() {

                    @Override
                    public void onResponse(Call<ModelDocumentStatus> call, Response<ModelDocumentStatus> response) {

                        if (response.body() != null) {
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelDocumentStatus> call, Throwable t) {

                        if (t != null && t.getMessage() != null) {
                            t.printStackTrace();
                        }
                        Log.e("DrivingFragment", "FAILURE DELETING DOCUMENT");
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection!!", Toast.LENGTH_LONG).show();
        }
    }
}