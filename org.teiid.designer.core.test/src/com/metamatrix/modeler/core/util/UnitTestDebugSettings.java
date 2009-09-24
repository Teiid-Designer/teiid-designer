/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import com.metamatrix.modeler.core.ModelerCore;

/**
 * The UnitTestDebugSettings is a simple utility for test cases that caches the ModelerCore's debug
 * settings and that can then reapply those same settings.  This is useful when a test case is to
 * turn on (or off) some debugging, yet upon clean-up (i.e., tearDown) reset the debug settings to
 * the state that the test case found them.
 * <p>
 * It can be used in a {@link junit.framework.TestCase TestCase} class as follows:
 * <code>
 *     private static UnitTestDebugSettings DEBUG_SETTINGS = new UnitTestDebugSettings();
 * </code>
 * and then in the one-time setup and tear-down methods:
 * <code>
 *     public static Test suite() {
 *         TestSuite suite = new TestSuite("TestModelBufferManager"); //$NON-NLS-1$
 *         suite.addTestSuite(TestModelBufferManager.class);
 *         // One-time setup and teardown
 *         return new TestSetup(suite) {
 *             public void setUp() {
 *                 DEBUG_SETTINGS.acquire();    // record current settings
 *                 // Make any changes to the settings ...
 *                 ModelerCore.DEBUG_MODEL_WORKSPACE = true;
 *             }
 *             public void tearDown() {
 *                 DEBUG_SETTINGS.reset();      // undo the changes made here
 *             }
 *         };
 *     }
 * </code>
 * </p>
 */
public class UnitTestDebugSettings {
    
    private boolean debug;
    private boolean debug_metamodel;
    private boolean debug_model_workspace;
    private boolean debug_notification;
    private boolean debug_transaction;
    private boolean debug_validation;

    /**
     * Construct an instance of UnitTestDebugSettings.
     * 
     */
    public UnitTestDebugSettings() {
        super();
    }
    
    public void acquire() {
        debug                   = ModelerCore.DEBUG;
        debug_metamodel         = ModelerCore.DEBUG_METAMODEL;
        debug_model_workspace   = ModelerCore.DEBUG_MODEL_WORKSPACE;
        debug_notification      = ModelerCore.DEBUG_NOTIFICATIONS;
        debug_transaction       = ModelerCore.DEBUG_TRANSACTION;
        debug_validation        = ModelerCore.DEBUG_VALIDATION;
    }
    
    public void reset() {
        ModelerCore.DEBUG                   = debug;
        ModelerCore.DEBUG_METAMODEL         = debug_metamodel;
        ModelerCore.DEBUG_MODEL_WORKSPACE   = debug_model_workspace;
        ModelerCore.DEBUG_NOTIFICATIONS     = debug_notification;
        ModelerCore.DEBUG_TRANSACTION       = debug_transaction;
        ModelerCore.DEBUG_VALIDATION        = debug_validation;
    }

}
