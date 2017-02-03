package com.nytimes.android.external.store.base.beta;


import com.nytimes.android.external.store.base.impl.BarCode;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.annotations.Experimental;

/**
 * a {@link com.nytimes.android.external.store.base.impl.StoreBuilder StoreBuilder}
 * will return an instance of a store
 * <p>
 * A {@link Store  Store} can
 * {@link Store#get(BarCode) Store.get() } cached data or
 * force a call to {@link Store#fetch(BarCode) Store.fetch() }
 * (skipping cache)
 */
public interface Store<T, V> {

    /**
     * Return an Observable of T for request Barcode
     * Data will be returned from oldest non expired source
     * Sources are Memory Cache, Disk Cache, Inflight, Network Response
     */
    @Nonnull
    Observable<T> get(@Nonnull V key);

    /**
     * Calls store.get(), additionally will repeat anytime store.clear(barcode) is called
     * WARNING: getRefreshing(barcode) is an endless observable, be careful when combining
     * with operators that expect an OnComplete event
     */
    @Experimental
    Observable<T> getRefreshing(@Nonnull final V key);


    /**
     * Return an Observable of T for requested Barcode skipping Memory & Disk Cache
     */
    @Nonnull
    Observable<T> fetch(@Nonnull V key);

    /**
     * @return an Observable that emits new items when they arrive.
     */
    @Nonnull
    Observable<T> stream();

    /**
     * Similar to  {@link Store#get(BarCode) Store.get() }
     * Rather than returning a single response, Stream will stay subscribed for future emissions to the Store
     * NOTE: Stream will continue to get emissions for ANY keyAndRawType not just starting one
     *
     * @deprecated Use {@link Store#stream()}. If you need to start with the first value,
     * use {@code store.stream().startWith(store.get(keyAndRawType))}
     */
    @Deprecated
    @Nonnull
    Observable<T> stream(V id);

    /**
     * Clear the memory cache of all entries
     */
    void clearMemory();

    /**
     * Purge a particular entry from memory cache.
     */
    void clearMemory(@Nonnull V key);


}
