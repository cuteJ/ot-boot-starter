package com.onlythinking.starter.oss.client.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class BucketRequest {

    @NonNull
    private String bucketName;
    private String location;
    private AccessControl access = AccessControl.Default;
    // owner
    private String displayName;
    private String id;

}
