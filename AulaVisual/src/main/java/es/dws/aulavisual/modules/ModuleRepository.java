package es.dws.aulavisual.modules;

import es.dws.aulavisual.courses.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModuleRepository extends JpaRepository <Module, Long> {

    List <Module> findByCourse(Course course);
}
