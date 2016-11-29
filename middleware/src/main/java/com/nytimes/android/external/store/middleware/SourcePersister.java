package com.nytimes.android.external.store.middleware;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.middleware.fs.FileSystem;
import com.nytimes.android.store.base.Persister;
import com.nytimes.android.store.base.impl.BarCode;

import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

/**
 * Persister to be used when storing something to persister from a BufferedSource
 * example usage:
 * ParsingStoreBuilder.<BufferedSource, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(new SourcePersister(fileSystem))
 * .parser(new GsonSourceParser<>(gson, BookResults.class))
 * .open();
 */
public class SourcePersister implements Persister<BufferedSource> {

    private final SourceFileReader sourceFileReader;
    private final SourceFileWriter sourceFileWriter;

    @Inject
    public SourcePersister(FileSystem fileSystem) {
        this.sourceFileReader = new SourceFileReader(fileSystem);
        sourceFileWriter = new SourceFileWriter(fileSystem);
    }

    @Override
    public Observable<BufferedSource> read(final BarCode barCode) {
        return sourceFileReader.read(barCode);
    }

    @Override
    public Observable<Boolean> write(final BarCode barCode, final BufferedSource data) {
        return sourceFileWriter.write(barCode, data);
    }


    @NonNull
    static String pathForBarcode(BarCode barCode) {
        return barCode.getType() + barCode.getKey();
    }

}
