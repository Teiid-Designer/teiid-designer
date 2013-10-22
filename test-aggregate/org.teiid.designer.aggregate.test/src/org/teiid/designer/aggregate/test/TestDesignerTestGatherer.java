/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.aggregate.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.runners.Suite.SuiteClasses;
import org.mockito.configuration.MockitoConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Gather all the tests in the test fragments to run in a single
 * aggregated test suite
 */
public class TestDesignerTestGatherer extends TestCase {

    private static final String ALL_PLUGIN_TESTS = "All Plugin Tests"; //$NON-NLS-1$

    private static final String BUNDLE_FILTER = "org\\.teiid.*"; //$NON-NLS-1$

    private static final String ALL_TESTS_CLASS = "AllTests.class"; //$NON-NLS-1$

    private static final String BUNDLE_ROOT = "/"; //$NON-NLS-1$

    private static final String PACKAGE_SEPARATOR = "."; //$NON-NLS-1$

    private static final String ORG = "org"; //$NON-NLS-1$

    private static final String TEIID = "teiid"; //$NON-NLS-1$

    private static final String TEST = "test"; //$NON-NLS-1$

    private static Map<String, URL> testCache = new HashMap<String, URL>();

    private static Map<String, Integer> testsInPackage = new HashMap<String, Integer>();

    private static Map<String, Integer> testsInClass = new HashMap<String, Integer>();

    /**
     * Assemble a junit 3 test suite
     *
     * @return test suite containing all found test classes
     */
    public static Test suite() {
        // Turn off mockito's objenesis cache as it can return the
        // wrong Admin interface in the runtime client tests
        MockitoConfiguration.setEnableClassCache(false);

        TestSuite aggregateSuite = new TestSuite(ALL_PLUGIN_TESTS);

        BundleContext context = TestDesignerPlugin.getPlugin().getBundleContext();
        for (Bundle bundle : context.getBundles()) {
            if (Platform.isFragment(bundle)) {
                // Ignore test fragments since AllTests classes loaded from their
                // host bundles
                System.out.println("Ignoring Test fragment " + bundle.getSymbolicName() + " since it is accessible from its host bundle");
                continue;
            }

            if (bundle.getSymbolicName().contains(TEST)) {
                // Ignore the test framework plugins
                System.out.println("Ignoring Test framework plugin " + bundle.getSymbolicName());
                continue;
            }

            if (!bundle.getSymbolicName().matches(BUNDLE_FILTER)) {
                System.out.println("Ignoring non-teiid plugin " + bundle.getSymbolicName());
                continue;
            }

            collectTests(aggregateSuite, bundle);
        }

        System.out.println("=== Number of Tests found per Class ==="); //$NON-NLS-1$
        outputCounts(testsInClass);

        System.out.println("=== Number of Tests found per Package ==="); //$NON-NLS-1$
        outputCounts(testsInPackage);

        if (aggregateSuite.countTestCases() == 0) {
            aggregateSuite.addTest(TestSuite.warning("Cannot find any tests conforming to the filter " + BUNDLE_FILTER)); //$NON-NLS-1$
        }

        return aggregateSuite;
    }

    /**
     * @param parentSuite
     * @param bundle
     */
    private static void collectTests(TestSuite parentSuite, Bundle bundle) {

        Enumeration<URL> entries = bundle.findEntries(BUNDLE_ROOT, ALL_TESTS_CLASS, true);
        if (entries == null || !entries.hasMoreElements()) {
            System.out.println("No AllTest class found in plugin " + bundle.getSymbolicName());
            return;
        }

        System.out.println("Collecting Tests for " + bundle.getSymbolicName() + PACKAGE_SEPARATOR + TEST); //$NON-NLS-1$

        TestSuite suite = new TestSuite(bundle.getSymbolicName() + PACKAGE_SEPARATOR + TEST);

        while (entries.hasMoreElements()) {
            URL element = entries.nextElement();

            try {
                URL url = FileLocator.toFileURL(element);
                String className = convertToClassName(url);

                /*
                 * If there are classes in both the bin and target/classes directories
                 * then both eclipse and maven have compiled the test plugin. We only
                 * need to test one of them so cache them accordingly.
                 */
                if (testCache .containsKey(className))
                    continue;

                if (AllTests.class.equals(className)) {
                    continue;
                }

                testCache.put(className, url);

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
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * @param counts
     */
    private static void outputCounts(Map<String, Integer> counts) {
        int total = 0;
        List<String> outLines = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            outLines.add(entry.getKey() + "\t\t\t" + entry.getValue()); //$NON-NLS-1$
            total += entry.getValue();
        }

        Collections.sort(outLines);

        System.out.println();
        for (String outLine : outLines) {
            System.out.println(outLine);
        }

        System.out.println("Total Number of Tests = " + total); //$NON-NLS-1$
        System.out.println();
    }

    private static void count(Class<?> testClass, int noTestCases) {
        String className = testClass.getCanonicalName();
        Integer classCount = testsInClass.get(className);
        if (classCount == null) {
            classCount = 0;
        }
        testsInClass.put(className, (classCount + noTestCases));

        String pkgName = testClass.getPackage().getName();
        Integer pkgCount = testsInPackage.get(pkgName);
        if (pkgCount == null) {
            pkgCount = 0;
        }
        testsInPackage.put(pkgName, (pkgCount + noTestCases));
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
            return;
        }

        JUnit4TestAdapter testAdapter = new JUnit4TestAdapter(klazz);
        suite.addTest(testAdapter);

        count(klazz, testAdapter.countTestCases());
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
            Object object = declaredMethod.invoke(suiteKlazz, new Object[0]);
            Test testCase = (Test) object;
            suite.addTest(testCase);

            count(suiteKlazz, testCase.countTestCases());
        }
        catch (Exception noSuchMethodException) {
            TestSuite testSuite = new TestSuite(suiteKlazz);
            suite.addTest(testSuite);

            count(suiteKlazz, testSuite.countTestCases());
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
    private static String convertToClassName(URL url) throws Exception {
        IPath fullPath = new Path(url.getPath());

        IPath classPath = null;
        for (int i = 0; i < fullPath.segmentCount(); ++i) {
            String segment = fullPath.segment(i);
            String segment2 = fullPath.segment(i + 1);

            if (segment == null || segment2 == null)
                continue;

            if (! ORG.equals(segment))
                continue;

            if (! segment2.startsWith(TEIID))
                continue;

            classPath = fullPath.removeFirstSegments(i);
            break;
        }

        if (classPath == null) {
            // this has been compiled in some other directory that we did not expect
            throw new Exception("Cannot process unsupported compiled class: " + fullPath); //$NON-NLS-1$
        }

        // Drop .class suffix
        classPath = classPath.removeFileExtension();

        // Replace / with .
        StringBuilder classNameBuilder = new StringBuilder();
        for (int i = 0; i < classPath.segmentCount(); ++i) {
            String segment = classPath.segment(i);
            classNameBuilder.append(segment);

            if (i + 1 < classPath.segmentCount())
                classNameBuilder.append(PACKAGE_SEPARATOR);
        }

        return classNameBuilder.toString();
    }
}