package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private BlobStore blobStore;


    private final AlbumsBean albumsBean;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
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
        saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Path coverFilePath= null;
        Optional<Blob> image = blobStore.get(format("covers/%d", albumId));

        Blob imageBlob = image.orElse(null);

        byte[] imageBytes = null;

        if (imageBlob != null){
            coverFilePath = getCoverFile(albumId).toPath();
            imageBytes = IOUtils.toByteArray(imageBlob.inputStream);
        }else{
            coverFilePath = getDefaultCover();
            imageBytes = readAllBytes(coverFilePath);
        }


        //byte[]
        HttpHeaders headers = createImageHttpHeaders(imageBytes);

        return new HttpEntity<>(imageBytes, headers);
    }


    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {

        String contentType = new Tika().detect(uploadedFile.getInputStream());
        Blob blob = new Blob(targetFile.getPath(), uploadedFile.getInputStream(), contentType);

        blobStore.put(blob);


    }

    private HttpHeaders createImageHttpHeaders(byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(imageBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private Path getDefaultCover() throws URISyntaxException {
        return Paths.get(this.getClass().getClassLoader().getResource("default-cover.jpg").toURI());
    }


}
