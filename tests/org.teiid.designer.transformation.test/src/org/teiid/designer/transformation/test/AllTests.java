package org.teiid.designer.transformation.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.transformation.aspects.validation.rules.TestMappingClassTransformationValidationHelper;
import org.teiid.designer.transformation.metadata.TestPerformanceIndexFilePerRecordType;
import org.teiid.designer.transformation.metadata.TestPerformanceMultipleRecordTypesInOneIndexFile;
import org.teiid.designer.transformation.metadata.TestPerformancePrefixAndPatternLookUp;
import org.teiid.designer.transformation.metadata.TestServerRuntimeMetadata;
import org.teiid.designer.transformation.metadata.TestTransformationMetadata;
import org.teiid.designer.transformation.metadata.TestTransformationMetadataFacade;



@RunWith( Suite.class )
@Suite.SuiteClasses( {TestMappingClassTransformationValidationHelper.class,
					  TestPerformanceIndexFilePerRecordType.class,
					  TestPerformanceMultipleRecordTypesInOneIndexFile.class,
					  TestPerformancePrefixAndPatternLookUp.class,
					  TestServerRuntimeMetadata.class,
					  TestTransformationMetadata.class,
					  TestTransformationMetadataFacade.class} )
public class AllTests {
    // nothing to do
}
