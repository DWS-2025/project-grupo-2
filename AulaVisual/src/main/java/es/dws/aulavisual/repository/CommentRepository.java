package es.dws.aulavisual.repository;

import es.dws.aulavisual.model.SubmissionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface CommentRepository extends JpaRepository <SubmissionComment, Long> {

}
