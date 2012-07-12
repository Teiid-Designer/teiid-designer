package org.teiid.designer.modelgenerator.salesforce.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.modelgenerator.salesforce.model.impl.DataModelImplTest;
import org.teiid.designer.modelgenerator.salesforce.model.impl.SalesforceFieldImplTest;
import org.teiid.designer.modelgenerator.salesforce.model.impl.SalesforceObjectImplTest;
import org.teiid.designer.modelgenerator.salesforce.model.impl.SalesforceRelationshipImplTest;
import org.teiid.designer.modelgenerator.salesforce.ui.wizards.SalesforceImportWizardManagerTest;


@RunWith( Suite.class )
@Suite.SuiteClasses( {SalesforceImportWizardManagerTest.class, SalesforceRelationshipImplTest.class,
   SalesforceObjectImplTest.class, SalesforceFieldImplTest.class, DataModelImplTest.class} )
public class AllTests {
	    // nothing to do
}