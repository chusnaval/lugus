package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.values.Formato;

/**
 * Convierte entre {@link Formato} y su identificador numérico (short) para la base de datos.
 */
@Converter(autoApply = true)               // se aplica automáticamente a todos los campos Formato
public class FormatoConverter implements AttributeConverter<Formato, Short> {

    @Override
    public Short convertToDatabaseColumn(Formato attribute) {
        return attribute == null ? null : attribute.getId();   // guardamos el short
    }

    @Override
    public Formato convertToEntityAttribute(Short dbData) {
        if (dbData == null) {
            return null;
        }
        return Formato.getById(dbData);
    }
}