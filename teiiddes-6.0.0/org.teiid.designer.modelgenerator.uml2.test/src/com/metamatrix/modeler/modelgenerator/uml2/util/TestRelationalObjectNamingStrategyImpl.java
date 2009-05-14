/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import junit.framework.TestCase;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;

/**
 * TestRelationalObjectNamingStrategyImpl
 */
public class TestRelationalObjectNamingStrategyImpl extends TestCase {
    private static final UMLFactory FACTORY = UMLFactory.eINSTANCE;
    private final RelationalObjectNamingStrategyImpl namingStrategy = new RelationalObjectNamingStrategyImpl(
                                                                                                             new Uml2RelationalOptions());

    /**
     * Constructor for TestRelationalObjectNamingStrategyImpl.
     * 
     * @param name
     */
    public TestRelationalObjectNamingStrategyImpl( String name ) {
        super(name);
    }

    public void testGetNameForClassBaseTableNestedPackagesSameName() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("GrandparentPackage"); //$NON-NLS-1$

        Class grandParentClass = FACTORY.createClass();
        grandParentClass.setPackage(grandParentPkg);

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setName("Class");//$NON-NLS-1$
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$
        classToProcess.setPackage(parentPackage);

        final String name = namingStrategy.getNameForClassBaseTable(classToProcess);

        if (!"Class_GrandparentPackage".equals(name)) {//$NON-NLS-1$
            fail(name);
        }

    }

    public void testGetNameForClassBaseTableNestedPackagesDiffName() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("GrandparentPackage");//$NON-NLS-1$

        Class grandParentClass = FACTORY.createClass();
        grandParentClass.setPackage(grandParentPkg);

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setName("Class1");//$NON-NLS-1$
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$
        classToProcess.setPackage(parentPackage);

        if (!"Class_Class1".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }

    }

    public void testGetNameForClassBaseTableNoPackage() {

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$

        if (!"Class".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }

    }

    public void testGetNameForClassBaseTableNullConditions() {
        Exception e = null;

        try {
            namingStrategy.getNameForClassBaseTable(null);
        } catch (IllegalArgumentException e1) {
            e = e1;
        }

        if (e == null) {
            fail();
        }
    }

    public void testGetNameForClassBaseTableParentPackagesWNoClasses() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("GrandparentPackage"); //$NON-NLS-1$

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setName("Class");//$NON-NLS-1$
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$
        classToProcess.setPackage(parentPackage);

        if (!"Class_GrandparentPackage".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }

    }

    public void testGetNameForClassBaseTableNested2PackagesSameName() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("Class"); //$NON-NLS-1$

        Class grandParentClass = FACTORY.createClass();
        grandParentClass.setPackage(grandParentPkg);

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setName("Class");//$NON-NLS-1$
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$
        classToProcess.setPackage(parentPackage);

        if (!"Class".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }

    }

    public void testGetNameForClassBaseTableNestedClassNullName() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("GrandparentPackage");//$NON-NLS-1$

        Class grandParentClass = FACTORY.createClass();
        grandParentClass.setPackage(grandParentPkg);

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setName("Class1");//$NON-NLS-1$
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setPackage(parentPackage);

        if (!"Class1".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }
    }

    public void testGetNameForClassBaseTableNestedParentPackageNullName() {
        Package grandParentPkg = FACTORY.createPackage();
        grandParentPkg.setName("GrandparentPackage"); //$NON-NLS-1$

        Class grandParentClass = FACTORY.createClass();
        grandParentClass.setPackage(grandParentPkg);

        Package parentPackage = FACTORY.createPackage();
        parentPackage.setNestingPackage(grandParentPkg);

        Class classToProcess = FACTORY.createClass();
        classToProcess.setName("Class");//$NON-NLS-1$
        classToProcess.setPackage(parentPackage);

        if (!"Class".equals(namingStrategy.getNameForClassBaseTable(classToProcess))) {//$NON-NLS-1$
            fail();
        }

    }

}
