package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.dto.TgUser;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper
public interface TgUserMapper {
    TgUserEntity toEntity(TgUser tgUser);

    TgUser toDto(TgUserEntity tgUserEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TgUserEntity partialUpdate(TgUser tgUser, @MappingTarget TgUserEntity tgUserEntity);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "chatState", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "isBot", "languageCode", "canJoinGroups", "canReadAllGroupMessages", "supportInlineQueries",
            "isPremium", "addedToAttachmentMenu", "canConnectToBusiness"
    })
    TgUser toDto(User user);
}