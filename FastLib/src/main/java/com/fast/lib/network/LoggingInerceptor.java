package com.fast.lib.network;

import com.fast.lib.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by siberiawolf on 17/5/17.
 */

  public class LoggingInerceptor implements Interceptor {

    String TAG = "LoggingInerceptor=>";

    @Override
    public Response intercept(Chain chain) throws IOException {

        long t1 = System.nanoTime();
        Request request = chain.request();
        Response response = chain.proceed(request);

        try{
            long t2 = System.nanoTime();

            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();

            ResponseBody body = clone.body();
            if (body != null)
            {
                MediaType mediaType = body.contentType();
                if (mediaType != null)
                {
                    String resp = body.string();

                    Logger.i("response %s in %.1fms%n%s",request.url(),(t2 - t1) / 1e6d, resp);

                    body = ResponseBody.create(mediaType, resp);
                    return response.newBuilder().body(body).build();
                }
            }

        }catch (Throwable e){
            Logger.e(TAG,e.getMessage());
        }

        return response;
    }
}
