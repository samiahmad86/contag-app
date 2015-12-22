package com.contag.app.model;

import com.contag.app.config.Constants;

import java.util.HashSet;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by tanay on 7/8/15.
 */
public interface APIInterface {
    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_LOGIN)
    Response login(@Body Login login);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_OTP)
    OTPResponse otp(@Header(Constants.Headers.HEADER_DEVICE_ID) String deviceId,
                    @Header(Constants.Headers.HEADER_PUSH_ID) String pushId,
                    @Header(Constants.Headers.HEADER_DEVICE_TYPE) String deviceType,
                    @Header(Constants.Headers.HEADER_APP_VERSION_ID) String appVersionId,
                    @Body OTP otp);

    @Headers({
            "Content-Type: application/json"
    })
    @DELETE(Constants.Urls.URL_LOGOUT)
    Response logout();

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_USER)
    NewUserResponse newUser(@Header(Constants.Headers.HEADER_DEVICE_ID) String deviceId,
                            @Header(Constants.Headers.HEADER_PUSH_ID) String pushId,
                            @Header(Constants.Headers.HEADER_DEVICE_TYPE) String deviceType,
                            @Header(Constants.Headers.HEADER_APP_VERSION_ID) String appVersionId,
                            @Header(Constants.Headers.HEADER_TOKEN) String token,
                            @Body NewUser newUser);


    @Headers({
            "Content-Type: application/json"
    })
    @PUT(Constants.Urls.URL_USER)
    User setUser(@Header(Constants.Headers.HEADER_TOKEN) String token, @Body UserRequestModel urm);


    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_USER)
    User getUser(@Header(Constants.Headers.HEADER_TOKEN) String token);


    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_USER)
    User getUserByID(@Header(Constants.Headers.HEADER_TOKEN) String token,
                         @Query(Constants.Keys.KEY_USER_ID) Long userID);




    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_CONTACT)
    ContactResponse.ContactList sendContacts(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                             @Body HashSet<RawContacts> contacts);
    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_CONTACT)
    ContactResponse.ContactList getContacts(@Header(Constants.Headers.HEADER_TOKEN) String token);


    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_CONTACT)
    ContactResponse.ContactList getUserByContagID(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                                  @Query(Constants.Keys.KEY_CONTAG_ID) String contagID);

    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_CONTACT)
    ContactResponse.ContactList getUserByUserID(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                                  @Query(Constants.Keys.KEY_NOTIF_USER_ID) long userID);

    @Headers({
            "Content-Type: application/json"
    })
    @PUT(Constants.Urls.URL_ADD_CONTACT)
    ContactResponse.ContactList addContagUser(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                              @Body AddContact userID);

    @Headers({
            "Content-Type: application/json"
    })
    @PUT(Constants.Urls.URL_ADD_CONTACT)
    ContactResponse.ContactList addContagUserFromNotification(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                              @Body NotificationAddContact nac );

    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_SOCIAL_PROFILE)
    SocialPlatformResponse.List getAllSocialPlatforms(@Header(Constants.Headers.HEADER_TOKEN) String token);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_SOCIAL_PROFILE)
    User addSocialPlatform(@Header(Constants.Headers.HEADER_TOKEN) String token, @Body SocialRequestModel.List
            mSocialRequestModelList);

    @Headers({
            "Content-Type: application/json"
    })
    @DELETE(Constants.Urls.URL_SOCIAL_PROFILE)
    Response deleteSocialPlatform(@Header(Constants.Headers.HEADER_TOKEN) String token, @Query("id") long id);


    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_USER_INTEREST)
    InterestSuggestion.List getInterestSuggestions(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                                   @Query(Constants.Keys.KEY_INTEREST_SUGGESTION_SLUG) String slug);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_USER_INTEREST)
    Response addUserInterests(@Header(Constants.Headers.HEADER_TOKEN) String token,
                              @Body InterestPost mInterestPost);



    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_PROFILE_REQUEST)
    MessageResponse makeProfileRequest(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                       @Body ProfileRequestModel mProfileRequestModel);

    @Headers({
            "Content-Type: application/json"
    })
    @PUT(Constants.Urls.URL_PROFILE_REQUEST)
    Response sendResponseToNotification(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                        @Body FieldRequestNotificationResponse mFieldRequestNotificationResponse);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_PROFILE_PRIVACY)
    MessageResponse setProfilePrivacy(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                       @Body ProfilePrivacyRequestModel mProfilePrivacyRequestModel);

    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_FEEDS)
    FeedsResponse.FeedList getFeeds(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                    @Query(Constants.Keys.KEY_FEEDS_START_INDEX) int startIndex,
                                    @Query(Constants.Keys.KEY_FEEDS_END_INDEX) int endIndex);

    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_NOTIFICATIONS)
    NotificationsResponse.NotificationList getNotifications(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                    @Query(Constants.Keys.KEY_FEEDS_START_INDEX) int startIndex,
                                    @Query(Constants.Keys.KEY_FEEDS_END_INDEX) int endIndex);

    @Headers({
            "Content-Type: application/json"
    })
    @DELETE(Constants.Urls.URL_NOTIFICATIONS)
    NotificationsResponse.NotificationList hideNotification(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                                            @Query(Constants.Keys.KEY_NOTIFICATION_ID) long notificationID);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Constants.Urls.URL_INTRODUCE)
    Response introduceContag(@Header(Constants.Headers.HEADER_TOKEN) String token,
                               @Body ContagIntroductionRequestModel introduction);


    @Headers({
            "Content-Type: application/json"
    })
    @GET(Constants.Urls.URL_LOG)
    MessageResponse logFuckingMessage(@Header(Constants.Headers.HEADER_TOKEN) String token,
                                      @Query("logged_at") long timestamp, @Query("message") String message);


    @Multipart
    @PUT(Constants.Urls.URL_IMAGE_UPLOAD)
    ImageUploadResponse uploadImage(@Header(Constants.Headers.HEADER_TOKEN) String token,
                         @Part(Constants.Keys.KEY_USER_AVATAR_URL)TypedFile mProfileImage);


}
