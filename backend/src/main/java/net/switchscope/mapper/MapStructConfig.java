package net.switchscope.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// https://mapstruct.org/documentation/stable/reference/html/#configuration-options
@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        // Applies to @MappingTarget methods only (updateFromTo), never to toEntity.
        // Without it MapStruct assigns unconditionally, so a PUT that omits a field would null the
        // stored value - the exact data loss the DTO update path exists to prevent. With IGNORE an
        // absent field keeps its current value, which matches how the reference ids are handled in
        // the services and how PUT /api/components/{id} has always behaved.
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructConfig {
}
