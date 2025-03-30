package es.dws.aulavisual.service;

import java.sql.Blob;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import es.dws.aulavisual.Mapper.ModuleMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.repository.ModuleRepository;
import es.dws.aulavisual.Mapper.CourseMapper;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;


@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;
    private final CourseService courseService;
    private final ModuleMapper moduleMapper;

    public ModuleService(ModuleRepository moduleRepository, CourseMapper courseMapper, UserService userService, CourseService courseService, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
        this.courseService = courseService;
        this.moduleMapper = moduleMapper;
    }

    public ModuleSimpleDTO save(CourseDTO courseDTO, String name, int position, MultipartFile content) {

        Course course = courseService.findById(courseDTO.id());
        Module module = new Module(course, name, position, transformImage(content));
        return moduleMapper.toSimpleDTO(moduleRepository.save(module));
    }

    private Blob transformImage(MultipartFile image) {

        try {
           return BlobProxy.generateProxy(image.getInputStream(), image.getSize());
        }catch (IOException e) {

            throw new RuntimeException("Error processing image", e);
        }
    }

    public ModuleSimpleDTO findById(long id) {

        return moduleMapper.toSimpleDTO(moduleRepository.findById(id).orElseThrow());
    }

    public List<ModuleSimpleDTO> getModulesByCourse(CourseDTO courseDTO) {

        Course course = courseService.findById(courseDTO.id());
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ResponseEntity<Object> viewModule(ModuleSimpleDTO moduleDTO) {

        try {

            Module module = moduleRepository.findById(moduleDTO.id()).orElseThrow();
            Blob content = module.getContent();
            Resource file = new InputStreamResource(content.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown")
                    .contentLength(content.length()).body(file);

        } catch (Exception e) {

            System.out.println("Error loading content: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    public void delete(ModuleSimpleDTO moduleDTO, long courseId) {

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

        Course course = courseService.findById(courseDto.id());
        return moduleRepository.existsByCourseAndPosition(course, position);
    }

    public ModuleSimpleDTO findFirstModule(CourseDTO courseDTO) {

        Course course = courseService.findById(courseDTO.id());
        return moduleMapper.toSimpleDTO(moduleRepository.findFirstModule(course.getId()).orElseThrow());
    }

    public List<Integer> getAvailablePositions(CourseDTO courseDTO) {

        Course course = courseService.findById(courseDTO.id());
        int maxPosition = moduleRepository.findlastModuleId(course.getId());
        List<Integer> positions = new ArrayList<>();
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

        Course course = courseService.findById(id);
        return moduleMapper.toSimpleDTOs(moduleRepository.findByCourse(course, Sort.by(Sort.Direction.ASC, "position")));
    }

    public ModuleSimpleDTO saveDTO(Long courseId, ModuleSimpleDTO moduleSimpleDTO) {


        return save(courseService.findByIdDTO(courseId), moduleSimpleDTO.name(), moduleSimpleDTO.position(), null);
    }
}