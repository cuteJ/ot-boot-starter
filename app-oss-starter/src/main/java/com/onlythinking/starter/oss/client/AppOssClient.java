package com.onlythinking.starter.oss.client;

import com.onlythinking.starter.oss.client.model.BucketRequest;
import com.onlythinking.starter.oss.client.model.PutObjectRs;
import com.onlythinking.starter.oss.client.model.UploadFileRq;
import com.onlythinking.starter.oss.client.model.UploadFileRs;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * <p> 对象存储 </p>
 *
 * @author Li Xingping
 */
public interface AppOssClient<OSS, Bucket, OSSObject> {

    String REQUEST_TIMEOUT = "RequestTimeout";
    String CONNECTION_TIMEOUT = "ConnectionTimeout";
    String SOCKET_TIMEOUT = "SocketTimeout";
    String IDLE_TIMEOUT = "IdleTimeout";
    String MAX_CONNECTIONS = "maxConnections";
    String MAX_RETRY = "maxRetry";

    /**
     * <p>获取云服务oss</p>
     *
     * @return OSS
     */
    OSS getOssClient();

    /**
     * <p>关闭OssClient</p>
     */
    void shutdown();

    /**
     * Creates a {@link Bucket}
     *
     * @param request    创建Bucket请求
     */
    Bucket createBucket(BucketRequest request);

    /**
     * Creates a {@link Bucket}
     *
     * @param bucketName bucketName 名称
     */
    Bucket createBucket(String bucketName);


    /**
     * Deletes the {@link Bucket} instance.
     *
     * @param bucketName bucket name to delete.
     */
    void deleteBucket(String bucketName);

    /**
     * Returns all {@link Bucket} instances of the current account.
     *
     * @return A list of {@link Bucket} instances. If there's no buckets, the list will be empty (instead of null).
     */
    List<Bucket> listBuckets();

    /**
     * oss-cn-hongkong, oss-cn-shenzhen, oss-cn-shanghai, oss-us-west-1, oss-us-east-1, and oss-ap-southeast-1.
     *
     * @param bucketName Bucket name.
     * @return The datacenter name in string.
     */
    String getBucketLocation(String bucketName);

    /**
     * Checks the {@link Bucket} exists .
     *
     * @param bucketName Bucket name.
     * @return Returns true if the bucket exists and false if not.
     */
    boolean doesBucketExist(String bucketName);

    /**
     * Uploads the file to the {@link Bucket} from the {@link InputStream} instance.
     * It overwrites the existing one and the bucket must exist.
     *
     * @param bucketName Bucket name.
     * @param key        object key.
     * @param input      {@link InputStream} instance to write from. The must be readable.
     */
    PutObjectRs putObject(String bucketName, String key, InputStream input);

    /**
     * Uploads the file to the {@link Bucket} from the file.
     *
     * @param bucketName Bucket name.
     * @param key        Object key.
     * @param file       File object to read from.
     */
    PutObjectRs putObject(String bucketName, String key, File file);

    /**
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @return A {@link OSSObject} instance. The caller is responsible to close the connection after usage.
     */
    OSSObject getObject(String bucketName, String key);

    /**
     * @param bucketName Bucket name.
     * @param key        Object key.
     */
    void deleteObject(String bucketName, String key);

    /**
     * @param bucketName Bucket name.
     * @param key        Object Key.
     * @return True if exists; false if not.
     */
    boolean doesObjectExist(String bucketName, String key);

    /**
     * File upload
     */
    UploadFileRs uploadFile(UploadFileRq uploadFileRequest) throws Throwable;

}
