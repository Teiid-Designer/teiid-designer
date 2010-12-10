package org.teiid.designer.transformation.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.metamatrix.modeler.internal.transformation.util.TestInputSetPramReplacementVisitor;
import com.metamatrix.modeler.transformation.aspects.validation.rules.TestMappingClassTransformationValidationHelper;
import com.metamatrix.modeler.transformation.metadata.TestPerformanceIndexFilePerRecordType;
import com.metamatrix.modeler.transformation.metadata.TestPerformanceMultipleRecordTypesInOneIndexFile;
import com.metamatrix.modeler.transformation.metadata.TestPerformancePrefixAndPatternLookUp;
import com.metamatrix.modeler.transformation.metadata.TestServerRuntimeMetadata;
import com.metamatrix.modeler.transformation.metadata.TestTransformationMetadata;
import com.metamatrix.modeler.transformation.metadata.TestTransformationMetadataFacade;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestInputSetPramReplacementVisitor.class,
					  TestMappingClassTransformationValidationHelper.class,
					  TestPerformanceIndexFilePerRecordType.class,
					  TestPerformanceMultipleRecordTypesInOneIndexFile.class,
					  TestPerformancePrefixAndPatternLookUp.class,
					  TestServerRuntimeMetadata.class,
					  TestTransformationMetadata.class,
					  TestTransformationMetadataFacade.class} )
public class AllTests {
    // nothing to do
}
