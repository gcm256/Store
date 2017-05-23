package com.nytimes.android.external.store3.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.fs3.ObjectToSourceTransformer;

import javax.annotation.Nonnull;

import io.reactivex.annotations.Experimental;

/**
 * Factory which returns Gson {@link io.reactivex.SingleTransformer} implementations.
 */
public final class GsonTransformerFactory {

    private GsonTransformerFactory() {
    }

    /**
     * Returns a new {@link ObjectToSourceTransformer}, which uses a {@link GsonBufferedSourceAdapter} to parse from
     * objects of the specified type to JSON using the provided {@link Gson} instance.
     */
    @Nonnull
    @Experimental
    public static <Parsed> ObjectToSourceTransformer<Parsed> createObjectToSourceTransformer(@Nonnull Gson gson) {
        return new ObjectToSourceTransformer<>(new GsonBufferedSourceAdapter<Parsed>(gson));
    }

}
