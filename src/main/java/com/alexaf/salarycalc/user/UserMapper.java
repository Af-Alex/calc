package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.repository.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"goals", "salaries"})
    UserDto toDto(User user);

    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "salaries", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created"})
    User toEntity(UserDto userDto);
}
