package dev.codescreen.CodeScreen_rpatlddg.repository;

import dev.codescreen.CodeScreen_rpatlddg.model.Amount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AmountRepos extends JpaRepository<Amount, UUID> {
}
