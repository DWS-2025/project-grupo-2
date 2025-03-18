package es.dws.aulavisual.Mapper;

import es.dws.aulavisual.DTO.TeacherInfoDTO;
import es.dws.aulavisual.DTO.UserDTO;
import org.mapstruct.Mapper;
import es.dws.aulavisual.DTO.UserSimpleDTO;
import es.dws.aulavisual.model.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    List <UserDTO> toDTOs(Collection <User> users);
    User toDomain(UserDTO userDTO);

    UserSimpleDTO toSimpleDTO(User user);
    List <UserSimpleDTO> toSimpleDTOs(Collection <User> users);
    User toDomain(UserSimpleDTO userSimpleDTO);

    TeacherInfoDTO toTeacherInfoDTO(User user);
    List <TeacherInfoDTO> toTeacherInfoDTOs(Collection <User> users);
    User toDomain(TeacherInfoDTO teacherInfoDTO);
}