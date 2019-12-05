package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore{
    public  S3Store (AmazonS3Client amazonS3Client, String st){

    }

    @Override
    public void put(Blob blob) throws IOException {

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
