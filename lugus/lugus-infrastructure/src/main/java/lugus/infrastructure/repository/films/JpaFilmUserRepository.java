package lugus.infrastructure.repository.films;



import org.springframework.data.jpa.repository.JpaRepository;



public interface JpaFilmUserRepository
        extends lugus.repository.films.FilmUserRepository, JpaRepository<lugus.model.films.FilmUser, Long> {

}
