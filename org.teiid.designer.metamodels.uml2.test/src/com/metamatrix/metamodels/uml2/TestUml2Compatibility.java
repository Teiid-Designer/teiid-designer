/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * TestUml2Compatibility
 */
public class TestUml2Compatibility extends TestCase {

    protected static final String METACLASS_FILE_PATH = SmartTestSuite.getTestDataPath() + "/MetaclassNameInclusionList.txt"; //$NON-NLS-1$
    protected static final String eNS_URI = UMLPackage.eNS_URI;
    protected static final int PREFIX_LENGTH = eNS_URI.length() + 1;
    protected static String[] metaClassNames;

    /**
     * Constructor for TestUml2Compatibility.
     * 
     * @param name
     */
    public TestUml2Compatibility( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestUml2Compatibility"); //$NON-NLS-1$
        suite.addTestSuite(TestUml2Compatibility.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() throws Exception {
                metaClassNames = readMetaClassFile(new File(METACLASS_FILE_PATH));
                assertTrue(metaClassNames.length > 10);
            }

            @Override
            public void tearDown() {
            }
        };
    }

    public static String[] readMetaClassFile( final File f ) throws Exception {
        if (f != null && f.exists()) {
            final List<String> names = new ArrayList<String>();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String str;
            while ((str = in.readLine()) != null) {
                String name = str.substring(PREFIX_LENGTH);
                names.add(name);
            }
            in.close();
            return (String[])names.toArray(new String[names.size()]);
        }
        return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
    }

    private static void sortAndOutputStrings( final List<String> strings ) {
        Collections.sort(strings, String.CASE_INSENSITIVE_ORDER);
        for (final Iterator<String> iter = strings.iterator(); iter.hasNext();) {
            final String str = (String)iter.next();
            System.out.println("    " + str); //$NON-NLS-1$
        }
    }

    private static List<String> getNameList( final List<?> objs ) {
        List<String> result = new ArrayList<String>(objs.size());
        for (final Iterator<?> iter = objs.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof EClass) {
                result.add(((EClass)obj).getName());
            } else if (obj instanceof EStructuralFeature) {
                result.add(((EStructuralFeature)obj).getName());
            } else if (obj instanceof EEnumLiteral) {
                result.add(((EEnumLiteral)obj).getName());
            }
        }
        return result;
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testGetEClassifiers() {
        System.out.println("\n testGetEClassifiers()"); //$NON-NLS-1$
        for (int i = 0; i != metaClassNames.length; ++i) {
            EClassifier eClassifier = UMLPackage.eINSTANCE.getEClassifier(metaClassNames[i]);
            if (eClassifier == null) {
                System.out.println("--> EClassifier is null " + metaClassNames[i]); //$NON-NLS-1$
            }
        }
    }

    public void testGetESuperTypes() {
        System.out.println("\n testGetESuperTypes()"); //$NON-NLS-1$
        for (int i = 0; i != metaClassNames.length; ++i) {
            EClassifier eClassifier = UMLPackage.eINSTANCE.getEClassifier(metaClassNames[i]);
            if (eClassifier != null && eClassifier instanceof EClass) {
                System.out.println("  SuperTypes for " + ((EClass)eClassifier).getName()); //$NON-NLS-1$
                List<String> superTypeNames = getNameList(((EClass)eClassifier).getEAllSuperTypes());
                sortAndOutputStrings(superTypeNames);
            } else {
                System.out.println("--> EClassifier is null or not instanceof EClass " + metaClassNames[i]); //$NON-NLS-1$
            }
        }
    }

    public void testGetEStructuralFeatures() {
        System.out.println("\n testGetEStructuralFeatures()"); //$NON-NLS-1$
        for (int i = 0; i != metaClassNames.length; ++i) {
            EClassifier eClassifier = UMLPackage.eINSTANCE.getEClassifier(metaClassNames[i]);
            if (eClassifier != null && eClassifier instanceof EClass) {
                System.out.println("  Features for " + ((EClass)eClassifier).getName()); //$NON-NLS-1$
                List<String> superTypeNames = getNameList(((EClass)eClassifier).getEAllStructuralFeatures());
                sortAndOutputStrings(superTypeNames);
            } else {
                System.out.println("--> EClassifier is null or not instanceof EClass " + metaClassNames[i]); //$NON-NLS-1$
            }
        }
    }

    public void testGetEEnumLiterals() {
        System.out.println("\n testGetEEnumLiterals()"); //$NON-NLS-1$
        for (int i = 0; i != metaClassNames.length; ++i) {
            EClassifier eClassifier = UMLPackage.eINSTANCE.getEClassifier(metaClassNames[i]);
            if (eClassifier != null && eClassifier instanceof EEnum) {
                System.out.println("  Enumeration literals for " + ((EEnum)eClassifier).getName()); //$NON-NLS-1$
                List<String> literals = getNameList(((EEnum)eClassifier).getELiterals());
                sortAndOutputStrings(literals);
            }
        }
    }

}
