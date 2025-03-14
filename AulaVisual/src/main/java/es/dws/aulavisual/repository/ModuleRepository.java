package es.dws.aulavisual.repository;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModuleRepository extends JpaRepository <Module, Long> {

    List <Module> findByCourse(Course course);

    boolean existsByCourseAndPosition(Course course, int position);

    List<Module> findByCourse(Course course, Sort sort);
}
