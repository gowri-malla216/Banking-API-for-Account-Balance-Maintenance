package dev.codescreen.CodeScreen_rpatlddg.repository;

import dev.codescreen.CodeScreen_rpatlddg.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepos extends JpaRepository<Transaction, UUID> {
}
