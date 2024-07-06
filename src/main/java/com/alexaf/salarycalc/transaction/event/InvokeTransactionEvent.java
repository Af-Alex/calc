package com.alexaf.salarycalc.transaction.event;

import com.alexaf.salarycalc.transaction.repository.TransactionEntity;

public record InvokeTransactionEvent(TransactionEntity transaction) {}
