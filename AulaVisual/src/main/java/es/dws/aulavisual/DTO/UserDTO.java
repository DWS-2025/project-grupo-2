package es.dws.aulavisual.DTO;

import java.util.List;

public record UserDTO(

        Long id,
        String realName,
        String surname,
        String userName,
        Integer role,
        String campus,
        List<CourseSimpleDTO> courses,
        CourseInfoDTO courseTeaching
) {

}
