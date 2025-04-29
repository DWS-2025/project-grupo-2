package es.dws.aulavisual.service;

import java.io.InputStream;
import java.sql.Blob;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import es.dws.aulavisual.Mapper.ModuleMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.model.User;
import es.dws.aulavisual.repository.ModuleRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;


@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseService courseService;
    private final ModuleMapper moduleMapper;
    private final UserService userService;

    public ModuleService(ModuleRepository moduleRepository, CourseService courseService, ModuleMapper moduleMapper, UserService userService) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
        this.moduleMapper = moduleMapper;
        this.userService = userService;
    }

    public ModuleSimpleDTO save(CourseDTO courseDTO, String name, int position, MultipartFile content) {

        // Already protected and sample data service, I'm not touching this
        Course course = courseService.findById(courseDTO.id());
        Blob blob = null;
        if(content != null) {
            blob = transformImage(content);
        }
        Module module = new Module(course, name, position, blob);
        return moduleMapper.toSimpleDTO(moduleRepository.save(module));
    }

    private Blob transformImage(MultipartFile image) {

        //Only used in the previous method
        try {
           return BlobProxy.generateProxy(image.getInputStream(), image.getSize());
        }catch (IOException e) {

            throw new RuntimeException("Error processing image", e);
        }
    }

    public ModuleSimpleDTO findById(long id) {

        User loggedUser = userService.getLoggedUser();
        if(!loggedUser.getRole().contains("USER")) {
            throw new RuntimeException("Primera debes iniciar sesion");
        }
        return moduleMapper.toSimpleDTO(moduleRepository.findById(id).orElseThrow());
    }

    public List<ModuleSimpleDTO> getModulesByCourse(CourseDTO courseDTO) {

        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("USER") ) {
            throw new RuntimeException("Primera debes iniciar sesion");
        }
        if(!courseService.userIsInCourse(loggedUser.getId(), courseDTO) && !userService.hasRoleOrHigher("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ver este curso");
        }
        Course course = courseService.findById(courseDTO.id());
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ResponseEntity<Object> viewModule(long id) {

        User loggedUser = userService.getLoggedUser();
        if(!loggedUser.getRole().contains("USER")) {
            throw new RuntimeException("Primera debes iniciar sesion");
        }
        try {

            Module module = moduleRepository.findById(id).orElseThrow();
            Blob content = module.getContent();
            if(content == null) {

                ClassPathResource resource = new ClassPathResource("static/md/default.md");
                byte [] imageBytes = resource.getInputStream().readAllBytes();
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown").body(imageBytes);
            }else{

                Resource file = new InputStreamResource(content.getBinaryStream());
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown")
                        .contentLength(content.length()).body(file);
            }

        } catch (Exception e) {

            System.out.println("Error loading content: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    public void delete(ModuleSimpleDTO moduleDTO, long courseId) {

        User admin = userService.getLoggedUser();
        if(!admin.getRole().contains("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ejecutar esta acción");
        }
        Module module = moduleRepository.findById(moduleDTO.id()).orElseThrow();
        Course course = module.getCourse();
        if (courseId != course.getId()){

            throw new NoSuchElementException("No such Course");
        }
        course.getmodules().remove(module);
        courseService.save(course);
        moduleRepository.deleteById(module.getId());
    }

    public boolean positionExists(CourseDTO courseDto, int position) {

        User admin = userService.getLoggedUser();
        if(!admin.getRole().contains("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ejecutar esta acción");
        }
        Course course = courseService.findById(courseDto.id());
        return moduleRepository.existsByCourseAndPosition(course, position);
    }

    public List<Integer> getAvailablePositions(CourseDTO courseDTO) {

        User admin = userService.getLoggedUser();
        if(!admin.getRole().contains("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ejecutar esta acción");
        }
        Course course = courseService.findById(courseDTO.id());
        Integer maxPosition = moduleRepository.findLastModuleId(course.getId());
        List<Integer> positions = new ArrayList<>();
        if(maxPosition == null) {
            positions.add(1);
            return positions;
        }
        List<Module> modules = moduleRepository.findByCourse(course);
        boolean found = false;

        for (int i = 1; i <= maxPosition; i++) {

            for (Module module : modules) {
                if (module.getPosition() == i) {
                    found = true;
                    break; // Exit the loop early if found
                }
            }

            if (!found) {
                positions.add(i);
            } else {
                found = false;
            }
        }
        positions.add(maxPosition+1);
        return positions;
    }

    public List<ModuleSimpleDTO> getModulesByCourseId(Long id){

        User loggedUser = userService.getLoggedUser();
        if(!loggedUser.getRole().contains("USER")) {
            throw new RuntimeException("Primera debes iniciar sesion");
        }
        Course course = courseService.findById(id);
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ModuleSimpleDTO saveDTO(Long courseId, ModuleSimpleDTO moduleSimpleDTO) {

        //Only used in the REST controller
        return save(courseService.findByIdDTO(courseId), moduleSimpleDTO.name(), moduleSimpleDTO.position(), null);
    }

    public void uploadModuleContent(long id, String location, InputStream inputStream, long size) {

        //Only used in the REST controller
        Module module = moduleRepository.findById(id).orElseThrow();
        module.setContentLocation(location);
        module.setContent(BlobProxy.generateProxy(inputStream, size));
        moduleRepository.save(module);
    }
}