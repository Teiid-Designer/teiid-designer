/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.product;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.product.AbstractProductCustomizer;
import com.metamatrix.ui.product.IProductCharacteristics;
import com.metamatrix.ui.product.IProductContext;

/**
 * @since 4.3
 */
public class TestAbstractProductCustomizer extends TestCase {

    static final IProductContext CONTEXT = IModelerProductContexts.Metamodel.URI;

    static final Object VALUE1 = "value1"; //$NON-NLS-1$

    private static final Object VALUE2 = "value2"; //$NON-NLS-1$

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestAbstractProductCustomizer"); //$NON-NLS-1$
        suite.addTestSuite(TestAbstractProductCustomizer.class);
        return new TestSetup(suite) {

            @Override
            public void setUp() throws Exception {
                UiPlugin plugin = new UiPlugin();
                ((PluginUtilImpl)UiConstants.Util).initializePlatformLogger(plugin);
            }
        };
    }

    public TestAbstractProductCustomizer( String theTestName ) {
        super(theTestName);
    }

    /**
     * Make sure OK to add support for a context
     */
    public void testAddContextSupport1() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                result = addContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Adding context support failed", customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure OK to add support for same context
     */
    public void testAddContextSupport2() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextSupport(CONTEXT);
                result = addContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Adding same context twice failed", customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure result is false when adding support for context that has been removed
     */
    public void testAddContextSupport3() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextSupport(CONTEXT);
                result = addContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Adding context after removing it did not fail", !customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure OK to remove support for a context
     */
    public void testRemoveContextSupport1() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                result = removeContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Removing context failed", customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure OK to remove support for same context
     */
    public void testRemoveContextSupport2() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextSupport(CONTEXT);
                result = removeContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Removing same context twice failed", customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure result is false when removing support for context that has been supported
     */
    public void testRemoveContextSupport3() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextSupport(CONTEXT);
                result = removeContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Removing context after adding it did not fail", !customizer.result); //$NON-NLS-1$
    }

    /**
     * Make sure no entries means support
     */
    public void testSupports1() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
            }
        };

        customizer.loadCustomizations();

        assertTrue("Context should be supported when no customizations", customizer.supports(CONTEXT)); //$NON-NLS-1$
    }

    /**
     * Make sure adding support works
     */
    public void testSupports2() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Context should be supported when support added", customizer.supports(CONTEXT)); //$NON-NLS-1$
    }

    /**
     * Make sure removing support works
     */
    public void testSupports3() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Context should not be supported when support removed", !customizer.supports(CONTEXT)); //$NON-NLS-1$
    }

    /**
     * Make sure entire context not supported if one value support is added
     */
    public void testSupports4() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Entire context should not be supported when support for one value has been added", !customizer.supports(CONTEXT)); //$NON-NLS-1$
    }

    /**
     * Make sure entire context not supported if one value support is removed
     */
    public void testSupports5() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Entire context should not be supported", !customizer.supports(CONTEXT)); //$NON-NLS-1$
    }

    /**
     * Make sure if no customization then one value is supported
     */
    public void testSupportsValue1() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
            }
        };

        customizer.loadCustomizations();

        assertTrue("One value should be supported when no customization", customizer.supports(CONTEXT, VALUE1)); //$NON-NLS-1$
    }

    /**
     * Make sure if entire context is supported then one value is supported
     */
    public void testSupportsValue2() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("One value should be supported when entire context is supported", customizer.supports(CONTEXT, VALUE1)); //$NON-NLS-1$
    }

    /**
     * Make sure if entire context is not supported then one value is not supported
     */
    public void testSupportsValue3() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextSupport(CONTEXT);
            }
        };

        customizer.loadCustomizations();

        assertTrue("One value should not be supported when entire context is not supported", !customizer.supports(CONTEXT, VALUE1)); //$NON-NLS-1$
    }

    /**
     * Make sure context value is supported after being added
     */
    public void testSupportsValue4() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Value added is not supported", customizer.supports(CONTEXT, VALUE1)); //$NON-NLS-1$
    }

    /**
     * Make sure context value is not supported after being removed
     */
    public void testSupportsValue5() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Value added is not supported", !customizer.supports(CONTEXT, VALUE1)); //$NON-NLS-1$
    }

    /**
     * Make sure one context value is not supported after another value is added
     */
    public void testSupportsValue6() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                addContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Value should not be supported after another value is added", !customizer.supports(CONTEXT, VALUE2)); //$NON-NLS-1$
    }

    /**
     * Make sure one context value is supported after another value is removed
     */
    public void testSupportsValue7() {
        FakeProductCustomizer customizer = new FakeProductCustomizer() {
            public void loadCustomizations() {
                removeContextValueSupport(CONTEXT, VALUE1);
            }
        };

        customizer.loadCustomizations();

        assertTrue("Value is not supported after another value is removed", customizer.supports(CONTEXT, VALUE2)); //$NON-NLS-1$
    }

    abstract class FakeProductCustomizer extends AbstractProductCustomizer {
        boolean result = true;

        public String getProductId() {
            return null;
        }

        public IProductCharacteristics getProductCharacteristics() {
            return null;
        }
    }
}
