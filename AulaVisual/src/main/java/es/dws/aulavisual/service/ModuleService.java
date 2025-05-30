package es.dws.aulavisual.service;

import java.io.ByteArrayInputStream;
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

        // Already protected route and sample data service causing trouble, I'm not touching this // me neither
        Course course = courseService.findById(courseDTO.id());
        Blob blob = null;
        if(content != null) {
            blob = transformImage(content);
        }
        Module module = new Module(course, name, position, blob);
        moduleRepository.save(module);
        module.setContentLocation("/api/course/" + module.getCourse().getId() + "/module/" + module.getId() + "/content/");
        return moduleMapper.toSimpleDTO(moduleRepository.save(module));
    }

    private Blob transformImage(MultipartFile image) {
        try {
            byte[] imageBytes = image.getBytes(); // Read the InputStream into a byte array
            return BlobProxy.generateProxy(new ByteArrayInputStream(imageBytes), imageBytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    public ModuleSimpleDTO findById(long id) {

        User loggedUser = userService.getLoggedUser();
        Module module = moduleRepository.findById(id).orElseThrow();
        Course courseOfModule = module.getCourse();
        if(!userService.hasRoleOrHigher("ADMIN") && !courseService.userIsInCourse(loggedUser.getId(),courseOfModule.getId())) {
            throw new RuntimeException("No tienes permisos para ejecutar esta acción");
        }
        return moduleMapper.toSimpleDTO(module);
    }

    public List<ModuleSimpleDTO> getModulesByCourse(CourseDTO courseDTO) {

        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("USER") ) {
            throw new RuntimeException("Primera debes iniciar sesion");
        }
        if(!courseService.userIsInCourse(loggedUser.getId(), courseDTO.id()) && !userService.hasRoleOrHigher("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ver este curso");
        }
        Course course = courseService.findById(courseDTO.id());
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ResponseEntity<Object> viewModule(long id) {

        User loggedUser = userService.getLoggedUser();
        Module module = moduleRepository.findById(id).orElseThrow();
        Course course = module.getCourse();
        if(userService.hasRoleOrHigher("ADMIN") || courseService.userIsInCourse(loggedUser.getId(), course.getId()) ) {

            try {

                Blob content = module.getContent();
                if(content == null) {

                    ClassPathResource resource = new ClassPathResource("static/md/default.md");
                    byte[] imageBytes = resource.getInputStream().readAllBytes();
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown").body(imageBytes);
                }else {

                    Resource file = new InputStreamResource(content.getBinaryStream());
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown")
                            .contentLength(content.length()).body(file);
                }

            }catch (Exception e) {

                System.out.println("Error loading content: " + e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
        throw new RuntimeException("No tienes permisos para ver este contenido");
    }

    public void delete(ModuleSimpleDTO moduleDTO, long courseId) {

        if(!userService.hasRoleOrHigher("ADMIN")) {
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

    public ModuleSimpleDTO deleteById(long id) {

        ModuleSimpleDTO moduleDTO = findById(id);
        long courseId = moduleDTO.course().id();
        delete(moduleDTO, courseId);
        return moduleDTO;
    }

    public boolean positionExists(CourseDTO courseDto, int position) {

        if(!userService.hasRoleOrHigher("ADMIN")) {
            throw new RuntimeException("No tienes permisos para ejecutar esta acción");
        }
        Course course = courseService.findById(courseDto.id());
        return moduleRepository.existsByCourseAndPosition(course, position);
    }

    public List<Integer> getAvailablePositions(CourseDTO courseDTO) {

        if(!userService.hasRoleOrHigher("ADMIN")) {
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
        Course course = courseService.findById(id);
        if(!userService.hasRoleOrHigher("ADMIN") && !courseService.userIsInCourse(loggedUser.getId(), course.getId())) {
            throw new RuntimeException("No tienes permisos para ver este curso");
        }
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ModuleSimpleDTO saveDTO(Long courseId, ModuleSimpleDTO moduleSimpleDTO) {

        //Only used in the REST controller
        CourseDTO courseDTO = courseService.findByIdDTO(courseId);
        if(!getAvailablePositions(courseDTO).contains(moduleSimpleDTO.position())){

            throw new RuntimeException("Las posiciones disponibles son: " + getAvailablePositions(courseDTO));
        }
        Module module = moduleRepository.findById(save(courseService.findByIdDTO(courseId), moduleSimpleDTO.name(), moduleSimpleDTO.position(), null).id()).orElseThrow();
        module.setContentLocation(null);
       return moduleMapper.toSimpleDTO(moduleRepository.save(module));
    }

    public void uploadModuleContent(long id, String location, InputStream inputStream, long size) {

        //Only used in the REST controller
        Module module = moduleRepository.findById(id).orElseThrow();
        module.setContentLocation(location);
        module.setContent(BlobProxy.generateProxy(inputStream, size));
        moduleRepository.save(module);
    }

    public Integer getFirstModuleByCourse(long courseId) {


        return moduleRepository.findFirstModuleId(courseId);
    }
}