
package server.com.portfolio.wordhunt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration file for managing the Game Pigeon Word Hunter application.
 */
@Configuration
@EnableWebMvc
public class GamePigeonWordHunterConfiguration implements WebMvcConfigurer {

    /**
     * Whenever a resource wants to interact with the server, they will do so through
     * HTTP requests; however, the client and server are often under different origins.
     * CORS (Cross-origin Resource Sharing) enables you to access a resource from a
     * different origin. It is used to override your browser's default behavior due to
     * SOP (Same-Origin policy). So now when your client requests a resource, the response
     * will additionally contain a stamp that tells your browser to allow resource sharing
     * across different origins.
     * The client doesn't have anything to do with CORS; it's only something that your browser
     * imposes, and it suggests that your requested resource should be configured differently.
     * Therefore, it makes sense to configure the response from the server in such a way that
     * the browser identifies this as a CORS request. Hence, logically, CORS should always be
     * handled from the server side.
     * @param registry the mappings of paths where we are allowing connections through CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE");
    }
}
