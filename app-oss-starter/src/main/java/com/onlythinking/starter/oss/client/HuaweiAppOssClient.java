package com.onlythinking.starter.oss.client;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;
import com.onlythinking.starter.oss.client.model.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * <p> 华为 Obs </p>
 *
 * @author Li Xingping
 */
public class HuaweiAppOssClient implements AppOssClient<ObsClient, S3Bucket, ObsObject> {

    private ObsClient delegate;

    public HuaweiAppOssClient(String endpoint, String accessKeyId,
                              String secretAccessKey,
                              Properties properties) {

        Assert.notNull(endpoint, "Endpoint must not be null");
        Assert.notNull(accessKeyId, "AccessKeyId must not be null");
        Assert.notNull(secretAccessKey, "SecretAccessKey must not be null");

        if (null != properties) {
            ObsConfiguration configuration = applyConfiguration(properties);
            configuration.setEndPoint(endpoint);
            delegate = new ObsClient(accessKeyId, secretAccessKey, configuration);
        } else {
            delegate = new ObsClient(accessKeyId, secretAccessKey, endpoint);
        }
    }

    private ObsConfiguration applyConfiguration(Properties properties) {
        ObsConfiguration configuration = new ObsConfiguration();
        if (StringUtils.hasLength(properties.getProperty(CONNECTION_TIMEOUT))) {
            configuration.setConnectionTimeout(Integer.parseInt(properties.getProperty(CONNECTION_TIMEOUT)));
        }
        if (StringUtils.hasLength(properties.getProperty(SOCKET_TIMEOUT))) {
            configuration.setSocketTimeout(Integer.parseInt(properties.getProperty(SOCKET_TIMEOUT)));
        }
        if (StringUtils.hasLength(properties.getProperty(IDLE_TIMEOUT))) {
            configuration.setIdleConnectionTime(Integer.parseInt(properties.getProperty(IDLE_TIMEOUT)));
        }
        if (StringUtils.hasLength(properties.getProperty(MAX_CONNECTIONS))) {
            configuration.setMaxConnections(Integer.parseInt(properties.getProperty(MAX_CONNECTIONS)));
        }
        if (StringUtils.hasLength(properties.getProperty(MAX_RETRY))) {
            configuration.setMaxErrorRetry(Integer.parseInt(properties.getProperty(MAX_RETRY)));
        }
        return configuration;
    }

    @Override
    public ObsClient getOssClient() {
        return this.delegate;
    }

    @Override
    public void shutdown() {
        try {
            this.delegate.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public S3Bucket createBucket(BucketRequest request) {
        ObsBucket obsBucket = new ObsBucket();
        obsBucket.setBucketName(request.getBucketName());
        obsBucket.setLocation(request.getLocation());
        obsBucket.setAcl(toAcl(request.getAccess()));

        if (StringUtils.hasText(request.getId()) && StringUtils.hasText(request.getDisplayName())) {
            Owner owner = new Owner();
            owner.setDisplayName(request.getDisplayName());
            owner.setId(request.getId());
            obsBucket.setOwner(owner);
        }
        return this.delegate.createBucket(obsBucket);
    }

    private AccessControlList toAcl(AccessControl accessControl) {
        switch (accessControl) {
            case Default:
                return AccessControlList.REST_CANNED_PUBLIC_READ;
            case Private:
                return AccessControlList.REST_CANNED_PRIVATE;
            case PublicRead:
                return AccessControlList.REST_CANNED_PUBLIC_READ;
            case PublicReadWrite:
                return AccessControlList.REST_CANNED_PUBLIC_READ_WRITE;
            default:
                return AccessControlList.REST_CANNED_PUBLIC_READ;
        }
    }

    @Override
    public S3Bucket createBucket(String bucketName) {
        return this.createBucket(new BucketRequest(bucketName));
    }

    @Override
    public void deleteBucket(String bucketName) {
        this.delegate.deleteBucket(bucketName);
    }

    @Override
    public List<S3Bucket> listBuckets() {
        return this.delegate.listBuckets();
    }

    @Override
    public String getBucketLocation(String bucketName) {
        return this.delegate.getBucketLocation(bucketName);
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        throw new UnsupportedOperationException("This method not supported at huawei.");
    }

    @Override
    public PutObjectRs putObject(String bucketName, String key, InputStream input) {
        PutObjectResult result = this.delegate.putObject(bucketName, key, input);
        return new PutObjectRs(result.getEtag(), bucketName, key, result.getRequestId());
    }

    @Override
    public PutObjectRs putObject(String bucketName, String key, File file) {
        PutObjectResult result = this.delegate.putObject(bucketName, key, file);
        return new PutObjectRs(result.getEtag(), bucketName, key, result.getRequestId());
    }

    @Override
    public ObsObject getObject(String bucketName, String key) {
        return this.delegate.getObject(bucketName, key);
    }

    @Override
    public void deleteObject(String bucketName, String key) {
        this.delegate.deleteObject(bucketName, key);
    }

    @Override
    public boolean doesObjectExist(String bucketName, String key) {
        throw new UnsupportedOperationException("This method not supported at huawei.");
    }

    @Override
    public UploadFileRs uploadFile(UploadFileRq uploadFileRq) throws Throwable {
        UploadFileRequest request = new UploadFileRequest(
          uploadFileRq.getBucketName(),
          uploadFileRq.getObjectKey(),
          uploadFileRq.getUploadFile(),
          uploadFileRq.getPartSize(),
          uploadFileRq.getTaskNum(),
          uploadFileRq.isEnableCheckpoint(),
          uploadFileRq.getCheckpointFile()
        );
        CompleteMultipartUploadResult result = this.delegate.uploadFile(request);
        UploadFileRs rs = new UploadFileRs();
        rs.setBucketName(result.getBucketName());
        rs.setKey(result.getObjectKey());
        rs.setETag(result.getEtag());
        return rs;
    }

}
