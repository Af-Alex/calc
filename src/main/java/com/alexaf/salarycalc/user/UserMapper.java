package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.dto.User;
import com.alexaf.salarycalc.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {

    User toDto(UserEntity userEntity);

    UserEntity toEntity(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void partialUpdate(@MappingTarget User existing, User user);
}