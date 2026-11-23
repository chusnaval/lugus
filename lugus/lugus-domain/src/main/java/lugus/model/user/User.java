package lugus.model.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.converter.GenreConverter;
import lugus.model.films.FilmUser;
import lugus.model.values.Genre;

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(of = "login")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	
	@Id
	private String login;
	
	@Column
	private String password;

	@Column
	private String email;
	
	@Column
	private boolean admin;
	
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_favoritos",
        joinColumns = @JoinColumn(name = "login")
    )

    @Builder.Default
    @Column(name = "genero")
	@Convert(converter = GenreConverter.class)
    private List<Genre> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FilmUser> films = new HashSet<>();


}
