package es.dws.aulavisual.DTO;

import es.dws.aulavisual.model.SubmissionComment;

import java.util.List;

public record SubmissionDTO(

        Long id,
        UserSimpleDTO student,
        CourseSimpleDTO course,
        boolean graded,
        float grade,
        String content,
        String submissionName,
        List<SubmissionComment> comments
) {
}
