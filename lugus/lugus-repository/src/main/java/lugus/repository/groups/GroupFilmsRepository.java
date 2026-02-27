package lugus.repository.groups;

import java.util.List;
import java.util.Optional;

import lugus.model.groups.GroupFilms;

public interface GroupFilmsRepository {

	List<GroupFilms> findAll();

	Optional<GroupFilms> findById(Integer id);

	GroupFilms save(GroupFilms groupFilms);

	void deleteById(Integer id);

	List<GroupFilms> findAllByGroupIdOrderByOrden(Integer id);

	List<GroupFilms> findAllByPeliculaIdOrderByGroupName(Integer id);

}
