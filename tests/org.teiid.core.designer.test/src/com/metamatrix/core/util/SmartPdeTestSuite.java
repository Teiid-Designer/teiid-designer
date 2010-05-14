/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.IOException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Ignore;
import org.osgi.framework.Bundle;

@Ignore
public class SmartPdeTestSuite extends SmartTestSuite {

    private static final String FILE_URL_PREFIX = "file:"; //$NON-NLS-1$

    public SmartPdeTestSuite( String projectName,
                              String testSuiteName ) {
        super(projectName, testSuiteName);

        try {
            // Look up the project assuming it is a plugin ...
            final Bundle bundle = Platform.getBundle(projectName);
            if (bundle != null) {
                final URL installUrl = bundle.getEntry("/"); //$NON-NLS-1$
                final URL resolvedUrl = FileLocator.resolve(installUrl);
                String location = resolvedUrl.toExternalForm(); // has delim on end
                if (location != null) {
                    // Lop off the "file:" on the front of the location, as that screws up the File constructor
                    if (location.startsWith(FILE_URL_PREFIX)) {
                        location = location.substring(FILE_URL_PREFIX.length());
                        // Remove leading slash if it precedes a drive letter
                        if (location.indexOf(':') > 0 && location.charAt(0) == '/') {
                            location = location.substring(1);
                        }
                    }
                    final String testDataPath = location + SmartTestSuite.DEFAULT_TESTDATA_PATH;
                    super.setTestDataPath(testDataPath);
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public SmartPdeTestSuite( String testdataPath,
                              Class testClass ) {
        super(testdataPath, testClass);
    }

    /*
     * @see junit.framework.TestSuite#addTestSuite(java.lang.Class)
     */
    @Override
    public void addTestSuite( Class testClass ) {
        addTest(new SmartPdeTestSuite(testdataPath, testClass));
    }
}
