package com.example.app.ourapplication.rest.api;

import com.example.app.ourapplication.rest.model.request.CommentFeedReqModel;
import com.example.app.ourapplication.rest.model.request.HomeFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileFeedReqModel;
import com.example.app.ourapplication.rest.model.request.ProfileUpdateModel;
import com.example.app.ourapplication.rest.model.request.SignInReqModel;
import com.example.app.ourapplication.rest.model.response.FeedRespModel;
import com.example.app.ourapplication.rest.model.response.ProfileRespModel;
import com.example.app.ourapplication.rest.model.response.SignInRespModel;
import com.example.app.ourapplication.rest.model.request.SignUpReqModel;

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
    Call<FeedRespModel> queryHomeFeed(@Body HomeFeedReqModel homeFeedReqModel);

    @POST("/profilefeedquery")
    Call<FeedRespModel> queryProfileFeed(@Body ProfileFeedReqModel profileFeedReqModel);

    @POST("/commentfeedquery")
    Call<FeedRespModel> queryCommentFeed(@Body CommentFeedReqModel commentFeedReqModel);
}