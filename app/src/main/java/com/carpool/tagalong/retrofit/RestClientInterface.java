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
import com.carpool.tagalong.models.ModelGetEstimatedFareRequest;
import com.carpool.tagalong.models.ModelGetEstimatedFareResponse;
import com.carpool.tagalong.models.ModelGetNearbyDriversRequest;
import com.carpool.tagalong.models.ModelGetNearbyDriversResponse;
import com.carpool.tagalong.models.ModelGetRecentRidesResponse;
import com.carpool.tagalong.models.ModelGetRideDetailsRequest;
import com.carpool.tagalong.models.ModelGetTimelineRequest;
import com.carpool.tagalong.models.ModelGetTimelineResponse;
import com.carpool.tagalong.models.ModelGetUserLocationResponse;
import com.carpool.tagalong.models.ModelLogoutResponse;
import com.carpool.tagalong.models.ModelPickupRider;
import com.carpool.tagalong.models.ModelQuickRideBookResponse;
import com.carpool.tagalong.models.ModelRateRiderequest;
import com.carpool.tagalong.models.ModelRequestQuickRideRider;
import com.carpool.tagalong.models.ModelRequestRide;
import com.carpool.tagalong.models.ModelRidePostRequest;
import com.carpool.tagalong.models.ModelRidePostResponse;
import com.carpool.tagalong.models.ModelSearchRideRequest;
import com.carpool.tagalong.models.ModelSearchRideResponse;
import com.carpool.tagalong.models.ModelSignInRequest;
import com.carpool.tagalong.models.ModelSignInResponse;
import com.carpool.tagalong.models.ModelSignUpResponse;
import com.carpool.tagalong.models.ModelStartRideRequest;
import com.carpool.tagalong.models.ModelTagUsers;
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
import com.carpool.tagalong.models.chat.ChatSendMessageRequest;
import com.carpool.tagalong.models.chat.ModelChatSendMessageResponse;
import com.carpool.tagalong.models.chat.ModelGetChatConversationResponse;
import com.carpool.tagalong.models.chat.ModelGetChatRequest;
import com.carpool.tagalong.models.emergencysos.ModelGetEmergencyRidesResponse;
import com.carpool.tagalong.models.emergencysos.ModelSendEmergencySOSRequest;
import com.carpool.tagalong.models.emergencysos.ModelUpdateCoordinates;
import com.carpool.tagalong.models.wepay.ModelAddCardRequest;
import com.carpool.tagalong.models.wepay.ModelAddMerchantBankDetailsRequest;
import com.carpool.tagalong.models.wepay.ModelCard;
import com.carpool.tagalong.models.wepay.ModelGetKycStatusResposne;
import com.carpool.tagalong.models.wepay.ModelGetWePayIframeRequest;
import com.carpool.tagalong.models.wepay.ModelIframeResponse;
import com.carpool.tagalong.models.wepay.ModelRegisterMerchantWePayRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
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

//    @retrofit2.http.POST("ride/paynow")
//    Call<ModelDocumentStatus> payNow(@Header("x-auth") String value, @Body ModelPaymentRequest modelPaymentRequest);

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

    @retrofit2.http.GET("user/driver_profile")
    Call<ModelGetDriverProfileResponse> getDriverProfile(@Header("x-auth") String value, @Query("userId") String userId);

    @retrofit2.http.GET("ride/upcoming_rides")
    Call<ModelGetAllRidesResponse> getAllRides(@Header("x-auth") String value);

    @retrofit2.http.POST("user/driver_all_rides")
    Call<ModelViewAllRidesDriverResponse> viewAllRidesOfDriver(@Header("x-auth") String value, @Body ModelViewAllRidesDriverRequest modelViewAllRidesDriverRequest);

    @retrofit2.http.POST("ride/ride_details")
    Call<ModelGetCurrentRideResponse> getRideDetails(@Header("x-auth") String value, @Body ModelGetRideDetailsRequest modelGetRideDetailsRequest);

    @retrofit2.http.GET("user/get_emergency_rides")
    Call<ModelGetEmergencyRidesResponse> getEmergencyRides(@Header("x-auth") String value);

    @retrofit2.http.POST("user/press_panic_button")
    Call<ModelDocumentStatus> pressPanicButton(@Header("x-auth") String value, @Body ModelSendEmergencySOSRequest modelSendEmergencySOSRequest);

    @retrofit2.http.POST("user/update_coordinate")
    Call<ModelDocumentStatus> updateCoordinates(@Header("x-auth") String value, @Body ModelUpdateCoordinates modelUpdateCoordinates);

    @retrofit2.http.POST("ride/tag_users_in_ride")
    Call<ModelDocumentStatus> tagUsers(@Header("x-auth") String value, @Body ModelTagUsers modelTagUsers);

    @retrofit2.http.POST("chat/get_message")
    Call<ModelGetChatConversationResponse> getConversationMessages(@Header("x-auth") String value, @Body ModelGetChatRequest modelGetChatRequest);

    @retrofit2.http.POST("chat/send_message")
    Call<ModelChatSendMessageResponse> sendMessage(@Header("x-auth") String value, @Body ChatSendMessageRequest chatSendMessageRequest);

    @retrofit2.http.POST("ride/nearest_driver_quick_ride")
    Call<ModelGetNearbyDriversResponse> getNearestDrivers(@Header("x-auth") String value, @Body ModelGetNearbyDriversRequest modelGetNearbyDriversRequest);

// ------------------------------------------- WEPAY APIs ---------------------------------------------------------------------//

    @retrofit2.http.POST("user/add_card_wepay")
    Call<ModelDocumentStatus> addCard(@Header("x-auth") String value, @Body ModelAddCardRequest modelAddCardRequest);

    @retrofit2.http.POST("user/make_card_default_wepay")
    Call<ModelDocumentStatus> makeCardDefault(@Header("x-auth") String value, @Body ModelCard modelCard);

    @retrofit2.http.POST("user/remove_card_wepay")
    Call<ModelDocumentStatus> removeCard(@Header("x-auth") String value, @Body ModelCard modelCard);

    @retrofit2.http.POST("user/add_merchant_bank_details")
    Call<ModelDocumentStatus> addMechantBankDetails(@Header("x-auth") String value, @Body ModelAddMerchantBankDetailsRequest modelAddMerchantBankDetailsRequest);

    @retrofit2.http.POST("user/register_merchant_wepay")
    Call<ModelDocumentStatus> registerMerchantOnWePay(@Header("x-auth") String value, @Body ModelRegisterMerchantWePayRequest modelRegisterMerchantWePayRequest);

    @retrofit2.http.POST("account/get_update_uri")
    Call<ModelIframeResponse> getIframeUrl(@Header("Authorization") String value, @Body ModelGetWePayIframeRequest modelGetWePayIframeRequest);

    @retrofit2.http.POST("account/kyc")
    Call<ModelGetKycStatusResposne> isKycDone(@Header("Authorization") String value, @Body ModelGetWePayIframeRequest modelGetWePayIframeRequest);

    //--------------------------------------------------------------------------------------------------------------------------------------------/////

    @retrofit2.http.GET("user/enable_quick_ride")
    Call<ModelDocumentStatus> enableDisableQuickRide(@Header("x-auth") String value, @Query("isEnable") boolean isEnable);

    @retrofit2.http.POST("ride/get_ride_fare")
    Call<ModelGetEstimatedFareResponse> getEstimatedFare(@Header("x-auth") String value, @Body ModelGetEstimatedFareRequest modelGetNearbyDriversRequest);

    @retrofit2.http.POST("ride/book_quick_ride")
    Call<ModelQuickRideBookResponse> bookQuickRide(@Header("x-auth") String value, @Body ModelRequestQuickRideRider modelRequestQuickRideRider);

    @retrofit2.http.GET(" user/resend_confirmation_wepay")
    Call<ModelDocumentStatus> resendConfirmation(@Header("x-auth") String value);

    @retrofit2.http.GET("user/get_user_location")
    Call<ModelGetUserLocationResponse> getUserLocation(@Header("x-auth") String value, @Query("userId") String userId);
}