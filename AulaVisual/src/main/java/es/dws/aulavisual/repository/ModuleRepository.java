package es.dws.aulavisual.repository;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ModuleRepository extends JpaRepository <Module, Long> {

    List <Module> findByCourse(Course course);

    boolean existsByCourseAndPosition(Course course, int position);

    List<Module> findByCourse(Course course, Sort sort);

    @Query(value = "SELECT position FROM Module WHERE course_id = :course_id ORDER BY position DESC LIMIT 1;",
            nativeQuery = true)
    Integer findLastModuleId(long course_id);

    @Query(value = "SELECT position FROM Module WHERE course_id = :course_id ORDER BY position ASC LIMIT 1;",
            nativeQuery = true)
    Integer findFirstModuleId(long course_id);
}
