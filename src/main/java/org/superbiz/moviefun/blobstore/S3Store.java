package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


@Primary
public class S3Store implements BlobStore{

    static final Logger LOGGER = LoggerFactory.getLogger(S3Store.class.getName());

    private AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;

    }

    @Override
    public void put(Blob blob) throws IOException {
        s3Client.putObject(photoStorageBucket,blob.name , blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        LOGGER.info("Reading file: {} from S3Store", name);

        if (s3Client.doesObjectExist(photoStorageBucket, name)){
            S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
            LOGGER.info("Reading file: {} from S3Store success", name);
            Blob blob = new Blob(name, s3Object.getObjectContent(),"image/png" );
            LOGGER.info("Blob is generated of size {}", blob.inputStream.available());


            return Optional.of(blob);
        }else{
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {

    }
}
