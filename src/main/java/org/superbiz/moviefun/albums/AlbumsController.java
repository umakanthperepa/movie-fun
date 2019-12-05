package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.blobstore.FileStore;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    final Logger LOGGER = LoggerFactory.getLogger(AlbumsController.class.getName());

    private final AlbumsBean albumsBean;

    public AlbumsController(AlbumsBean albumsBean) {
        this.albumsBean = albumsBean;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        Blob blob = new Blob(format("covers/%d.png", albumId), uploadedFile.getInputStream(), uploadedFile.getContentType());
        BlobStore blobStore = new FileStore();
        blobStore.put(blob);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        LOGGER.info("reading file for Album {}", albumId);

        BlobStore blobStore = new FileStore();
        String coverFileName = format("covers/%d.png", albumId);

        LOGGER.info("CoverFile Name  {}", coverFileName);

        Optional<Blob> blobFile = blobStore.get(coverFileName);

        Blob blob = blobFile.get();
        InputStream inputStream = blob.getInputStream();
        LOGGER.info("inputStream Length  {}", inputStream.available());
        byte[] imageBytes = IOUtils.toByteArray(inputStream);

        LOGGER.info("Read From BlobStore {} and ContentType {}", imageBytes.length, blob.getContentType());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/png"));
        headers.setContentLength(imageBytes.length);
        LOGGER.info("Header Content Type: {} and length {}", headers.getContentType(), headers.getContentLength());
        return new HttpEntity<>(imageBytes, headers);
    }

    private Blob getDefaultCoverFile() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("default-cover.jpg");
        String contentType = new Tika().detect(inputStream);
        return new Blob( "default-cover.jpg", inputStream, contentType);
    }

/*
    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }
*/

    /*private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(this.getClass().getClassLoader().getResource("default-cover.jpg").toURI());

//            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }*/
}
