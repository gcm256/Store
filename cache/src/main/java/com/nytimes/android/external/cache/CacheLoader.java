package com.nytimes.android.external.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;

public abstract class CacheLoader<K, V> {
  /**
   * Constructor for use by subclasses.
   */
  protected CacheLoader() {}

  /**
   * Computes or retrieves the value corresponding to {@code key}.
   *
   * @param key the non-null key whose value should be loaded
   * @return the value associated with {@code key}; <b>must not be null</b>
   * @throws Exception if unable to load the result
   * @throws InterruptedException if this method is interrupted. {@code InterruptedException} is
   *     treated like any other {@code Exception} in all respects except that, when it is caught,
   *     the thread's interrupt status is set
   */
  @Nullable
  public abstract V load(K key) throws Exception;


  @Nullable
  public ListenableFuture<V> reload(@NotNull K key, @NotNull V oldValue) throws Exception {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(oldValue);
    return Futures.immediateFuture(load(key));
  }


  @NotNull
  public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
    // This will be caught by getAll(), causing it to fall back to multiple calls to
    // LoadingCache.get
    throw new UnsupportedLoadingOperationException();
  }

  /**
   * Returns a cache loader based on an <i>existing</i> function instance. Note that there's no need
   * to create a <i>new</i> function just to pass it in here; just subclass {@code CacheLoader} and
   * implement {@link #load load} instead.
   *
   * @param function the function to be used for loading values; must never return {@code null}
   * @return a cache loader that loads values by passing each key to {@code function}
   */
  @NotNull
  public static <K, V> CacheLoader<K, V> from(@NotNull Function<K, V> function) {
    return new FunctionToCacheLoader<K, V>(function);
  }

  private static final class FunctionToCacheLoader<K, V>
      extends CacheLoader<K, V> implements Serializable {
    private final Function<K, V> computingFunction;

    public FunctionToCacheLoader(@NotNull Function<K, V> computingFunction) {
      this.computingFunction = Preconditions.checkNotNull(computingFunction);
    }

    @Nullable
    @Override
    public V load(@NotNull K key) {
      return computingFunction.apply(Preconditions.checkNotNull(key));
    }

    private static final long serialVersionUID = 0;
  }

  /**
   * Returns a cache loader based on an <i>existing</i> supplier instance. Note that there's no need
   * to create a <i>new</i> supplier just to pass it in here; just subclass {@code CacheLoader} and
   * implement {@link #load load} instead.
   *
   * @param supplier the supplier to be used for loading values; must never return {@code null}
   * @return a cache loader that loads values by calling {@link Supplier#get}, irrespective of the
   *     key
   */
  @NotNull
  public static <V> CacheLoader<Object, V> from(@NotNull Supplier<V> supplier) {
    return new SupplierToCacheLoader<V>(supplier);
  }


  private static final class SupplierToCacheLoader<V>
      extends CacheLoader<Object, V> implements Serializable {
    private final Supplier<V> computingSupplier;

    public SupplierToCacheLoader(@NotNull Supplier<V> computingSupplier) {
      this.computingSupplier = Preconditions.checkNotNull(computingSupplier);
    }

    @NotNull
    @Override
    public V load(@NotNull Object key) {
      Preconditions.checkNotNull(key);
      return computingSupplier.get();
    }

    private static final long serialVersionUID = 0;
  }

  /**
   * Exception thrown by {@code loadAll()} to indicate that it is not supported.
   *
   * @since 19.0
   */
  public static final class UnsupportedLoadingOperationException
      extends UnsupportedOperationException {
    // Package-private because this should only be thrown by loadAll() when it is not overridden.
    // Cache implementors may want to catch it but should not need to be able to throw it.
    UnsupportedLoadingOperationException() {}
  }

  /**
   * Thrown to indicate that an invalid response was returned from a call to {@link CacheLoader}.
   *
   * @since 11.0
   */
  public static final class InvalidCacheLoadException extends RuntimeException {
    public InvalidCacheLoadException(String message) {
      super(message);
    }
  }
}
