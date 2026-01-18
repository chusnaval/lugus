package lugus.repository.groups;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.groups.GroupFilms;

public interface GroupFilmsRepository extends JpaRepository<GroupFilms, Integer> {

	List<GroupFilms> findAllByGroupIdOrderByOrden(Integer id);

	List<GroupFilms> findAllByPeliculaIdOrderByGroupName(Integer id);

}
