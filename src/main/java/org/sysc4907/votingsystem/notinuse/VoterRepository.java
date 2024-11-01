package org.sysc4907.votingsystem.notinuse;

import org.springframework.data.repository.CrudRepository;
import org.sysc4907.votingsystem.models.VoterAccount;

import java.util.Optional;


public interface VoterRepository extends CrudRepository<VoterAccount, String> {

    Optional<VoterAccount> findById(String userName);

}
