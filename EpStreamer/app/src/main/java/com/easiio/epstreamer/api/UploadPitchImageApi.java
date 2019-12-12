package com.easiio.epstreamer.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UploadPitchImageApi extends PbxApi {

    private static final String TAG = "UploadPitchImageApi";

    @NonNull
    private static String getUrl() {
        return PbxApi.BASEURL + "rest/app/token/pitchImageLive/uploadPitchImage";
    }

    public static void call(@NonNull String uuid, @NonNull final String fileurl, @NonNull final String access_token) {
        //callRecordInfo.setFilebase64(fileToBase64(file));
        final Request request = getRequest(uuid, fileurl, access_token);

        ApiUtils.newClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtils.e(TAG, e.toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String body = response.body().string();
                LogUtils.d(TAG, "body=" + body);
                Response result = new Gson().fromJson(body, Response.class);
                LogUtils.d(TAG, "post callrecord success=" + new Gson().toJson(result));
            }
        });
    }

    public class Response {

        /**
         * status : ok
         * code : 1
         * message : upload call cdr successfully.
         */

        private String status;
        private String code;
        private String message;

        public boolean isSuccessful() {
            return TextUtils.equals(status, "ok");
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private static Request getRequest(@NonNull String uuid, @NonNull final String fileurl, String accessToken) {
        RequestBody requestBody = new FormBody.Builder()
                .add("uuid", uuid)
                .add("fileurl", fileurl)
                .add("storage_type", "oss")
                .build();

        return new Request.Builder()
                .url(getUrl())
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", getBearerAuth(accessToken))
                .post(requestBody)
                .build();
    }

}