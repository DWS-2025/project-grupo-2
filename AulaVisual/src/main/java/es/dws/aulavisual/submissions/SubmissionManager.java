package es.dws.aulavisual.submissions;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@Service
public class SubmissionManager {

    public boolean submitCourseSubmission(long userId, long courseId, MultipartFile submission) {
        try {
            Path path = Paths.SUBMISSIONSFOLDER.resolve("course" + "-" + courseId);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path pathToFile = path.resolve("user" + "-" + userId + ".txt");
            if (Files.exists(pathToFile)) {
                return false;
            } else {
                Files.copy(submission.getInputStream(), pathToFile);
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing submission", e);
        }
    }

    public ResponseEntity<Object> getSubmission(long userId, long courseId) {
        try {
            Path path = Paths.SUBMISSIONSFOLDER.resolve("course" + "-" + courseId);
            Path pathToFile = path.resolve("user" + "-" + userId + ".txt");
            if (Files.exists(pathToFile)) {
                Resource resource = new UrlResource(pathToFile.toUri());
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/plaintext").body(resource);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving submission", e);
        }
    }
}