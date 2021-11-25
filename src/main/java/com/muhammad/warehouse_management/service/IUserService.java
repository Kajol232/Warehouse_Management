package com.muhammad.warehouse_management.service;

import com.muhammad.warehouse_management.exception.domain.EmailExistException;
import com.muhammad.warehouse_management.exception.domain.EmailNotFoundException;
import com.muhammad.warehouse_management.exception.domain.UserNotFoundException;
import com.muhammad.warehouse_management.exception.domain.UsernameExistException;
import com.muhammad.warehouse_management.model.User;

import java.util.List;

public interface IUserService {
    User register(String firstName, String lastName, String username, String email, String  role, String password) throws UserNotFoundException, UsernameExistException, EmailExistException, UserNotFoundException, UsernameExistException, EmailExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);
    User updateUser(String currentUsername, String newFirstName, String newUsername, String newEmail, boolean isNotLocked,
                    boolean isActive, String Role) throws UserNotFoundException, UsernameExistException, EmailExistException;
    void deleteUser(long id);
    void resetPassword(String email, String password) throws EmailNotFoundException;

}
