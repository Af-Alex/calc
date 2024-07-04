package com.alexaf.salarycalc.account;

import com.alexaf.salarycalc.account.dto.Account;
import com.alexaf.salarycalc.account.repository.AccountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    Account toDto(AccountEntity accountEntity);

    @Mapping(target = "owner", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"ownerId", "id", "created"})
    AccountEntity createFromDto(Account dto);

}
