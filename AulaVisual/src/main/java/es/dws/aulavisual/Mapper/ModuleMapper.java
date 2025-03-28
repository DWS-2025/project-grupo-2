package es.dws.aulavisual.Mapper;

import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    ModuleSimpleDTO toSimpleDTO(Module module);
    Module toSimpleDomain(ModuleSimpleDTO moduleSimpleDTO);
    List <ModuleSimpleDTO> toSimpleDTOs(List<Module> modules);
}
