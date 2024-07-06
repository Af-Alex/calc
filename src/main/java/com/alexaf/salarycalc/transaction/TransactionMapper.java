package com.alexaf.salarycalc.transaction;

import com.alexaf.salarycalc.transaction.dto.Transaction;
import com.alexaf.salarycalc.transaction.repository.TransactionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TransactionMapper {

    @Mapping(target = "initiatorId", source = "initiator.id")
    @Mapping(target = "accrualAccountId", source = "accrualAccount.id")
    Transaction toDto(TransactionEntity entity);

    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "accrualAccount", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"id", "created", "initiatorId", "accrualAccountId"})
    TransactionEntity toEntity(Transaction dto);

}