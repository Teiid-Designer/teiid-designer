package org.teiid.designer.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.core.TestModelerCore;
import com.metamatrix.modeler.core.compare.TestFakeMappableObject;
import com.metamatrix.modeler.core.compare.TestMappingProducer;
import com.metamatrix.modeler.core.metamodel.TestMultiplicity;
import com.metamatrix.modeler.core.metamodel.aspect.TestAbstractMetamodelAspect;
import com.metamatrix.modeler.core.transaction.TestTransactionStateConstants;
import com.metamatrix.modeler.core.types.TestDatatypeConstants;
import com.metamatrix.modeler.core.util.StringUtilitiesTest;
import com.metamatrix.modeler.core.util.TestColumnRecordComparator;
import com.metamatrix.modeler.core.util.TestIoUtilities;
import com.metamatrix.modeler.core.util.TestModelStatistics;
import com.metamatrix.modeler.core.util.TestModelStatisticsVisitor;
import com.metamatrix.modeler.core.util.TestPrimaryMetamodelStatisticsVisitor;
import com.metamatrix.modeler.core.util.TestUriValidator;
import com.metamatrix.modeler.core.validation.TestValidationContext;
import com.metamatrix.modeler.core.validation.rules.TestCoreValidationRulesUtil;
import com.metamatrix.modeler.core.validation.rules.TestStringNameValidator;
import com.metamatrix.modeler.core.workspace.TestModelWorkspaceSelections;
import com.metamatrix.modeler.internal.core.TestModelEditor;
import com.metamatrix.modeler.internal.core.container.TestAbstractContainer;
import com.metamatrix.modeler.internal.core.container.TestAbstractProxyContainer;
import com.metamatrix.modeler.internal.core.container.TestDefaultContainerResultSetFinder;
import com.metamatrix.modeler.internal.core.container.TestDefaultResourceFinder;
import com.metamatrix.modeler.internal.core.container.TestResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.index.TestCreateIndexFile;
import com.metamatrix.modeler.internal.core.index.TestResourceFileIndexSelector;
import com.metamatrix.modeler.internal.core.index.TestRuntimeIndexSelector;
import com.metamatrix.modeler.internal.core.index.TestWordEntryComparator;
import com.metamatrix.modeler.internal.core.metadata.runtime.TestMetadataRecord;
import com.metamatrix.modeler.internal.core.metadata.runtime.TestRuntimeAdapter;
import com.metamatrix.modeler.internal.core.search.runtime.TestSearchRuntimeAdapter;
import com.metamatrix.modeler.internal.core.transaction.TestSourcedNotificationImpl;
import com.metamatrix.modeler.internal.core.transaction.TestUnitOfWorkProviderImpl;
import com.metamatrix.modeler.internal.core.util.TestBasicUriPathConverter;
import com.metamatrix.modeler.internal.core.util.TestFlatRegistry;
import com.metamatrix.modeler.internal.core.util.TestOverflowingLRUCache;
import com.metamatrix.modeler.internal.core.validation.TestValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.TestValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelperTest;
import com.metamatrix.modeler.internal.core.workspace.TestDotProjectUtil;
import com.metamatrix.modeler.internal.core.workspace.TestModelBufferCache;
import com.metamatrix.modeler.internal.core.workspace.TestModelBufferImpl;
import com.metamatrix.modeler.internal.core.workspace.TestModelBufferManager;
import com.metamatrix.modeler.internal.core.workspace.TestModelProjectImpl;
import com.metamatrix.modeler.internal.core.workspace.TestModelProjectInfo;
import com.metamatrix.modeler.internal.core.workspace.TestModelResourceInfo;
import com.metamatrix.modeler.internal.core.workspace.TestModelStatusImpl;
import com.metamatrix.modeler.internal.core.workspace.TestModelUtil;
import com.metamatrix.modeler.internal.core.workspace.TestModelWorkspaceCache;
import com.metamatrix.modeler.internal.core.workspace.TestModelWorkspaceImpl;
import com.metamatrix.modeler.internal.core.workspace.TestModelWorkspaceInfo;
import com.metamatrix.modeler.internal.core.workspace.TestModelWorkspaceItemCache;
import com.metamatrix.modeler.internal.core.workspace.TestModelWorkspaceItemInfo;
import com.metamatrix.modeler.internal.core.workspace.TestOpenableModelWorkspaceItemInfo;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestOpenableModelWorkspaceItemInfo.class, TestModelWorkspaceItemInfo.class,
    TestModelWorkspaceItemCache.class, TestModelWorkspaceInfo.class, TestModelWorkspaceImpl.class, TestModelWorkspaceCache.class,
    TestModelUtil.class, TestModelStatusImpl.class, TestModelResourceInfo.class, TestModelProjectInfo.class,
    TestModelProjectImpl.class, TestModelBufferManager.class, TestModelBufferImpl.class, TestModelBufferCache.class,
    TestDotProjectUtil.class, ResourceAnnotationHelperTest.class, TestValidationResultImpl.class,
    TestValidationProblemImpl.class, TestOverflowingLRUCache.class, TestFlatRegistry.class, TestBasicUriPathConverter.class,
    TestUnitOfWorkProviderImpl.class, TestSourcedNotificationImpl.class, TestSearchRuntimeAdapter.class,
    TestRuntimeAdapter.class, TestMetadataRecord.class, TestWordEntryComparator.class, TestRuntimeIndexSelector.class,
    TestResourceFileIndexSelector.class, TestCreateIndexFile.class, TestResourceDescriptorImpl.class,
    TestDefaultResourceFinder.class, TestDefaultContainerResultSetFinder.class, TestAbstractProxyContainer.class,
    TestAbstractContainer.class, TestModelEditor.class, TestModelWorkspaceSelections.class, TestStringNameValidator.class,
    TestCoreValidationRulesUtil.class, TestValidationContext.class, TestUriValidator.class,
    TestPrimaryMetamodelStatisticsVisitor.class, TestModelStatisticsVisitor.class, TestModelStatistics.class,
    TestIoUtilities.class, TestColumnRecordComparator.class, StringUtilitiesTest.class, TestDatatypeConstants.class,
    TestMultiplicity.class, TestMappingProducer.class, TestFakeMappableObject.class, TestModelerCore.class,
    TestTransactionStateConstants.class, TestAbstractMetamodelAspect.class,} )
public class AllTests {
    // nothing to do
}
