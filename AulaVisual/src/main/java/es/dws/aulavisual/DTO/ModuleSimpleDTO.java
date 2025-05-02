package es.dws.aulavisual.DTO;

import es.dws.aulavisual.DTO.CourseSimpleDTO;

public record ModuleSimpleDTO(
        Long id,
        String name,
        int position,
        CourseSimpleDTO course,
        String contentLocation
) {

}
