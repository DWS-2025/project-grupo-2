package es.dws.aulavisual.DTO;

public record SubmissionDTO(

        Long id,
        UserSimpleDTO student,
        CourseSimpleDTO course,
        boolean graded,
        float grade,
        String content
) {
}
