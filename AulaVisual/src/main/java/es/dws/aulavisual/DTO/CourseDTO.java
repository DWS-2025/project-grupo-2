package es.dws.aulavisual.DTO;

import java.sql.Blob;
import java.util.List;

public record CourseDTO(

        Long id,
        String name,
        String description,
        String task,
        TeacherInfoDTO teacher,
        List<UserSimpleDTO> students
) {
}
