package com.carpool.tagalong.retrofit;

import com.carpool.tagalong.constants.Constants;
import com.carpool.tagalong.models.ModelAcceptRideRequest;
import com.carpool.tagalong.models.ModelCancelOwnRideRequest;
import com.carpool.tagalong.models.ModelDocumentStatus;
import com.carpool.tagalong.models.ModelForgotPasswordRequest;
import com.carpool.tagalong.models.ModelForgotPasswordResponse;
import com.carpool.tagalong.models.ModelGetAllRidesResponse;
import com.carpool.tagalong.models.ModelGetCarBrandModelResponse;
import com.carpool.tagalong.models.ModelGetCarColorsListResponse;
import com.carpool.tagalong.models.ModelGetCarYearList;
import com.carpool.tagalong.models.ModelGetCurrentRideResponse;
import com.carpool.tagalong.models.ModelGetDriverProfileResponse;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelGetRideDetailsResponse;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelLogoutResponse;
import com.carpool.tagalong.models.ModelPaymentRequest;
import com.carpool.tagalong.models.ModelPickupRider;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelRequestRide;
import com.carpool.tagalong.models.ModelRidePostRequest;
import com.carpool.tagalong.models.ModelRidePostResponse;
import com.carpool.tagalong.models.ModelSearchRideRequest;
import com.carpool.tagalong.models.ModelSearchRideResponse;
import com.carpool.tagalong.models.ModelSignInRequest;
import com.carpool.tagalong.models.ModelSignInResponse;
import com.carpool.tagalong.models.ModelSignUpResponse;
import com.carpool.tagalong.models.ModelStartRideRequest;
import com.carpool.tagalong.models.ModelUpdatePasswordRequest;
import com.carpool.tagalong.models.ModelUpdatePasswordResponse;
import com.carpool.tagalong.models.ModelUpdateProfileRequest;
import com.carpool.tagalong.models.ModelUserProfile;
import com.carpool.tagalong.models.ModelVerifyOtp;
import com.carpool.tagalong.models.ModelVerifyOtpResponse;
import com.carpool.tagalong.models.ModelVerifySignUp;
import com.carpool.tagalong.models.ModelVerifySignUpResponse;
import com.carpool.tagalong.models.ModelViewAllRidesDriverRequest;
import com.carpool.tagalong.models.ModelViewAllRidesDriverResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sahilsharma on 25/5/18.
 */

public interface RestClientInterface {

    @Multipart
    @retrofit2.http.POST("user/user_register")
    Call<ModelSignUpResponse> registerUser(@Part(Constants.USERNAME) RequestBody userName, @Part(Constants.EMAIL) RequestBody email, @Part(Constants.MOBILENO) RequestBody mobileNo, @Part(Constants.ADDRESS) RequestBody address, @Part(Constants.PASSWORD) RequestBody password, @Part(Constants.REPASSWORD) RequestBody rePassword, @Part(Constants.DEVICE_TOKEN_STRING) RequestBody deviceToken, @Part(Constants.DEVICE_TYPE_STRING) RequestBody deviceType);

    @retrofit2.http.POST("user/signin")
    Call<ModelSignInResponse> signIn(@Body ModelSignInRequest getCategoryAlertDataRequest);

    @retrofit2.http.POST("user/verify_signup")
    Call<ModelVerifySignUpResponse> verifySignUp(@Header("x-auth") String value, @Body ModelVerifySignUp modelVerifySignUp);

    @retrofit2.http.DELETE("user/logout")
    Call<ModelLogoutResponse> logout(@Header("x-auth") String value);

    @retrofit2.http.POST("user/forgot_password")
    Call<ModelForgotPasswordResponse> forgotPassword(@Body ModelForgotPasswordRequest modelForgotPasswordRequest);

    @retrofit2.http.POST("user/verify_otp")
    Call<ModelVerifyOtpResponse> verifyOtp(@Body ModelVerifyOtp modelVerifyOtp);

    @retrofit2.http.POST("user/update_password")
    Call<ModelUpdatePasswordResponse> updatePasssword(@Body ModelUpdatePasswordRequest modelUpdatePasswordRequest);

    @retrofit2.http.GET("user/user_profile")
    Call<ModelUserProfile> getUserProfile(@Header("x-auth") String value);

    @retrofit2.http.POST("user/update_profile")
    Call<ModelDocumentStatus> updateProfile(@Header("x-auth") String value, @Body ModelUpdateProfileRequest modelUpdateProfileRequest);

    @retrofit2.http.DELETE("user/delete_document")
    Call<ModelDocumentStatus> deleteDocument(@Header("x-auth") String value, @Query("id") String doc_id);

    @retrofit2.http.GET("ride/check_doc")
    Call<ModelDocumentStatus> checkDocument(@Header("x-auth") String value);

    @Multipart
    @retrofit2.http.POST("user/upload_documents")
    Call<ModelDocumentStatus> uploadDocuments(@Header("x-auth") String authorization, @Part MultipartBody.Part file, @Part("type") RequestBody type);

    @retrofit2.http.POST("ride/search_ride")
    Call<ModelSearchRideResponse> searchRide(@Header("x-auth") String value, @Body ModelSearchRideRequest modelSearchRideRequest);

    @retrofit2.http.POST("ride/request_ride")
    Call<ModelDocumentStatus> requestRide(@Header("x-auth") String value, @Body ModelRequestRide modelRequestRide);

    @retrofit2.http.POST("ride/ridepost")
    Call<ModelRidePostResponse> ridePost(@Header("x-auth") String value, @Body ModelRidePostRequest modelRidePostRequest);

    @retrofit2.http.GET("ride/get_current_ride")
    Call<ModelGetCurrentRideResponse> getCurrentRide(@Header("x-auth") String value);

    @retrofit2.http.POST("ride/cancel_ride")
    Call<ModelDocumentStatus> cancelRide(@Header("x-auth") String value, @Body ModelCancelOwnRideRequest modelCancelOwnRideRequest);

    @retrofit2.http.POST("ride/paynow")
    Call<ModelDocumentStatus> payNow(@Header("x-auth") String value, @Body ModelPaymentRequest modelPaymentRequest);

    @retrofit2.http.POST("ride/accept_ride")
    Call<ModelDocumentStatus> acceptRejectRide(@Header("x-auth") String value, @Body ModelAcceptRideRequest modelAcceptRideRequest);

    @retrofit2.http.POST("ride/cancel_pickup")
    Call<ModelDocumentStatus> cancelPickup(@Header("x-auth") String value, @Body ModelCancelOwnRideRequest modelCancelPickup);

    @retrofit2.http.POST("ride/pickup_rider")
    Call<ModelDocumentStatus> pickupRider(@Header("x-auth") String value, @Body ModelPickupRider modelPickupRider);

    @retrofit2.http.POST("ride/drop_rider")
    Call<ModelDocumentStatus> dropRider(@Header("x-auth") String value, @Body ModelPickupRider modelPickupRider);

    @Multipart
    @retrofit2.http.POST("timeline/add_post")
    Call<ModelDocumentStatus> addPost(@Header("x-auth") String value, @Part("rideId") RequestBody rideId, @Part("type") RequestBody type, @Part MultipartBody.Part file);

    @retrofit2.http.POST("timeline/get_post")
    Call<ModelGetTimelineResponse> getPost(@Header("x-auth") String value, @Body ModelGetTimelineRequest modelGetTimelineRequest);

    @retrofit2.http.POST("ride/rate_ride")
    Call<ModelDocumentStatus> rateRide(@Header("x-auth") String value, @Body ModelRateRiderequest modelRateRiderequest);

    @retrofit2.http.POST("ride/start_ride")
    Call<ModelDocumentStatus> startRide(@Header("x-auth") String value, @Body ModelStartRideRequest modelStartRideRequest);

    @retrofit2.http.GET("ride/recent_rides")
    Call<ModelGetRecentRidesResponse> getRecentRides(@Header("x-auth") String value);

    @retrofit2.http.GET("admin/get_car_list")
    Call<ModelGetCarBrandModelResponse> getCarBrandModels();

    @retrofit2.http.GET("admin/get_year_list")
    Call<ModelGetCarYearList> getYears();

    @retrofit2.http.GET("admin/get_color_list")
    Call<ModelGetCarColorsListResponse> getColors();

    @retrofit2.http.GET("user/driver_profile/{userId}")
    Call<ModelGetDriverProfileResponse> getDriverProfile(@Header("x-auth") String value, @Path("userId") String userId);

    @retrofit2.http.GET("ride/upcoming_rides")
    Call<ModelGetAllRidesResponse> getAllRides(@Header("x-auth") String value);

    @retrofit2.http.POST("user/driver_all_rides")
    Call<ModelViewAllRidesDriverResponse> viewAllRidesOfDriver(@Header("x-auth") String value, @Body ModelViewAllRidesDriverRequest modelViewAllRidesDriverRequest);

    @retrofit2.http.POST("ride/ride_details")
    Call<ModelGetCurrentRideResponse> getRideDetails(@Header("x-auth") String value, @Body ModelGetRideDetailsRequest modelGetRideDetailsRequest);
}