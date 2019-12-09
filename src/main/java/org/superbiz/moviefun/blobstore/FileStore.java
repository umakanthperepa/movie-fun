package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

@Component
public class FileStore implements BlobStore {

    static final Logger LOGGER = LoggerFactory.getLogger(FileStore.class.getName());

    @Override
    public void put(Blob blob) throws IOException {
        // ...

        LOGGER.info("Storing File with NAme {}", blob.name);

        File targetFile = new File(blob.name);


        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();



        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.inputStream, outputStream);
        }
        LOGGER.info("File Store Complete {}", blob.name);

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // ...
        LOGGER.info("Reading File with NAme {}", name);
        File file = new File(name);

        if(file.exists()){
            InputStream is = new FileInputStream(file);

            String contentType = new Tika().detect(name);
            Blob blob = new Blob(name, is, contentType);

            LOGGER.info("Successfully Created Blob for file {}", name);


            return Optional.of(blob);

        }else{
            return  Optional.empty();
        }



    }

    @Override
    public void deleteAll() {
        // ...
    }
}