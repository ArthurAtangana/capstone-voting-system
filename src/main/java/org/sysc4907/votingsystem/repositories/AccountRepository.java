package org.sysc4907.votingsystem.repositories;

import org.springframework.data.repository.CrudRepository;
import org.sysc4907.votingsystem.models.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, String> {
    Optional<Account> findById(String userName);
}