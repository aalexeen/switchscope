package net.switchscope.web.page;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Registers {@link ListQueryArgumentResolver}. A {@code WebMvcConfigurer} adds to Spring Boot's
 * MVC configuration rather than replacing it - that is {@code @EnableWebMvc} - so everything else
 * about request handling stays as it was.
 */
@Configuration
@RequiredArgsConstructor
public class ListQueryWebConfig implements WebMvcConfigurer {

    private final ListQueryArgumentResolver listQueryArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(listQueryArgumentResolver);
    }
}
