package es.dws.aulavisual.DTO;

import es.dws.aulavisual.DTO.CourseSimpleDTO;

public record ModuleSimpleDTO(
        Long id,
        String name,
        int position,
        String description,
        CourseSimpleDTO course,
        String contentLocation
) {

}
