package es.dws.aulavisual.DTO;

import java.util.List;

public record CourseSimpleDTO (

        Long id,
        String name,
        String description,
        String task,
        TeacherInfoDTO teacher
){

}
