package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.dto.GoalDto;
import com.alexaf.salarycalc.goal.repository.Goal;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GoalMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contributions", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "userId"})
    Goal createFromDto(GoalDto goalDto);

    @Mapping(source = "user.id", target = "userId")
    @BeanMapping(ignoreUnmappedSourceProperties = {"contributions"})
    GoalDto toDto(Goal goal);

}