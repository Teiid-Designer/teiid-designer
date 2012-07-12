package org.teiid.designer.modelgenerator.wsdl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.modelgenerator.wsdl.RelationalModelBuilderTest;
import org.teiid.designer.modelgenerator.wsdl.WSDLReaderTest;
import org.teiid.designer.modelgenerator.wsdl.model.ModelTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.BindingImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.FaultImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.MessageImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.ModelBuilderTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.OperationImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.PartImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.PortImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.ServiceImplTest;
import org.teiid.designer.modelgenerator.wsdl.model.impl.WSDLElementImplTest;
import org.teiid.designer.modelgenerator.wsdl.validation.WSDLValidationExceptionTest;
import org.teiid.designer.modelgenerator.wsdl.validation.internal.WSDLValidatorImplTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {WSDLValidatorImplTest.class, WSDLValidationExceptionTest.class, WSDLElementImplTest.class,
    ServiceImplTest.class, PortImplTest.class, PartImplTest.class, OperationImplTest.class, ModelBuilderTest.class,
    MessageImplTest.class, FaultImplTest.class, BindingImplTest.class, ModelTest.class, WSDLReaderTest.class,
    RelationalModelBuilderTest.class} )
public class AllTests {
    // nothing to do
}
