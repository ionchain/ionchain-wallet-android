package com.fast.lib.okhttp.request;

import android.util.Pair;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpUploadRequest extends OkHttpPostRequest
{
    private Pair<String, File>[] files;


    protected OkHttpUploadRequest(String url, String tag, Map<String, String> params, Map<String, String> headers, Pair<String, File>[] files)
    {
        super(url, tag, params, headers, null, null,null, null, null);
        this.files = files;
    }

    @Override
    protected void validParams()
    {
        if (params == null && files == null)
        {
            throw new IllegalArgumentException("params and files can't both null in upload request .");
        }
    }

    private void addParams(MultipartBuilder builder, Map<String, String> params)
    {
        if (builder == null)
        {
            throw new IllegalArgumentException("builder can not be null .");
        }

        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));

            }
        }
    }

    @Override
    public RequestBody buildRequestBody()
    {
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        addParams(builder, params);

        if (files != null)
        {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++)
            {
                if(files[i] != null){
                    Pair<String, File> filePair = files[i];
                    String fileKeyName = filePair.first;
                    File file = filePair.second;
                    String fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                    builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }


            }
        }

        return builder.build();
    }


    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

}
