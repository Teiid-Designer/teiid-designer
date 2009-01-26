/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import junit.framework.TestCase;
import org.eclipse.core.runtime.NullProgressMonitor;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;

public class SalesforceImportWizardManagerTest extends TestCase {

    static final String MODEL_NAME = "model_name"; //$NON-NLS-1$

    String username = "jdoyleoss@gmail.com"; //$NON-NLS-1$
    String password = "l3tm31nNZ4loJCls59GlDr4sZLB8N4TT"; //$NON-NLS-1$
    private SalesforceImportWizardManager man;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        man = new SalesforceImportWizardManager();
    }

    public void testCredentialsValid() {
        man.setPassword(password);
        man.setUsername(username);
        try {
            assertTrue(man.validateCredentials(new NullProgressMonitor()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testCredentialsBadUser() {
        man.setPassword(password);
        man.setUsername("bogus"); //$NON-NLS-1$
        try {
            assertTrue(man.validateCredentials(new NullProgressMonitor()));
            fail("should have thrown an exception"); //$NON-NLS-1$
        } catch (Exception e) {
            return;
        }
    }

    public void testCredentialsBadPass() {
        man.setPassword("bogus"); //$NON-NLS-1$
        man.setUsername(username);
        try {
            assertTrue(man.validateCredentials(new NullProgressMonitor()));
            fail("should have thrown an exception"); //$NON-NLS-1$
        } catch (Exception e) {
            return;
        }
    }

    public void testGetDataModel() {
        man.setPassword(password);
        man.setUsername(username);
        man.setTargetModelName(MODEL_NAME);
        try {
            DataModel model = man.createDataModel(new NullProgressMonitor());
            assertEquals(109, model.getSalesforceObjects().length);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGetSetModelName() {
        assertEquals(null, man.getTargetModelName());
        man.setTargetModelName(MODEL_NAME);
        try {
            assertEquals(MODEL_NAME, man.getTargetModelName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
