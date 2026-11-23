
package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import lugus.model.films.Film;
import lugus.model.films.FilmUser;
import lugus.model.user.User;

public interface FilmUserRepository {
    List<FilmUser> findByUser(final User user);

    List<FilmUser> findByFilm(final Film film);

    Optional<FilmUser> findByUserAndFilm(final User user, final Film film);

    void delete(FilmUser entity);

    FilmUser save(FilmUser entity);
}
