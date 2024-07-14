package com.alexaf.salarycalc.contribution;

import com.alexaf.salarycalc.contribution.dto.ContributionDto;
import com.alexaf.salarycalc.contribution.repository.Contribution;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContributionMapper {

    @Mapping(target = "goal", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "goalId"})
    Contribution createFromDto(ContributionDto dto);

    @Mapping(target = "goalId", source = "goal.id")
    ContributionDto toDto(Contribution entity);

}
