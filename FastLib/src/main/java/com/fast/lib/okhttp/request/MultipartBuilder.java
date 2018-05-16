package com.fast.lib.okhttp.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;

/**
 * Created by siberiawolf on 16/9/1.
 */
public final class MultipartBuilder
{
    public static final MediaType MIXED = MediaType.parse("multipart/mixed");

    public static final MediaType ALTERNATIVE = MediaType.parse("multipart/alternative");

    public static final MediaType DIGEST = MediaType.parse("multipart/digest");

    public static final MediaType PARALLEL = MediaType.parse("multipart/parallel");

    public static final MediaType FORM = MediaType.parse("multipart/form-data");

    private static final byte[] COLONSPACE = { 58, 32 };
    private static final byte[] CRLF = { 13, 10 };
    private static final byte[] DASHDASH = { 45, 45 };
    private final ByteString boundary;
    private MediaType type = MIXED;

    private final List<Headers> partHeaders = new ArrayList();
    private final List<RequestBody> partBodies = new ArrayList();

    public MultipartBuilder()
    {
        this(UUID.randomUUID().toString());
    }

    public MultipartBuilder(String boundary)
    {
        this.boundary = ByteString.encodeUtf8(boundary);
    }

    public MultipartBuilder type(MediaType type)
    {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        if (!type.type().equals("multipart")) {
            throw new IllegalArgumentException(new StringBuilder().append("multipart != ").append(type).toString());
        }
        this.type = type;
        return this;
    }

    public MultipartBuilder addPart(RequestBody body)
    {
        return addPart(null, body);
    }

    public MultipartBuilder addPart(Headers headers, RequestBody body)
    {
        if (body == null) {
            throw new NullPointerException("body == null");
        }
        if ((headers != null) && (headers.get("Content-Type") != null)) {
            throw new IllegalArgumentException("Unexpected header: Content-Type");
        }
        if ((headers != null) && (headers.get("Content-Length") != null)) {
            throw new IllegalArgumentException("Unexpected header: Content-Length");
        }

        this.partHeaders.add(headers);
        this.partBodies.add(body);
        return this;
    }

    private static StringBuilder appendQuotedString(StringBuilder target, String key)
    {
        target.append('"');
        int i = 0; for (int len = key.length(); i < len; i++) {
        char ch = key.charAt(i);
        switch (ch) {
            case '\n':
                target.append("%0A");
                break;
            case '\r':
                target.append("%0D");
                break;
            case '"':
                target.append("%22");
                break;
            default:
                target.append(ch);
        }
    }

        target.append('"');
        return target;
    }

    public MultipartBuilder addFormDataPart(String name, String value)
    {
        return addFormDataPart(name, null, RequestBody.create(null, value));
    }

    public MultipartBuilder addFormDataPart(String name, String filename, RequestBody value)
    {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        StringBuilder disposition = new StringBuilder("form-data; name=");
        appendQuotedString(disposition, name);

        if (filename != null) {
            disposition.append("; filename=");
            appendQuotedString(disposition, filename);
        }

        return addPart(Headers.of(new String[] { "Content-Disposition", disposition.toString() }), value);
    }

    public RequestBody build()
    {
        if (this.partHeaders.isEmpty()) {
            throw new IllegalStateException("Multipart body must have at least one part.");
        }
        return new MultipartRequestBody(this.type, this.boundary, this.partHeaders, this.partBodies);
    }

    private static final class MultipartRequestBody extends RequestBody
    {
        private final ByteString boundary;
        private final MediaType contentType;
        private final List<Headers> partHeaders;
        private final List<RequestBody> partBodies;
        private long contentLength = -1L;

        public MultipartRequestBody(MediaType type, ByteString boundary, List<Headers> partHeaders, List<RequestBody> partBodies)
        {
            if (type == null) throw new NullPointerException("type == null");

            this.boundary = boundary;
            this.contentType = MediaType.parse(type + "; boundary=" + boundary.utf8());
            this.partHeaders = Util.immutableList(partHeaders);
            this.partBodies = Util.immutableList(partBodies);
        }

        public MediaType contentType() {
            return this.contentType;
        }

        public long contentLength() throws IOException {
            long result = this.contentLength;
            if (result != -1L) return result;
            return this.contentLength = writeOrCountBytes(null, true);
        }

        private long writeOrCountBytes(BufferedSink sink, boolean countBytes)
                throws IOException
        {
            long byteCount = 0L;

            Buffer byteCountBuffer = null;
            if (countBytes) {
                sink = byteCountBuffer = new Buffer();
            }

            int p = 0; for (int partCount = this.partHeaders.size(); p < partCount; p++) {
            Headers headers = (Headers)this.partHeaders.get(p);
            RequestBody body = (RequestBody)this.partBodies.get(p);

            sink.write(MultipartBuilder.DASHDASH);
            sink.write(this.boundary);
            sink.write(MultipartBuilder.CRLF);

            if (headers != null) {
                int h = 0; for (int headerCount = headers.size(); h < headerCount; h++) {
                    sink.writeUtf8(headers.name(h))
                            .write(MultipartBuilder.COLONSPACE)
                            .writeUtf8(headers
                                    .value(h))
                            .write(MultipartBuilder.CRLF);
                }
            }

            MediaType contentType = body.contentType();
            if (contentType != null) {
                sink.writeUtf8("Content-Type: ")
                        .writeUtf8(contentType
                                .toString())
                        .write(MultipartBuilder.CRLF);
            }

            long contentLength = body.contentLength();
            if (contentLength != -1L) {
                sink.writeUtf8("Content-Length: ")
                        .writeDecimalLong(contentLength)
                        .write(MultipartBuilder.CRLF);
            } else if (countBytes)
            {
                byteCountBuffer.clear();
                return -1L;
            }

            sink.write(MultipartBuilder.CRLF);

            if (countBytes)
                byteCount += contentLength;
            else {
                ((RequestBody)this.partBodies.get(p)).writeTo(sink);
            }

            sink.write(MultipartBuilder.CRLF);
        }

            sink.write(MultipartBuilder.DASHDASH);
            sink.write(this.boundary);
            sink.write(MultipartBuilder.DASHDASH);
            sink.write(MultipartBuilder.CRLF);

            if (countBytes) {
                byteCount += byteCountBuffer.size();
                byteCountBuffer.clear();
            }

            return byteCount;
        }

        public void writeTo(BufferedSink sink) throws IOException {
            writeOrCountBytes(sink, false);
        }
    }
}
