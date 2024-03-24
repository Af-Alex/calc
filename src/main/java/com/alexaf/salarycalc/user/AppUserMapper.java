package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.dto.AppUser;
import com.alexaf.salarycalc.user.entity.AppUserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AppUserMapper {

    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = "id")
    AppUser toDto(AppUserEntity appUserEntity);

}