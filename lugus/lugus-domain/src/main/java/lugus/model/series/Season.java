package lugus.model.series;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.converter.LangVersionConverter;
import lugus.model.orders.SeasonOrder;
import lugus.model.values.LangVersion;

@Entity
@Table(name = "seasons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Season {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "serie_id", nullable = true) // FK → serie.id
	private Series series;
	
	@Column(name = "\"desc\"")
	private String desc;
	
	@Column
	private int year;
	
	@Column(name = "\"order\"")
	private int order;
	
	@Column
	private boolean purchased;
	
	@Column
	private boolean wanted;

	@Column
	@Convert(converter = LangVersionConverter.class)
	private LangVersion publishedVersion;
	
	@Column
	@Convert(converter = LangVersionConverter.class)
	private LangVersion purchasedVersion;

	@JsonIgnore
	@OneToMany(mappedBy = "season", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<SeasonOrder> orders = new HashSet<>();
	
	private transient String situation;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Season other = (Season) obj;
		return Objects.equals(id, other.id) && order == other.order && year == other.year;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, order, year);
	}
	

	public void obtainSituation() {

		if (purchased) {
			this.situation = "Comprado";
		} else if (hasIncompleteOrders()) {
			this.situation = "En pedido";
		} else {
			this.situation = "No comprado";
		}
	}

	private boolean hasIncompleteOrders() {
		return orders.stream().anyMatch(o -> !o.isReceived());
	}

}
