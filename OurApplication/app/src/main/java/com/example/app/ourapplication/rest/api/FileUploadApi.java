package com.example.app.ourapplication.rest.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadApi {
    @Multipart
    @POST("/file_upload")
   // Call<FileUploadRespModel> postImage(@Part("file") RequestBody file, @Part("description") RequestBody description);
    Call<ResponseBody> postImage(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("CompleteFeedModel") RequestBody completeFeedModel);
}
