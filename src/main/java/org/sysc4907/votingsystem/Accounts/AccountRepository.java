package org.sysc4907.votingsystem.Accounts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.sysc4907.votingsystem.Accounts.Account;

import java.util.Optional;
@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    Optional<Account> findById(String userName);
}