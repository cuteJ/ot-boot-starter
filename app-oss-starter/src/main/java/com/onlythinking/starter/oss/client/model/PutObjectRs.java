package com.onlythinking.starter.oss.client.model;

import lombok.Data;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Data
public class PutObjectRs {

    // Object ETag
    private String eTag;
    private String bucketName;
    private String objectKey;

    // Aliyun The callback response body. Caller needs to close it.
    private String requestId;

    public PutObjectRs(String eTag, String bucketName, String objectKey, String requestId) {
        this.eTag = eTag;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.requestId = requestId;
    }

    public PutObjectRs() {
    }
}
