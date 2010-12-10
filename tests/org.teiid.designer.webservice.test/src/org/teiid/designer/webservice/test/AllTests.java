package org.teiid.designer.webservice.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.internal.webservice.TestAbstractWebServiceResource;
import com.metamatrix.modeler.internal.webservice.TestWebServiceModelGenerator;
import com.metamatrix.modeler.internal.webservice.TestWebServiceResources;
import com.metamatrix.modeler.internal.webservice.gen.TestBasicWsdlGenerator;
import com.metamatrix.modeler.webservice.procedure.LocalTestXsdInstanceNode;
import com.metamatrix.modeler.webservice.procedure.TestProcedureCriteriaMappingFactory;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestProcedureCriteriaMappingFactory.class, LocalTestXsdInstanceNode.class, TestBasicWsdlGenerator.class,
    TestWebServiceResources.class, TestWebServiceModelGenerator.class, TestAbstractWebServiceResource.class} )
public class AllTests {
    // nothing to do
}
