/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;

public class SalesforceImportWizardManagerTest extends TestCase {

    static final String MODEL_NAME = "model_name"; //$NON-NLS-1$
    private SalesforceImportWizardManager man;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        man = new SalesforceImportWizardManager();
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
