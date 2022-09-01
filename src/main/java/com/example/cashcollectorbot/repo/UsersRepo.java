package com.example.cashcollectorbot.repo;

import com.example.cashcollectorbot.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends CrudRepository<User, Long> {

    Optional<User> findById(Long id);

}
