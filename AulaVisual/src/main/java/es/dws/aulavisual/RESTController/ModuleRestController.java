package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.service.ModuleService;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/course/{courseId}")
public class ModuleRestController {

    private final ModuleService moduleService;

    public ModuleRestController(ModuleService moduleService) {

        this.moduleService = moduleService;
    }

    @GetMapping("modules/")
    public ResponseEntity <List <ModuleSimpleDTO>> getModules(@PathVariable long courseId) {

        List<ModuleSimpleDTO> modules = moduleService.getModulesByCourseId(courseId);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("module/{id}/")
    public ResponseEntity <ModuleSimpleDTO> getModule(@PathVariable long id) {

        ModuleSimpleDTO module = moduleService.findById(id);
        return ResponseEntity.ok(module);
    }

    @GetMapping("module/{id}/content/")
    public ResponseEntity <Object> getModuleContent(@PathVariable long id) {

       return moduleService.viewModule(id);
    }

    @PostMapping("module/{id}/content/")
    public ResponseEntity <Object> updateModuleContent(@PathVariable long id, @RequestParam("content") MultipartFile content) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            moduleService.uploadModuleContent(id, location, content.getInputStream(), content.getSize());
            return ResponseEntity.created(URI.create(location)).body(location);
        }catch (IOException e) {

            throw new RuntimeException("Error processing file", e);
        }
    }

    @PostMapping("module/")
    public ResponseEntity <ModuleSimpleDTO> createModule(@PathVariable long courseId, @RequestBody ModuleSimpleDTO module) {

        ModuleSimpleDTO createdModule = moduleService.saveDTO(courseId, module);
        return ResponseEntity.ok(createdModule);
    }

    @DeleteMapping("module/{id}/")
    public ResponseEntity <ModuleSimpleDTO> deleteModule(@PathVariable Long courseId, @PathVariable long id) {

        ModuleSimpleDTO module = moduleService.findById(id);
        moduleService.delete(module, courseId);
        return ResponseEntity.ok(module);
    }
}
