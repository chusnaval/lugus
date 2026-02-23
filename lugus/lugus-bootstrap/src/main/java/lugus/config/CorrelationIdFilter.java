package lugus.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

	private static final String CORRELATION_ID_KEY = "correlationId";

	@Value("${lugus.observability.correlation-id.header:X-Correlation-Id}")
	private String correlationIdHeader;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String correlationId = request.getHeader(correlationIdHeader);
		if (correlationId == null || correlationId.isBlank()) {
			correlationId = UUID.randomUUID().toString();
		}

		MDC.put(CORRELATION_ID_KEY, correlationId);
		response.setHeader(correlationIdHeader, correlationId);
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(CORRELATION_ID_KEY);
		}
	}
}
