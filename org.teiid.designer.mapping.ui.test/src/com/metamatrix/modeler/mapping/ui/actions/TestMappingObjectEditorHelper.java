/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.TransformationFactory;

public class TestMappingObjectEditorHelper extends TestCase {

    private final TransformationFactory factory = TransformationFactory.eINSTANCE;

    static void oneTimeSetUp() {
    }

    static void oneTimeTearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TestMappingObjectEditorHelper"); //$NON-NLS-1$
        suite.addTestSuite(TestMappingObjectEditorHelper.class);

        return new TestSetup(suite) { // junit.extensions package
            @Override
            public void setUp() {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    public static void main( String[] args ) {
        junit.textui.TestRunner.run(TestMappingObjectEditorHelper.class);
    }

    public void testSimpleCanUndoDelete() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final MappingClass mc = factory.createMappingClass();

        final boolean canUndo = moeh.canUndoDelete(mc);
        if (canUndo) {
            fail("Can Undo returned true, expected false"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDelete2() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final String str = new String();

        final boolean canUndo = moeh.canUndoDelete(str);
        if (!canUndo) {
            fail("Can Undo returned false, expected true"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteNull() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();

        final boolean canUndo = moeh.canUndoDelete(null);
        if (!canUndo) {
            fail("Can Undo returned false, expected true"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteMCCollection() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();
        mcs.add(factory.createMappingClass());
        mcs.add(factory.createMappingClass());

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (canUndo) {
            fail("Can Undo returned true, expected false"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteEmptyCollection() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (!canUndo) {
            fail("Can Undo returned false, expected true"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteMixedMCCollection() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();
        mcs.add(factory.createMappingClass());
        mcs.add(new String());

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (canUndo) {
            fail("Can Undo returned true, expected false"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteMixedCollection2() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();
        mcs.add(new String());
        mcs.add(factory.createMappingClass());

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (canUndo) {
            fail("Can Undo returned true, expected false"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteNullCollection() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();
        mcs.add(null);

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (!canUndo) {
            fail("Can Undo returned false, expected true"); //$NON-NLS-1$
        }
    }

    public void testSimpleCanUndoDeleteStringCollection() {
        final MappingObjectEditHelper moeh = new MappingObjectEditHelper();
        final Collection mcs = new ArrayList();
        mcs.add(new String());

        final boolean canUndo = moeh.canUndoDelete(mcs);
        if (!canUndo) {
            fail("Can Undo returned false, expected true"); //$NON-NLS-1$
        }
    }
}
