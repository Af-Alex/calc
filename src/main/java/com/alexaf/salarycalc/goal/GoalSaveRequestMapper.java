package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.goal.repository.GoalSaveRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GoalSaveRequestMapper {

    @Mapping(target = "userId", source = "user.id")
    GoalSaveRequestDto toDto(GoalSaveRequest goalSaveRequest);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "userId"})
    GoalSaveRequest createFromDto(GoalSaveRequestDto dto);

}