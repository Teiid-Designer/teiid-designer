/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.teiid.designer.test.AllPluginTests.PluginTestGatherer;

@RunWith(Suite.class)
@SuiteClasses({ PluginTestGatherer.class })
public class AllPluginTests {

    private static final String ALL_PLUGIN_TESTS = "All Plugin Tests"; //$NON-NLS-1$

    private static final String BUNDLE_FILTER = "org\\.teiid.*(test)$"; //$NON-NLS-1$

    public static class PluginTestGatherer extends TestSuite {
        public static Test suite() {

            TestSuite aggregateSuite = new TestSuite(ALL_PLUGIN_TESTS);

            BundleContext context = TestDesignerPlugin.getPlugin()
                    .getBundleContext();
            for (Bundle bundle : context.getBundles()) {
                if (!bundle.getSymbolicName().matches(BUNDLE_FILTER)) {
                    continue;
                }

                collectTests(aggregateSuite, bundle);
            }

            return aggregateSuite;
        }

        /**
         * @param parentSuite
         * @param bundle
         * @return
         */
        private static void collectTests(TestSuite parentSuite, Bundle bundle) {

            Enumeration<URL> entries = bundle.findEntries(
                    "/", "AllTests.class", true); //$NON-NLS-1$ //$NON-NLS-2$
            if (entries == null || !entries.hasMoreElements()) {
                TestDesignerPlugin
                        .logWarning("No AllTests class in bundle " + bundle.getSymbolicName()); //$NON-NLS-1$
                return;
            }

            TestSuite suite = new TestSuite(bundle.getSymbolicName());

            while (entries.hasMoreElements()) {
                URL element = entries.nextElement();

                try {
                    URL url = FileLocator.toFileURL(element);
                    String className = convertToClassName(url);

                    TestDesignerPlugin.logInfo("Test class name: " + className); //$NON-NLS-1$

                    Class<?> klazz = loadClassFromBundle(bundle, className);

                    SuiteClasses suiteClasses = klazz.getAnnotation(SuiteClasses.class);
                    for (Class<?> suiteKlazz : suiteClasses.value()) {
                        if (TestCase.class.isAssignableFrom(suiteKlazz)) {
                            addJUnit3TestClass(suite, (Class<? extends TestCase>) suiteKlazz);
                        }
                        else {
                            addJUnit4TestClass(suite, suiteKlazz);
                        }
                    }

                    parentSuite.addTest(suite);
                }
                catch (Exception ex) {
                    TestDesignerPlugin.logException(ex);
                }
            }
        }

        /**
         * Such classes do not extend {@link TestCase} and simply use the Test annotation
         * to denote their tests.
         * 
         * @param suite
         * @param suiteKlazz
         */
        private static void addJUnit4TestClass(TestSuite suite, Class<?> klazz) {
            if (! hasJUnit4AnnotateTestMethods(klazz)) {
                TestDesignerPlugin.logWarning("Test Class " + klazz.getSimpleName() + " tagged for testing but has no annotated Test methods"); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            
            JUnit4TestAdapter testAdapter = new JUnit4TestAdapter(klazz);
            suite.addTest(testAdapter);
        }

        /**
         * @param klazz
         * @return
         */
        private static boolean hasJUnit4AnnotateTestMethods(Class<?> klazz) {
            for (Method method : klazz.getDeclaredMethods()) {
                if (method.getAnnotation(org.junit.Test.class) != null) {
                    return true;
                }
            }
            
            return false;
        }

        /**
         * @param suite
         * @param suiteKlazz
         */
        private static void addJUnit3TestClass(TestSuite suite, Class<? extends TestCase> suiteKlazz) {
            try {
                // JUnit 3 tests can have a static suite() method that creates
                // the suite and has its own setup and teardown. Should prefer
                // to use these.
                Method declaredMethod = suiteKlazz.getDeclaredMethod("suite"); //$NON-NLS-1$
                Object object = declaredMethod
                        .invoke(suiteKlazz, new Object[0]);
                suite.addTest((Test) object);
            }
            catch (Exception noSuchMethodException) {
                suite.addTestSuite(suiteKlazz);
            }
        }

        /**
         * @param bundle
         * @param className
         * @return
         */
        private static Class<?> loadClassFromBundle(Bundle bundle, String className) throws ClassNotFoundException {
            Class<?> klazz = null;

            if (Platform.isFragment(bundle)) {
                // Need to use the bundle's host for loading the class
                Bundle[] hosts = Platform.getHosts(bundle);
                if (hosts == null) {
                    TestDesignerPlugin.logWarning("Cannot test fragment as NO host!"); //$NON-NLS-1$
                    return klazz;
                }

                for (Bundle host : hosts) {
                    klazz = host.loadClass(className);
                    if (klazz != null) {
                        break;
                    }
                }
            }
            else {
                klazz = bundle.loadClass(className);
            }

            return klazz;
        }

        /**
         * @param url
         */
        private static String convertToClassName(URL url) {
            String fileName = url.getPath();

            // Drop prefix
            fileName = fileName.substring(fileName.indexOf("bin/") + 4); //$NON-NLS-1$

            // Drop .class suffix
            fileName = fileName.substring(0,
                    fileName.length() - ".class".length()); //$NON-NLS-1$

            // Replace / with .
            fileName = fileName.replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$

            return fileName;
        }
    }

}
