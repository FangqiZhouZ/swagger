package tech.getarrays.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.getarrays.domain.AppUser;


public interface UserRepo extends JpaRepository<AppUser, Long> {

	AppUser findAppUsersByUsername(String username);

	AppUser findUserByEmail(String email);

}
