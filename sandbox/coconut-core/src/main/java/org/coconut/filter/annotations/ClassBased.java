package coconut.filter.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@FilterAnnotation()
public @interface ClassBased {
    Class value();
}
