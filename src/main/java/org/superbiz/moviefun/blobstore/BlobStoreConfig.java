package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobStoreConfig {

    static final Logger LOGGER = LoggerFactory.getLogger(BlobStoreConfig.class.getName());

    @Bean
    ServiceCredentials serviceCredentials(@Value("${vcap.services}") String vcapServices) {
        return new ServiceCredentials(vcapServices);
    }

    @Bean
    public BlobStore blobStore(
            ServiceCredentials serviceCredentials,
            @Value("${vcap.services.photo-storage.credentials.endpoint:#{null}}") String endpoint
    ) {

        LOGGER.info(" initializing blobStore Bean");

        String photoStorageAccessKeyId = serviceCredentials.getCredential("photo-storage", "user-provided", "access_key_id");
        String photoStorageSecretKey = serviceCredentials.getCredential("photo-storage", "user-provided", "secret_access_key");
        String photoStorageBucket = serviceCredentials.getCredential("photo-storage", "user-provided", "bucket");

        LOGGER.info(" initializing blobStore Bean accessID: {}, storageKey: {}, bucketName: {}", photoStorageAccessKeyId,photoStorageSecretKey, photoStorageBucket );


        AWSCredentials credentials = new BasicAWSCredentials(photoStorageAccessKeyId, photoStorageSecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        LOGGER.info("Created S3 client");

        if (endpoint != null) {
            s3Client.setEndpoint(endpoint);
        }

        return new S3Store(s3Client, photoStorageBucket);
    }


}
