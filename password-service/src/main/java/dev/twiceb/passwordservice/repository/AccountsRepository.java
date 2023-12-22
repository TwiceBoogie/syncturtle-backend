package dev.twiceb.passwordsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordsservice.model.Accounts;
import org.springframework.data.jpa.repository.Query;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {

}
