package net.switchscope.web.page;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Hands a list route what the whole query string asked for.
 * <p>
 * Without this, Spring would bind {@link ListQuery} as a model attribute - one field per known
 * parameter - and a filter would be unreachable: the fields a route can be filtered by are the
 * fields its row has, which is not something a class can declare. A parameter nobody expected would
 * be dropped silently, which is the failure the whole mechanism is written against.
 */
@Component
public class ListQueryArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ListQuery.class.equals(parameter.getParameterType());
    }

    @Override
    public ListQuery resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                     NativeWebRequest request, WebDataBinderFactory binderFactory) {
        return ListQuery.of(request.getParameterMap());
    }
}
