package tech.getarrays.service;

import org.springframework.web.multipart.MultipartFile;
import tech.getarrays.domain.AppUser;
import tech.getarrays.exception.domain.EmailExistException;
import tech.getarrays.exception.domain.EmailNotFoundException;
import tech.getarrays.exception.domain.UserNotFoundException;
import tech.getarrays.exception.domain.UsernameExistException;

import java.io.IOException;
import java.util.List;

public interface UserService {

	AppUser register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException, UserNotFoundException;

	AppUser addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonlocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UsernameExistException, UserNotFoundException, IOException;

	AppUser updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonlocked, boolean isActive) throws EmailExistException, UsernameExistException, UserNotFoundException, IOException;

	List<AppUser> getUsers();

	AppUser findUserByUsername(String username);
	
	AppUser findUserByEmail(String email);
	
	void deleteUser(String username) throws IOException;

	void resetPassword(String email) throws EmailNotFoundException;

}
