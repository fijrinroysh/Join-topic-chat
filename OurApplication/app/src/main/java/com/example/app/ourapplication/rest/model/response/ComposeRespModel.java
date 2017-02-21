package com.example.app.ourapplication.rest.model.response;

import com.example.app.ourapplication.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ROYSH on 2/21/2017.
 */
public class ComposeRespModel extends Model  {

        private boolean mIsSuccess;

        @JsonCreator
        public ComposeRespModel(@JsonProperty("success") Boolean success) {
            this.mIsSuccess = success;
        }

        public boolean isSuccess() {
            return mIsSuccess;
        }

        public void setIsSuccess(boolean isSuccess) {
            this.mIsSuccess = isSuccess;
        }


}
