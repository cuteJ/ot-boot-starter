package com.onlythinking.starter.oss.client.model;

import lombok.Data;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Data
public class UploadFileRs {

    private String eTag;
    private int partNumber;

    // aliyun
    private String bucketName;
    private String key;
    private String location;
}
