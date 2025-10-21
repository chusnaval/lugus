package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.Genero;

/**
 * Convierte entre {@link Genero} y su código (String) para la base de datos.
 */
@Converter(autoApply = true)               // se aplica automáticamente a todos los campos Genero
public class GeneroConverter implements AttributeConverter<Genero, String> {

    @Override
    public String convertToDatabaseColumn(Genero attribute) {
        return attribute == null ? null : attribute.getCodigo();   // guardamos el código
    }

    @Override
    public Genero convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Buscamos el enum cuyo código coincida
        return Genero.getById(dbData);
    }
}