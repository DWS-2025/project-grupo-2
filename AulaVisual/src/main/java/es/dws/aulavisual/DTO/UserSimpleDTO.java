package es.dws.aulavisual.DTO;

import java.util.List;

public record UserSimpleDTO(

        Long id,
        String realName,
        String surname,
        String userName,
        String role,
        String campus,
        List<CourseSimpleDTO> coursesTeaching

) {


}
