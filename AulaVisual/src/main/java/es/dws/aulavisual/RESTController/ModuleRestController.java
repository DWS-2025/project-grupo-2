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
    public ResponseEntity <Object> getModules(@PathVariable long courseId) {

        try{
            List<ModuleSimpleDTO> modules = moduleService.getModulesByCourseId(courseId);
            return ResponseEntity.ok(modules);
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("module/{id}/")
    public ResponseEntity <Object> getModule(@PathVariable long id) {

        try {
            ModuleSimpleDTO module = moduleService.findById(id);
            return ResponseEntity.ok(module);
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("module/{id}/content/")
    public ResponseEntity <Object> getModuleContent(@PathVariable long id) {

       try {
           return moduleService.viewModule(id);
       }catch (RuntimeException e){

           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @PutMapping("module/{id}/content/")
    public ResponseEntity <Object> updateModuleContent(@PathVariable long id, @RequestParam("content") MultipartFile content) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            moduleService.uploadModuleContent(id, location, content.getInputStream(), content.getSize());
            return ResponseEntity.created(URI.create(location)).body(location);
        }catch (RuntimeException | IOException e) {

            throw new RuntimeException("Error processing file", e);
        }
    }

    @PostMapping("module/")
    public ResponseEntity <Object> createModule(@PathVariable long courseId, @RequestBody ModuleSimpleDTO module) {

        try {
            ModuleSimpleDTO createdModule = moduleService.saveDTO(courseId, module);
            return ResponseEntity.ok(createdModule);
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("module/{id}/")
    public ResponseEntity <Object> deleteModule(@PathVariable long id) {

        try {

            return ResponseEntity.ok(moduleService.deleteById(id));
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
