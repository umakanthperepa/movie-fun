package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;


public class FileStore implements BlobStore {

    final Logger LOGGER = LoggerFactory.getLogger(FileStore.class.getName());

    @Override
    public void put(Blob blob) throws IOException {
        LOGGER.info("Blob when storing: {}", blob.toString());
        saveUploadToFile(blob.inputStream, blob.name);

    }

    @Override
    public Optional<Blob> get(String name) {
        try {


            LOGGER.info("Name when reading: {}", name);

            File file = new File(name);

            LOGGER.info("File Exists {}. and its absolute path is {}, and fileSize {}", file.exists(), file.getAbsolutePath(), file.length());

            InputStream inputStream = new FileInputStream(name);
            LOGGER.info("inputStream Length  {}", inputStream.available());
            String contentType = new Tika().detect(inputStream);
            LOGGER.info("contentType: {}", contentType);
            Blob blob = new Blob(name, inputStream, "image/png");
            LOGGER.info("Blob when reading: {}", blob.toString());
            return Optional.of(blob);

        } catch (IOException e) {
            return null;
        }
    }




    @Override
    public void deleteAll() {
        // ...
    }

    private void saveUploadToFile(InputStream inputStream, String coverFileName) throws IOException {

        File targetFile = new File(coverFileName);
        LOGGER.info("File Name to be stored : {}", targetFile);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(inputStream, outputStream);
        }
        LOGGER.info("File {} stored successfully. ", targetFile.getAbsolutePath());

    }
}