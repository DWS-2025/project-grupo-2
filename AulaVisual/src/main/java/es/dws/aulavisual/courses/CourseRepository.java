package es.dws.aulavisual.courses;

import es.dws.aulavisual.users.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> searchCoursesByStudentsContaining(User user);

    List<Course> searchCoursesByStudentsNotContaining(User user);
}