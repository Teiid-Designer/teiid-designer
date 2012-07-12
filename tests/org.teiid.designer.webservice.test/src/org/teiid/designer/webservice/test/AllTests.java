package org.teiid.designer.webservice.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.webservice.TestAbstractWebServiceResource;
import org.teiid.designer.webservice.TestWebServiceModelGenerator;
import org.teiid.designer.webservice.TestWebServiceResources;
import org.teiid.designer.webservice.gen.TestBasicWsdlGenerator;
import org.teiid.designer.webservice.procedure.LocalTestXsdInstanceNode;
import org.teiid.designer.webservice.procedure.TestProcedureCriteriaMappingFactory;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestProcedureCriteriaMappingFactory.class, LocalTestXsdInstanceNode.class, TestBasicWsdlGenerator.class,
    TestWebServiceResources.class, TestWebServiceModelGenerator.class, TestAbstractWebServiceResource.class} )
public class AllTests {
    // nothing to do
}
