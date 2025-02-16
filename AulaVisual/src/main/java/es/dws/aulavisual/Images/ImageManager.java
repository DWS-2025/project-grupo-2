package es.dws.aulavisual.Images;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import es.dws.aulavisual.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageManager {

    public void saveImage(String folderName, long userId, MultipartFile image) {



        try {
            Path folder = Paths.USERIMGS.resolve(folderName);
            Files.createDirectories(folder); // Create the directory if it does not exist

            Path newImg = folder.resolve("image-" + userId + ".png");
            image.transferTo(newImg);
        } catch (Exception e) {

            System.out.println("Error saving image: " + e.getMessage());
        }
    }

    public ResponseEntity<Object> loadImage(String folderName, long userId) {

        try {
            Path folder = Paths.USERIMGS.resolve(folderName);
            Path imgPath = folder.resolve("image-" + userId + ".png");

            Resource img = new UrlResource(imgPath.toUri());

            if (!Files.exists(imgPath)) {

                folder = Paths.USERDEFAULTIMGFOLDER;
                imgPath = folder.resolve(Paths.USERDEFAULTIMGPATH);
                img = new UrlResource(imgPath.toUri());
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png").body(img);
        } catch (Exception e) {

            System.out.println("Error loading image: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
