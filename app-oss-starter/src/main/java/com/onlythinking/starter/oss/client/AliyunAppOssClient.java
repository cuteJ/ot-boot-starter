package com.onlythinking.starter.oss.client;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.onlythinking.starter.oss.client.model.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * <p> 阿里云 Oss </p>
 *
 * @author Li Xingping
 */
public class AliyunAppOssClient implements AppOssClient<OSSClient, Bucket, OSSObject> {

    private OSSClient delegate;

    public AliyunAppOssClient(String endpoint, String accessKeyId,
                              String secretAccessKey,
                              Properties properties) {

        Assert.notNull(endpoint, "Endpoint must not be null");
        Assert.notNull(accessKeyId, "AccessKeyId must not be null");
        Assert.notNull(secretAccessKey, "SecretAccessKey must not be null");

        if (null != properties) {
            delegate = new OSSClient(endpoint, accessKeyId, secretAccessKey, applyConfiguration(properties));
        } else {
            delegate = new OSSClient(endpoint, accessKeyId, secretAccessKey);
        }
    }

    private ClientConfiguration applyConfiguration(Properties properties) {
        ClientConfiguration configuration = new ClientConfiguration();
        if (StringUtils.hasLength(properties.getProperty(REQUEST_TIMEOUT))) {
            configuration.setRequestTimeout(Integer.parseInt(properties.getProperty(REQUEST_TIMEOUT)));
        }
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
    public OSSClient getOssClient() {
        return this.delegate;
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public Bucket createBucket(BucketRequest request) {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(request.getBucketName());
        if (request.getAccess() != null) {
            createBucketRequest.withCannedACL(
              toAcl(request.getAccess())
            );
        }

        if (StringUtils.hasText(request.getLocation())) {
            createBucketRequest.setLocationConstraint(request.getLocation());
        }

        return this.delegate.createBucket(createBucketRequest);
    }

    private CannedAccessControlList toAcl(AccessControl accessControl) {
        return CannedAccessControlList.parse(accessControl.toString());
    }

    @Override
    public Bucket createBucket(String bucketName) {
        return this.delegate.createBucket(new CreateBucketRequest(bucketName)
          .withCannedACL(CannedAccessControlList.PublicRead));
    }

    @Override
    public void deleteBucket(String bucketName) {
        this.delegate.deleteBucket(bucketName);
    }

    @Override
    public List<Bucket> listBuckets() {
        return this.delegate.listBuckets();
    }

    @Override
    public String getBucketLocation(String bucketName) {
        return this.delegate.getBucketLocation(bucketName);
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        return this.delegate.doesBucketExist(bucketName);
    }

    @Override
    public PutObjectRs putObject(String bucketName, String key, InputStream input) {
        PutObjectResult result = this.delegate.putObject(bucketName, key, input);
        return new PutObjectRs(result.getETag(), bucketName, key, result.getRequestId());
    }

    @Override
    public PutObjectRs putObject(String bucketName, String key, File file) {
        PutObjectResult result = this.delegate.putObject(bucketName, key, file);
        return new PutObjectRs(result.getETag(), bucketName, key, result.getRequestId());
    }

    @Override
    public OSSObject getObject(String bucketName, String key) {
        return this.delegate.getObject(bucketName, key);
    }

    @Override
    public void deleteObject(String bucketName, String key) {
        this.delegate.deleteObject(bucketName, key);
    }

    @Override
    public boolean doesObjectExist(String bucketName, String key) {
        return this.delegate.doesObjectExist(bucketName, key);
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
        UploadFileResult result = this.delegate.uploadFile(request);
        UploadFileRs rs = new UploadFileRs();
        rs.setETag(result.getMultipartUploadResult().getETag());
        rs.setBucketName(result.getMultipartUploadResult().getBucketName());
        rs.setKey(result.getMultipartUploadResult().getKey());
        rs.setLocation(result.getMultipartUploadResult().getLocation());
        return rs;
    }

}
