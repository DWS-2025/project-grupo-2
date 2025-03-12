package es.dws.aulavisual.repository;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.model.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface SubmissionRepository extends CrudRepository <Submission, Long> {


    Optional<Submission> findByUserAndCourse(User user, Course course);

    List <Submission> findSubmissionByCourseAndGraded(Course course, boolean graded);
}
