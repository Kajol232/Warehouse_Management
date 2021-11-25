package com.muhammad.warehouse_management.repository;

import com.muhammad.warehouse_management.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUserName(String username);
    User findUserByEmail(String email);

}
