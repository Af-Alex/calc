package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.dto.User;
import com.alexaf.salarycalc.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User toDto(UserEntity userEntity);

}