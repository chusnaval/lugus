package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.Actor;
import lugus.repository.people.ActorRepository;

public interface JpaActorRepository extends ActorRepository, JpaRepository<Actor, Integer> {

}