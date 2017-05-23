package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.cache3.Cache;
import com.nytimes.android.external.cache3.CacheBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Single;

final class CacheFactory {
    private CacheFactory() {

    }

    static <Key, Parsed> Cache<Key, Maybe<Parsed>> createCache(MemoryPolicy memoryPolicy) {
        if (memoryPolicy == null) {
            return CacheBuilder
                    .newBuilder()
                    .maximumSize(StoreDefaults.getCacheSize())
                    .expireAfterWrite(StoreDefaults.getCacheTTL(), StoreDefaults.getCacheTTLTimeUnit())
                    .build();
        } else {
            if (memoryPolicy.getExpireAfterAccess() == memoryPolicy.DEFAULT_POLICY) {
                return CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterWrite(memoryPolicy.getExpireAfterWrite(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            } else {
                return CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterAccess(memoryPolicy.getExpireAfterAccess(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            }
        }
    }

    static <Key, Parsed> Cache<Key, Single<Parsed>> createInflighter(MemoryPolicy memoryPolicy) {
        long expireAfterToSeconds = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit()
                .toSeconds(StoreDefaults.getCacheTTL())
                : memoryPolicy.getExpireAfterTimeUnit().toSeconds(memoryPolicy.getExpireAfterWrite());
        long maximumInFlightRequestsDuration = TimeUnit.MINUTES.toSeconds(1);

        if (expireAfterToSeconds > maximumInFlightRequestsDuration) {
            return CacheBuilder
                    .newBuilder()
                    .expireAfterWrite(maximumInFlightRequestsDuration, TimeUnit.SECONDS)
                    .build();
        } else {
            long expireAfter = memoryPolicy == null ? StoreDefaults.getCacheTTL() :
                    memoryPolicy.getExpireAfterWrite();
            TimeUnit expireAfterUnit = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit() :
                    memoryPolicy.getExpireAfterTimeUnit();
            return CacheBuilder.newBuilder()
                    .expireAfterWrite(expireAfter, expireAfterUnit)
                    .build();
        }
    }
}
