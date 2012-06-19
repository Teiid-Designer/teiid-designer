package org.teiid.designer.ui.bot.test.suite;


import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.teiid.designer.ui.bot.test.ModelWizardTest;
import org.teiid.designer.ui.bot.test.VirtualGroupTutorialTest;



/**
 * 
 * @author psrna
 *
 */
@SuiteClasses({
	ModelWizardTest.class,
	VirtualGroupTutorialTest.class
	//TeiidSourceInSeamTest.class
	//TeiidSourceInHibernateToolsTest.class
	})
@RunWith(RequirementAwareSuite.class)
public class TeiidDesignerAllTests {

}
