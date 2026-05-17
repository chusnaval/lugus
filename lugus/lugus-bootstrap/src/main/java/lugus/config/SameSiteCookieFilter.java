package lugus.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SameSiteCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

        // Reescribir Set-Cookie si es JSESSIONID
        for (String header : response.getHeaders("Set-Cookie")) {
            if (header.startsWith("JSESSIONID")) {
                String newHeader = header
                        + "; SameSite=None; Secure"; // Chrome exige ambos
                response.setHeader("Set-Cookie", newHeader);
            }
        }
    }
}
