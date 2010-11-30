/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt.impl;

import java.io.File;
import java.util.Collection;
import junit.framework.TestCase;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.core.xslt.Style;
import com.metamatrix.core.xslt.StyleFromResource;
import com.metamatrix.core.xslt.StyleRegistry;

/**
 * TestStyleRegistryImpl
 */
public class TestStyleRegistryImpl extends TestCase {

    private static final String[] STYLE_NAMES = new String[] {"Style1", //$NON-NLS-1$
        "Style2", //$NON-NLS-1$
        " " //$NON-NLS-1$
    };
    private static final String[] STYLE_DESCS = new String[] {"Description for Style1", //$NON-NLS-1$
        "Description for Style2", //$NON-NLS-1$
        "Description for Style with space as name" //$NON-NLS-1$
    };
    private static final String[] STYLE_PATHS = new String[] {"/path/for/Style1", //$NON-NLS-1$
        "path/for_Style2", //$NON-NLS-1$
        "path/for_Style with space as name" //$NON-NLS-1$
    };

    private StyleRegistry emptyRegistry;
    private StyleRegistry registry;
    private StyleRegistry registryWithNoDescriptions;

    /**
     * Constructor for TestStyleRegistryImpl.
     * 
     * @param name
     */
    public TestStyleRegistryImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.emptyRegistry = new StyleRegistryImpl();
        this.registry = new StyleRegistryImpl();
        this.registryWithNoDescriptions = new StyleRegistryImpl();

        final File testDataFolder = new File(SmartTestDesignerSuite.getTestDataPath());

        // Create some styles
        for (int i = 0; i < STYLE_NAMES.length; ++i) {
            final Style style1 = new StyleFromResource(new File(testDataFolder, STYLE_PATHS[i]).toURI().toURL(), STYLE_NAMES[i],
                                                       STYLE_DESCS[i]);
            this.registry.getStyles().add(style1);

            final Style style2 = new StyleFromResource(new File(testDataFolder, STYLE_PATHS[i]).toURI().toURL(), STYLE_NAMES[i]);
            this.registryWithNoDescriptions.getStyles().add(style2);
        }
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.emptyRegistry = null;
        this.registry = null;
        this.registryWithNoDescriptions = null;
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testStartupData() {
        final Collection styles = this.emptyRegistry.getStyles();
        assertNotNull(styles);
        assertEquals(0, styles.size());

        final Collection styles2 = this.registry.getStyles();
        assertNotNull(styles2);
        assertEquals(3, styles2.size());

        final Collection styles3 = this.registryWithNoDescriptions.getStyles();
        assertNotNull(styles3);
        assertEquals(3, styles3.size());
    }

    public void testStyleRegistryImpl() {
        new StyleRegistryImpl();
    }

    public void testGetStyleByNameWithValidNames() {
        for (int i = 0; i < STYLE_NAMES.length; ++i) {
            final String name = STYLE_NAMES[i];
            final Style style = this.registry.getStyle(name);
            assertNotNull(style);
        }
    }

    public void testGetStyleByNameWithInvalidNames() {
        for (int i = 0; i < STYLE_NAMES.length; ++i) {
            final String name = "Some bogus prefix " + STYLE_NAMES[i]; //$NON-NLS-1$
            final Style style = this.registry.getStyle(name);
            assertNull(style);
        }
    }

    public void testGetStyleByNameWithInvalidNamesOnEmptyRegistry() {
        for (int i = 0; i < STYLE_NAMES.length; ++i) {
            final String name = "Some bogus prefix " + STYLE_NAMES[i]; //$NON-NLS-1$
            final Style style = this.emptyRegistry.getStyle(name);
            assertNull(style);
        }
    }

    public void testGetStyleByNameWithNameWhenNoStyleExists() {
        final Style style = this.registryWithNoDescriptions.getStyle(null);
        assertNull(style);
    }

    public void testGetStyleByNameWithZeroLengthWhenNoStyleExists() {
        final Style style = this.registryWithNoDescriptions.getStyle(""); //$NON-NLS-1$
        assertNull(style);
    }

    public void testGetStyles() {
        final Collection styles = this.emptyRegistry.getStyles();
        assertNotNull(styles);
        assertEquals(0, styles.size());
    }

}
