package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;

import static com.nytimes.android.external.fs.SourcePersister.pathForBarcode;

public class SourceFileReader extends FSReader<BarCode> implements DiskRead<BufferedSource, BarCode> {

    public SourceFileReader(FileSystem fileSystem) {
        this(fileSystem, new BarCodePathResolver());
    }

    public SourceFileReader(FileSystem fileSystem, PathResolver<BarCode> pathResolver) {
        super(fileSystem, pathResolver);
    }

    @Nonnull
    public RecordState getRecordState(@Nonnull BarCode barCode,
                                      @Nonnull TimeUnit expirationUnit,
                                      long expirationDuration) {
        return fileSystem.getRecordState(expirationUnit, expirationDuration, pathForBarcode(barCode));
    }
}
