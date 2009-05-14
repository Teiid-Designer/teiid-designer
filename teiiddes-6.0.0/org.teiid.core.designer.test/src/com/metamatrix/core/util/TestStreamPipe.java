/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;

/**
 * TestStreamPipe
 */
public class TestStreamPipe extends TestCase {

    private StreamPipe pipe;
    private static final String DATA_FILE = "streampipe.test.txt"; //$NON-NLS-1$
    private InputStream dataStream;

    /**
     * Constructor for TestStreamPipe.
     * 
     * @param name
     */
    public TestStreamPipe( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pipe = new StreamPipe();
        dataStream = new FileInputStream(SmartTestSuite.getTestDataFile(DATA_FILE));
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        pipe = null;
        if (dataStream != null) {
            dataStream.close();
        }
    }

    public class Reader {
        final InputStream stream;
        final int bufferSize;

        public Reader( final InputStream stream ) {
            this(stream, 1024);
        }

        public Reader( final InputStream stream,
                       int bufferSize ) {
            this.stream = stream;
            this.bufferSize = bufferSize;
        }

        public void print( final String desc,
                           final boolean shouldComplete ) {
            Thread r = new Thread() {
                @Override
                public void run() {
                    byte[] buffer = new byte[bufferSize];
                    // Print Data
                    try {
                        while ((stream.read(buffer)) > -1) {
                            if (!shouldComplete) {
                                stream.close(); // close early
                            }
                        }

                        stream.close();
                        if (!shouldComplete) {
                            fail("Expected an IO Exception while reading from the stream"); //$NON-NLS-1$
                        }
                    } catch (Throwable ex) {
                        if (shouldComplete) {
                            fail(ex.getMessage());
                        }
                    }
                }
            };
            r.start();
        }
    }

    public void helpTestReading( String desc,
                                 int bufferSize,
                                 boolean readShouldComplete,
                                 boolean writeShouldComplete ) throws Exception {
        final Reader reader = new Reader(pipe.getInputStream());
        reader.print(desc, readShouldComplete);
        Thread.sleep(100); // sleep a bit

        OutputStream ostream = pipe.getOutputStream();

        // Write Data
        byte[] buffer = new byte[bufferSize / 3];
        int n;
        try {
            while ((n = dataStream.read(buffer)) > -1) {
                ostream.write(buffer, 0, n);
                if (!writeShouldComplete) {
                    ostream.close();
                    dataStream.close();
                }
            }

            ostream.close();
            dataStream.close();
            if (!writeShouldComplete) {
                fail("Expected an IO Exception while writing to the stream"); //$NON-NLS-1$
            }
        } catch (IOException e) {
            if (readShouldComplete && writeShouldComplete) {
                throw e;
            }
        }
    }

    public void testReadingWith512Buffer() throws Exception {
        final boolean readCompletes = true;
        final boolean writeCompletes = true;
        helpTestReading("Reading With 512 buffer", 512, readCompletes, writeCompletes); //$NON-NLS-1$
    }

    public void testReadingWith1024Buffer() throws Exception {
        final boolean readCompletes = true;
        final boolean writeCompletes = true;
        helpTestReading("Reading With 1024 buffer", 1024, readCompletes, writeCompletes); //$NON-NLS-1$
    }

    public void testReadingWith2048Buffer() throws Exception {
        final boolean readCompletes = true;
        final boolean writeCompletes = true;
        helpTestReading("Reading With 2048 buffer", 2048, readCompletes, writeCompletes); //$NON-NLS-1$
    }

    public void testReadingWith512BufferAndCloseWritingEarly() throws Exception {
        final boolean readCompletes = true;
        final boolean writeCompletes = false;
        helpTestReading("Reading With 512 buffer and close writing early", 512, readCompletes, writeCompletes); //$NON-NLS-1$
    }

    public void testReadingWith512BufferAndCloseReadingEarly() throws Exception {
        final boolean readCompletes = false;
        final boolean writeCompletes = true;
        helpTestReading("Reading With 512 buffer and close reading early", 512, readCompletes, writeCompletes); //$NON-NLS-1$
    }

}
