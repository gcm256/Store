package com.nytimes.android.external.store.middleware;

import com.nytimes.android.external.store.middleware.fs.FileSystem;
import com.nytimes.android.store.base.DiskRead;
import com.nytimes.android.store.base.impl.BarCode;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

import static com.nytimes.android.external.store.middleware.SourcePersister.pathForBarcode;

public class SourceFileReader implements DiskRead<BufferedSource> {

    final FileSystem fileSystem;

    @Inject
    public SourceFileReader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public Observable<BufferedSource> read(final BarCode barCode) {
        return Observable.fromCallable(new Callable<BufferedSource>() {
            @Override
            public BufferedSource call() throws FileNotFoundException {
                return fileSystem.read(pathForBarcode(barCode));
            }
        });
    }
}
