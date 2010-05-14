/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.relational;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * @since 4.0
 */
public final class TestJdbcImporter extends TestCase implements CoreStringUtil.Constants {

    /**
     * @since 4.0
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite(TestJdbcImporter.class);
        return new TestSetup(suite) {
            @Override
            protected void setUp() throws Exception {
                super.setUp();
            }

            @Override
            protected void tearDown() throws Exception {
                super.tearDown();
            }
        };
    }

    private JdbcImporter importer;

    /**
     * @see junit.framework.TestCase#setUp()
     * @since 4.0
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.importer = new JdbcImporter();
    }

    /**
     * @since 4.0
     */
    public void testSetUpdatedModel() throws Exception {
        try {
            this.importer.setUpdatedModel(null);
        } catch (final IllegalArgumentException expected) {
        }
        ModelResource modelRes = this.importer.getUpdatedModel();
        if (modelRes != null) {
            fail("Expected updatedModelResource to be null"); //$NON-NLS-1$
        }
    }
}
