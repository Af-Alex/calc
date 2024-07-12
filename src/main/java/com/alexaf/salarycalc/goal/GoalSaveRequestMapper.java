package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.goal.repository.GoalSaveRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper
public interface GoalSaveRequestMapper {

    @Mapping(target = "userId", source = "user.id")
    GoalSaveRequestDto toDto(GoalSaveRequest entity);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "userId"})
    GoalSaveRequest createFromDto(GoalSaveRequestDto dto);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(
            nullValuePropertyMappingStrategy = IGNORE,
            ignoreUnmappedSourceProperties = {"id", "created", "user"}
    )
    GoalSaveRequest partialUpdate(@MappingTarget GoalSaveRequest target, GoalSaveRequest source);
}