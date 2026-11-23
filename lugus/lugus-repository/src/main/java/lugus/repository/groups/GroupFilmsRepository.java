package lugus.repository.groups;

import java.util.List;
import java.util.Optional;

import lugus.model.groups.GroupFilms;

public interface GroupFilmsRepository {

	List<GroupFilms> findAll();

	Optional<GroupFilms> findById(final Integer id);

	GroupFilms save(final GroupFilms groupFilms);

	void deleteById(final Integer id);

	List<GroupFilms> findAllByGroupIdOrderByOrder(final Integer id);

	List<GroupFilms> findAllByFilmIdOrderByGroupName(final Integer id);

}
