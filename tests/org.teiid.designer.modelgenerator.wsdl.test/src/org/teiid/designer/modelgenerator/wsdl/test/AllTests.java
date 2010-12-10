package org.teiid.designer.modelgenerator.wsdl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.modelgenerator.wsdl.RelationalModelBuilderTest;
import com.metamatrix.modeler.modelgenerator.wsdl.WSDLReaderTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.FaultImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilderTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.OperationImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PortImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ServiceImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.WSDLElementImplTest;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationExceptionTest;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.internal.WSDLValidatorImplTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {WSDLValidatorImplTest.class, WSDLValidationExceptionTest.class, WSDLElementImplTest.class,
    ServiceImplTest.class, PortImplTest.class, PartImplTest.class, OperationImplTest.class, ModelBuilderTest.class,
    MessageImplTest.class, FaultImplTest.class, BindingImplTest.class, ModelTest.class, WSDLReaderTest.class,
    RelationalModelBuilderTest.class} )
public class AllTests {
    // nothing to do
}
