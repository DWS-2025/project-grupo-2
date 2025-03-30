package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.service.ModuleService;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
