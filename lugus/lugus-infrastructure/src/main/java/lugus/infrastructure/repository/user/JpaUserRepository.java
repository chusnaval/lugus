package lugus.infrastructure.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.user.User;
import lugus.repository.user.UserRepository;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, String> {

}