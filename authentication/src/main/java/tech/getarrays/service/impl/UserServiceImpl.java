package tech.getarrays.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.getarrays.domain.AppUser;
import tech.getarrays.domain.UserPrincipal;
import tech.getarrays.enumeration.Role;
import tech.getarrays.exception.domain.EmailExistException;
import tech.getarrays.exception.domain.EmailNotFoundException;
import tech.getarrays.exception.domain.UserNotFoundException;
import tech.getarrays.exception.domain.UsernameExistException;
import tech.getarrays.repository.UserRepo;
import tech.getarrays.service.LoginAttemptService;
import tech.getarrays.service.UserService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static tech.getarrays.constant.UserImplConstant.*;
import static tech.getarrays.enumeration.Role.ROLE_ADMIN;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findAppUsersByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLogInDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(USER_FROM_USER_DETAILS_SERVICE + username);
            return userPrincipal;
        }

    }

    @Override
    public AppUser register(String firstName, String lastName, String username, String email, String password)
            throws EmailExistException, UsernameExistException, UserNotFoundException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        AppUser user = new AppUser();
        //String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setAuthorities(ROLE_ADMIN.getAuthorities());
        user.setRole(ROLE_ADMIN.name());
        userRepo.save(user);
        LOGGER.info(password);
        return user;
    }

    @Override
    public AppUser addNewUser(String firstName, String lastName, String username, String email, String role,
                              boolean isNonlocked, boolean isActive, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, UserNotFoundException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        AppUser user = new AppUser();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNonlocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(user);
        LOGGER.info(password);
        return user;
    }

    @Override
    public AppUser updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                              String newEmail, String role, boolean isNonlocked, boolean isActive)
            throws EmailExistException, UsernameExistException, UserNotFoundException {
        AppUser currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonlocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(currentUser);
        return currentUser;
    }

    @Override
    public List<AppUser> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public AppUser findUserByUsername(String username) {
        return userRepo.findAppUsersByUsername(username);
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public void deleteUser(String username) throws IOException {
        AppUser user = userRepo.findAppUsersByUsername(username);
        userRepo.deleteById(user.getUserId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        AppUser user = userRepo.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        LOGGER.info(password);
        userRepo.save(user);
    }

    private void validateLoginAttempt(AppUser user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private AppUser validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameExistException, EmailExistException{
        AppUser userByNewUsername = findUserByUsername(newUsername);
        AppUser userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            AppUser currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(USER_NOT_FOUND + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getUserId().equals(userByNewUsername.getUserId())) {
                throw new UsernameExistException(USERNAME_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getUserId().equals(userByNewEmail.getUserId())) {
                throw new EmailExistException(EMAIL_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_EXISTS);
            }
            return null;
        }
    }

}