package org.superbiz.moviefun.blobstore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public interface BlobStore {

    void put(Blob blob) throws IOException;

    Optional<Blob> get(String name) throws IOException;

    void deleteAll();


}