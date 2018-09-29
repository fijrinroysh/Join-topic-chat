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
import com.example.app.ourapplication.rest.model.response.SubscriberDataRespModel;
import com.example.app.ourapplication.rest.model.request.SubscribeReqModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    @POST("/feedquery/home")
    Call<SuccessRespModel> queryHomeFeed(@Body HomeFeedReqModel homeFeedReqModel);

    @POST("/postfeed")
    Call<ComposeRespModel> ComposeFeed(@Body CompleteFeedModel completeFeedModel);

    @POST("/feedquery/profile")
    Call<SuccessRespModel> queryProfileFeed(@Body ProfileFeedReqModel profileFeedReqModel);

    @POST("/feedquery/comment")
    Call<SuccessRespModel> queryCommentFeed(@Body CommentFeedReqModel commentFeedReqModel);

    @POST("/subscriber/subscribe")
    Call<SuccessRespModel> SubscribeFeed(@Body SubscribeReqModel subscribereqmodel);

    @POST("/subscriber/unsubscribe")
    Call<SuccessRespModel> UnSubscribeFeed(@Body SubscribeReqModel subscribereqmodel);

    @POST("/subscriber/dataquery")
    Call<SubscriberDataRespModel> querySubscriberData();
}