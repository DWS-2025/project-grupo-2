package es.dws.aulavisual.Mapper;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.CourseInfoDTO;
import es.dws.aulavisual.DTO.CourseSimpleDTO;
import es.dws.aulavisual.model.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseSimpleDTO toSimpleDTO(Course course);
    Course toSimpleDomain(CourseSimpleDTO courseSimpleDTO);
    List<CourseSimpleDTO> toSimpleDTOs(List<Course> courses);

    CourseInfoDTO toInfoDTO(CourseInfoDTO courseInfoDTO);
    Course toInfoDomain(CourseInfoDTO courseInfoDTO);
    List<CourseInfoDTO> toInfoDTOs(List<Course> courses);

    Course toDomain(CourseDTO courseSimpleDTO);
    CourseDTO toDTO(Course course);
    List<CourseDTO> toDTOs(List<Course> courses);
}
