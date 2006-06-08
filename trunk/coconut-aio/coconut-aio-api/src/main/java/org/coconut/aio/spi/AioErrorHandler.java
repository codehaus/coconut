package org.coconut.aio.spi;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface AioErrorHandler<E> {
    boolean handleError(E element, Throwable t);
}
