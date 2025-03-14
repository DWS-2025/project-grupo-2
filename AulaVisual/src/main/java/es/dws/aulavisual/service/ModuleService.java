package es.dws.aulavisual.service;

import java.sql.Blob;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.repository.ModuleRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public void save(Course course, String name, int position, MultipartFile content) {

        Module module = new Module(course, name, position, transformImage(content));
        moduleRepository.save(module);
    }

    private Blob transformImage(MultipartFile image) {

        try {
           return BlobProxy.generateProxy(image.getInputStream(), image.getSize());
        }catch (IOException e) {

            throw new RuntimeException("Error processing image", e);
        }
    }

    public Optional<Module> findById(long id) {

        return moduleRepository.findById(id);
    }

    public List <Module> getAllModules() {

        return moduleRepository.findAll();
    }

    public List<Module> getModulesByCourse(Course course) {

        return moduleRepository.findByCourse(course);
    }

    public ResponseEntity<Object> viewModule(Module module) {

        try {

            Blob content = module.getContent();
            Resource file = new InputStreamResource(content.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown")
                    .contentLength(content.length()).body(file);

        } catch (Exception e) {

            System.out.println("Error loading content: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    public void delete(Module module) {

        moduleRepository.delete(module);
    }

    public boolean positionExists(Course course, int position) {

        return moduleRepository.existsByCourseAndPosition(course, position);
    }
}