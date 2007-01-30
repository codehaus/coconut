package coconut.filter.annotation;

import java.lang.annotation.Annotation;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public @interface Not {
    Class<ClassBased> value();
}
