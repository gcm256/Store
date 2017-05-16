package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.fs.ObjectToSourceTransformer;

import javax.annotation.Nonnull;

import rx.annotations.Experimental;

/**
 * Factory which returns Jackson {@link rx.Observable.Transformer} implementations.
 */
public final class JacksonTransformerFactory {

    private JacksonTransformerFactory() {
    }

    /**
     * Returns a new {@link ObjectToSourceTransformer}, which uses a {@link JacksonBufferedSourceAdapter} to parse from
     * objects of the specified type to JSON using the provided
     * {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper} instance.
     */
    @Nonnull
    @Experimental
    public static <Parsed> ObjectToSourceTransformer<Parsed> createObjectToSourceTransformer(@Nonnull ObjectMapper
                                                                                                         objectMapper) {
        return new ObjectToSourceTransformer<>(new JacksonBufferedSourceAdapter<Parsed>(objectMapper));
    }

}
