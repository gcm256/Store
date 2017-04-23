package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.Clearable;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.RecordProvider;
import com.nytimes.android.external.store.base.RecordState;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static com.nytimes.android.external.store.base.RecordState.STALE;

final class StoreUtil {
    private StoreUtil() {
    }

    @Nonnull
    static <Parsed, Key> Observable.Transformer<Parsed, Parsed>
    repeatWhenCacheEvicted(PublishSubject<Key> refreshSubject, @Nonnull final Key keyForRepeat) {
        Observable<Key> filter = refreshSubject.filter(new Func1<Key, Boolean>() {
            @Override
            public Boolean call(Key key) {
                return key.equals(keyForRepeat);
            }
        });
        return RepeatWhenEmits.from(filter);
    }

    static <Raw, Key> boolean shouldReturnNetworkBeforeStale(
            Persister<Raw, Key> persister, StalePolicy stalePolicy, Key key) {
        return stalePolicy == StalePolicy.NETWORK_BEFORE_STALE
                && persisterIsStale(key, persister);
    }

    static <Raw, Key> boolean persisterIsStale(@Nonnull Key key, Persister<Raw, Key> persister) {
        if (persister instanceof RecordProvider) {
            RecordProvider<Key> provider = (RecordProvider<Key>) persister;
            RecordState recordState = provider.getRecordState(key);
            return recordState == STALE;
        }
        return false;
    }

    static <Raw, Key> void clearPersister(Persister<Raw, Key> persister, @Nonnull Key key) {
        boolean isPersisterClearable = persister instanceof Clearable;

        if (isPersisterClearable) {
            ((Clearable<Key>) persister).clear(key);
        }
    }
}
