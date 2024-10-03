package org.sysc4907.votingsystem;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findById(long id);

}
