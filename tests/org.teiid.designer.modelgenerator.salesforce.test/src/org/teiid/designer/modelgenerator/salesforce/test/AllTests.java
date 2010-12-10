package org.teiid.designer.modelgenerator.salesforce.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModelImplTest;
import com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceFieldImplTest;
import com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceObjectImplTest;
import com.metamatrix.modeler.modelgenerator.salesforce.model.impl.SalesforceRelationshipImplTest;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards.SalesforceImportWizardManagerTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {SalesforceImportWizardManagerTest.class, SalesforceRelationshipImplTest.class,
   SalesforceObjectImplTest.class, SalesforceFieldImplTest.class, DataModelImplTest.class} )
public class AllTests {
	    // nothing to do
}