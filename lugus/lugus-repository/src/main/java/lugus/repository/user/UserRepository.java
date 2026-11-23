package lugus.repository.user;

import java.util.Optional;

import lugus.model.user.User;


public interface UserRepository {

	Optional<User> findById(final String id);

	User save(final User user);

	void deleteById(final String id);

}
