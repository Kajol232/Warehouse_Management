package com.muhammad.warehouse_management.service;

import com.muhammad.warehouse_management.exception.domain.EmailExistException;
import com.muhammad.warehouse_management.exception.domain.EmailNotFoundException;
import com.muhammad.warehouse_management.exception.domain.UserNotFoundException;
import com.muhammad.warehouse_management.exception.domain.UsernameExistException;
import com.muhammad.warehouse_management.model.CustomUserDetails;
import com.muhammad.warehouse_management.model.Role;
import com.muhammad.warehouse_management.model.User;
import com.muhammad.warehouse_management.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.muhammad.warehouse_management.config.constant.UserImplConstant.*;
import static com.muhammad.warehouse_management.model.Role.ROLE_ADMIN;
import static com.muhammad.warehouse_management.model.Role.ROLE_WORKER_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
public class UserServiceImpl implements IUserService, UserDetailsService {
    private UserRepository userRepository;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserName(username);
        if(user == null){
            LOGGER.error("User not found by username: " + username);
            throw new UsernameNotFoundException("User not found by username: " + username);
        }else{
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            LOGGER.info("Returning found user by username: " + username);
            return customUserDetails;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email, String  role, String password) throws
            UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);

        if(role.equalsIgnoreCase("Worker")){
           user.setRole(ROLE_WORKER_USER.name());
           user.setAuthorities(ROLE_WORKER_USER.getAuthorities());
        }
        if(role.equalsIgnoreCase("Admin")){
            user.setRole(ROLE_ADMIN.name());
            user.setAuthorities(ROLE_ADMIN.getAuthorities());
        }

        userRepository.save(user);
        LOGGER.info("New user password: " + password);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUserName(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newUsername, String newEmail, boolean isNotLocked,
                           boolean isActive, String role) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User user = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        user.setFirstName(newFirstName);
        user.setUserName(newUsername);
        user.setEmail(newEmail);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        Role r = getRoleEnumName(role);
        user.setRole(r.name());
        user.setAuthorities(r.getAuthorities());

        userRepository.save(user);
        return user;


    }



    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);

    }

    @Override
    public void resetPassword(String email, String password) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            throw new EmailNotFoundException("No user for Email: " + email);
        }
        user.setPassword(encodePassword(password));
        userRepository.save(user);

    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }


    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws
            UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }
}
