package com.onlythinking.starter.oss.client.model;

import lombok.Data;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Data
public class UploadFileRq {
    private String bucketName;
    private String objectKey;
    private long partSize = 1024 * 100;
    private int taskNum = 1;
    private String uploadFile;
    private boolean enableCheckpoint = false;
    private String checkpointFile;
    private boolean enableCheckSum;
}
