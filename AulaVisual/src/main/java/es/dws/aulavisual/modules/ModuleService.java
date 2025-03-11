package es.dws.aulavisual.modules;

import es.dws.aulavisual.courses.Course;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public void save(Course course, String name) {

        Module module = new Module(course, name);
        moduleRepository.save(module);
    }

    public Optional<Module> getModule(long id) {

        return moduleRepository.findById(id);
    }

    public List <Module> getAllModules() {

        return moduleRepository.findAll();
    }

    public void deleteModule(long id) {

        moduleRepository.deleteById(id);
    }

    public void editModuleName(long id, String newName) {

        Optional<Module> searchModule = moduleRepository.findById(id);
        if(searchModule.isEmpty()) {

            System.out.println("Module not found");
            return;
        }
        Module moduleToEdit = searchModule.get();
        moduleToEdit.setName(newName);
        moduleRepository.save(moduleToEdit);
    }

    public List<Module> getModulesByCourse(Course course) {

        return moduleRepository.findByCourse(course);
    }
}