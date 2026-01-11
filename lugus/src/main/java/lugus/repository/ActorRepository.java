package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Actor;



public interface ActorRepository extends JpaRepository<Actor, Integer> {

	List<Actor> findByPeliculaIdOrderByOrdenAsc(int id);

}
