package coconut.core;

/**
 * An interface which is implemented by components that need to dispose of resources during the shutdown of that
 * component. The {@link Disposable#dispose()} must be called once during shutdown, directly after {@link
 * Startable#stop()} (if the component implements the {@link Startable} interface).
 * @version $Revision: 1.1 $
 * @see coconut.staged.Startable the Startable interface if you need to <code>start()</code> and
 *      <code>stop()</code> semantics.
 * @see coconut.staged.StagedContainer the main StagedContainer interface (and hence its subinterfaces and
 *      implementations like {@link coconut.staged.defaults.DefaultPipeline}) implement this interface.
 * @since 1.0
 */
public interface Disposable {
    /**
     * Dispose this component. The component should deallocate all resources. The contract for this method defines a
     * single call at the end of this component's life.
     */
    void dispose();
}