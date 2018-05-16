package com.fast.lib.okhttp.request;

import android.text.TextUtils;


import com.fast.lib.okhttp.callback.ResultCallback;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpPostRequest extends OkHttpRequest
{
    private String content;
    private String json;
    private byte[] bytes;
    private File file;
    private MediaType mediaType;

    private int type = 0;
    private static final int TYPE_PARAMS = 1;
    private static final int TYPE_STRING = 2;
    private static final int TYPE_BYTES = 3;
    private static final int TYPE_FILE = 4;
    private static final int TYPE_JSON = 5;

    private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
    private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");


    protected OkHttpPostRequest(String url, String tag, Map<String, String> params, Map<String, String> headers, MediaType mediaType, String content,String json, byte[] bytes, File file)
    {
        super(url, tag, params, headers);
        this.mediaType = mediaType;
        this.content = content;
        this.json = json;
        this.bytes = bytes;
        this.file = file;
    }

    protected void validParams()
    {
        int count = 0;
        if (params != null && !params.isEmpty())
        {
            type = TYPE_PARAMS;
            count++;
        }
        if (content != null)
        {
            type = TYPE_STRING;
            count++;
        }
        if (bytes != null)
        {
            type = TYPE_BYTES;
            count++;
        }
        if (file != null)
        {
            type = TYPE_FILE;
            count++;
        }

        if(json != null)
        {
            type = TYPE_JSON;
            count++;
        }

        if (count <= 0 || count > 1)
        {
            throw new IllegalArgumentException("the params , content , file , bytes must has one and only one .");
        }
    }

    @Override
    protected Request buildRequest()
    {
        if (TextUtils.isEmpty(url))
        {
            throw new IllegalArgumentException("url can not be empty!");
        }
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, headers);
        builder.url(url).tag(tag).post(requestBody);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        validParams();
        RequestBody requestBody = null;


        switch (type)
        {
            case TYPE_PARAMS:
                FormBody.Builder formBody = new FormBody.Builder();
                addParams(formBody, params);
                requestBody = formBody.build();
                break;
            case TYPE_BYTES:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, bytes);
                break;
            case TYPE_FILE:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, file);
                break;
            case TYPE_STRING:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STRING, content);
                break;
            case TYPE_JSON:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_JSON, json);
                break;
        }
        return requestBody;
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final ResultCallback callback)
    {
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener()
        {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength)
            {

                mOkHttpClientManager.getDelivery().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.inProgress(bytesWritten * 1.0f / contentLength);
                    }
                });

            }
        });
        return countingRequestBody;
    }

    private void addParams(FormBody.Builder builder, Map<String, String> params)
    {
        if (builder == null)
        {
            throw new IllegalArgumentException("builder can not be null .");
        }

        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.add(key, params.get(key));
            }
        }
    }
}
