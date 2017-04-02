package com.example.app.ourapplication.rest.api;

import com.example.app.ourapplication.rest.model.request.CommentFeedReqModel;
import com.example.app.ourapplication.rest.model.request.HomeFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;
import com.example.app.ourapplication.rest.model.response.CompleteFeedModel;
import com.example.app.ourapplication.rest.model.response.ComposeRespModel;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.rest.model.response.SignInRespModel;
import com.example.app.ourapplication.rest.model.request.SignUpReqModel;
import com.example.app.ourapplication.rest.model.response.SuccessRespModel;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by sarumugam on 15/01/17.
 */
public interface RestApi {

    @POST("/signin")
    Call<SignInRespModel> signIn(@Body SignInReqModel signInReqModel);

    @POST("/signup")
    Call<Void> signUp(@Body SignUpReqModel signUpReqModel);

    @POST("/updateprofile")
    Call<ProfileRespModel> updateProfile(@Body ProfileUpdateModel profileReqModel);

    @POST("/homefeedquery")
    Call<SuccessRespModel> queryHomeFeed(@Body HomeFeedReqModel homeFeedReqModel);

    @POST("/postfeed")
    Call<ComposeRespModel> ComposeFeed(@Body CompleteFeedModel completeFeedModel);

    @POST("/profilefeedquery")
    Call<SuccessRespModel> queryProfileFeed(@Body ProfileFeedReqModel profileFeedReqModel);

    @POST("/commentfeedquery")
    Call<SuccessRespModel> queryCommentFeed(@Body CommentFeedReqModel commentFeedReqModel);
}