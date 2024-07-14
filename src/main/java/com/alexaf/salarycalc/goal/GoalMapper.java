package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.dto.GoalDto;
import com.alexaf.salarycalc.goal.repository.Goal;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface GoalMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contributions", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "userId"})
    Goal createFromDto(GoalDto goalDto);

    @Mapping(target = "userId", source = "user.id")
    @BeanMapping(ignoreUnmappedSourceProperties = {"contributions"})
    GoalDto toDto(Goal goal);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contributions", ignore = true)
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
            ignoreUnmappedSourceProperties = {"id", "created", "userId"}
    )
    void partialUpdate(@MappingTarget Goal entity, GoalDto goalDto);
}