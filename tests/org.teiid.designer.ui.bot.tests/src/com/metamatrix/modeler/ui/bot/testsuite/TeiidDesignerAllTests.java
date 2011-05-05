package com.metamatrix.modeler.ui.bot.testsuite;


import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.metamatrix.modeler.ui.bot.testcase.ModelWizardTest;
import com.metamatrix.modeler.ui.bot.testcase.VirtualGroupTutorialTest;


/**
 * 
 * @author psrna
 *
 */
@SuiteClasses({
	ModelWizardTest.class,
	//DDLExportTest.class
	VirtualGroupTutorialTest.class
	//TeiidSourceInSeamTest.class,
	//TeiidSourceInHibernateToolsTest.class
	})
@RunWith(RequirementAwareSuite.class)
public class TeiidDesignerAllTests {

}
