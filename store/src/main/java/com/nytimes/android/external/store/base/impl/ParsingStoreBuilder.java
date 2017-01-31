package com.nytimes.android.external.store.base.impl;


import com.nytimes.android.external.cache.Cache;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.DiskWrite;
import com.nytimes.android.external.store.base.Fetcher;
import com.nytimes.android.external.store.base.Parser;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.Store;
import com.nytimes.android.external.store.util.NoopPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;

/**
 * Builder where there parser is used.
 */
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName"})
public class ParsingStoreBuilder<Raw, Parsed> {

    private final List<Parser> parsers = new ArrayList<>();
    private Fetcher<Raw> fetcher;
    private Persister<Raw> persister;
    private Cache<BarCode, Observable<Parsed>> memCache;

    public ParsingStoreBuilder() {

    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> fetcher(final @Nonnull Fetcher<Raw> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> nonObservableFetcher(final @Nonnull Func1<BarCode, Raw> fetcher) {
        this.fetcher = new Fetcher<Raw>() {
            @Nonnull
            @Override
            public Observable<Raw> fetch(final BarCode barCode) {
                return Observable.fromCallable(new Callable<Raw>() {
                    @Override
                    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                    public Raw call() throws Exception {
                        return fetcher.call(barCode);
                    }
                });
            }
        };
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> persister(final @Nonnull Persister<Raw> persister) {
        this.persister = persister;
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> persister(final @Nonnull DiskRead<Raw> diskRead,
                                                      final @Nonnull DiskWrite<Raw> diskWrite) {
        persister = new Persister<Raw>() {
            @Nonnull
            @Override
            public Observable<Raw> read(BarCode barCode) {
                return diskRead.read(barCode);
            }

            @Nonnull
            @Override
            public Observable<Boolean> write(BarCode barCode, Raw raw) {
                return diskWrite.write(barCode, raw);
            }
        };
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> parser(final @Nonnull Func1<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add((Parser<Raw, Parsed>) parser);
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> parser(final @Nonnull Parser<Raw, Parsed> parser) {
        this.parsers.clear();
        this.parsers.add(parser);
        return this;
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> parsers(final @Nonnull List<Parser> parsers) {
        this.parsers.clear();
        this.parsers.addAll(parsers);
        return this;
    }

    @Nonnull
    public static <Raw, Parsed> ParsingStoreBuilder<Raw, Parsed> builder() {
        return new ParsingStoreBuilder<>();
    }

    @Nonnull
    public ParsingStoreBuilder<Raw, Parsed> memory(Cache<BarCode, Observable<Parsed>> memCache) {
        this.memCache = memCache;
        return this;
    }

    @Nonnull
    public Store<Parsed> open() {
        if (persister == null) {
            persister = new NoopPersister<>();
        }

        Parser<Raw, Parsed> multiParser = new MultiParser<>(parsers);
        RealInternalStore<Raw, Parsed> realInternalStore;

        if (memCache == null) {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser);
        } else {
            realInternalStore = new RealInternalStore<>(fetcher, persister, multiParser, memCache);
        }

        return new RealStore<>(realInternalStore);
    }
}
