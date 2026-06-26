package lugus.dto.films;

import java.time.Instant;

import lombok.Data;
import lugus.dto.core.FormatDTO;

@Data
public class EditionDto {

	private int id;

	private int peliculaId;

	private FormatDTO format;

	private String location;

	private String mgmtCode;

	private String notes;

	private PackDto pack;

	private boolean steelbook;

	private boolean slipcover;

	private boolean owned;

	private Instant tsCompra;

	private ConditionDto condition;

}
