package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class FilePersisterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;

    private final BarCode simple = new BarCode("type", "key");
    private final String resolvedPath = new BarCodePathResolver().resolve(simple);
    private Persister<BufferedSource, BarCode> fileSystemPersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileSystemPersister = FileSystemPersister.create(fileSystem, new BarCodePathResolver());
    }

    @Test
    public void readExists() throws FileNotFoundException {
        when(fileSystem.exists(resolvedPath))
                .thenReturn(true);
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);

        BufferedSource returnedValue = fileSystemPersister.read(simple).blockingFirst();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void readDoesNotExist() throws FileNotFoundException {
        expectedException.expect(NoSuchElementException.class);
        when(fileSystem.exists(resolvedPath))
                .thenReturn(false);

        fileSystemPersister.read(simple).blockingFirst();
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void writeThenRead() throws IOException {
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);
        when(fileSystem.exists(resolvedPath)).thenReturn(true);
        fileSystemPersister.write(simple, bufferedSource).blockingFirst();
        BufferedSource source = fileSystemPersister.read(simple).blockingFirst();
        InOrder inOrder = inOrder(fileSystem);
        inOrder.verify(fileSystem).write(resolvedPath, bufferedSource);
        inOrder.verify(fileSystem).exists(resolvedPath);
        inOrder.verify(fileSystem).read(resolvedPath);

        assertThat(source).isEqualTo(bufferedSource);


    }

}
