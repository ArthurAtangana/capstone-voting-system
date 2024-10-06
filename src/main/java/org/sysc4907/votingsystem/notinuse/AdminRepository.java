package org.sysc4907.votingsystem.notinuse;

import org.springframework.data.repository.CrudRepository;
import org.sysc4907.votingsystem.AdminAccount;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<AdminAccount, String> {

    Optional<AdminAccount> findById(String userName);

}
