package lugus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
	/**
     * Ruta del directorio NFS donde guardaremos los archivos.
     */
	@Value("${storage.nfs-root}")
    private String nfsRoot;
}
