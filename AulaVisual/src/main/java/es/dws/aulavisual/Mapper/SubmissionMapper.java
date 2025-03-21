package es.dws.aulavisual.Mapper;

import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.model.Submission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    Submission toDomain(SubmissionDTO submissionDTO);
    SubmissionDTO toDTO(Submission submission);
    List<SubmissionDTO> toDTOs(List<Submission> submissions);
}
