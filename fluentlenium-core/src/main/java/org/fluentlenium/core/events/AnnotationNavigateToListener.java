package org.fluentlenium.core.events;

import com.google.common.base.Function;
import org.fluentlenium.utils.ReflectionUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class AnnotationNavigateToListener extends AbstractAnnotationListener implements NavigateToListener {
    private final Method method;
    private final String annotationName;

    AnnotationNavigateToListener(final Method method, final Object container, final String annotationName, final int priority) {
        super(container, priority);
        this.method = method;
        this.annotationName = annotationName;
    }

    protected Function<Class<?>, Object> getArgsFunction(final String url, final WebDriver driver) {
        return new Function<Class<?>, Object>() {
            @Override
            public Object apply(final Class<?> input) {
                if (input.isAssignableFrom(String.class)) {
                    return url;
                }
                if (input.isAssignableFrom(WebDriver.class)) {
                    return driver;
                }
                return null;
            }
        };
    }

    @Override
    public void on(final String url, final WebDriver driver) {
        final Class<?>[] parameterTypes = method.getParameterTypes();

        final Object[] args = ReflectionUtils.toArgs(getArgsFunction(url, driver), parameterTypes);

        try {
            ReflectionUtils.invoke(method, getContainer(), args);
        } catch (final IllegalAccessException e) {
            throw new EventAnnotationsException("An error has occured in " + annotationName + " " + method, e);
        } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new EventAnnotationsException("An error has occured in " + annotationName + " " + method, e);
        }
    }
}
