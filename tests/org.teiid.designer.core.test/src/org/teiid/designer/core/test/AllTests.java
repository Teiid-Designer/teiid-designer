package org.teiid.designer.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.core.TestModelEditor;
import org.teiid.designer.core.TestModelerCore;
import org.teiid.designer.core.compare.TestFakeMappableObject;
import org.teiid.designer.core.compare.TestMappingProducer;
import org.teiid.designer.core.container.TestAbstractContainer;
import org.teiid.designer.core.container.TestAbstractProxyContainer;
import org.teiid.designer.core.container.TestDefaultContainerResultSetFinder;
import org.teiid.designer.core.container.TestDefaultResourceFinder;
import org.teiid.designer.core.container.TestResourceDescriptorImpl;
import org.teiid.designer.core.index.TestCreateIndexFile;
import org.teiid.designer.core.index.TestResourceFileIndexSelector;
import org.teiid.designer.core.index.TestRuntimeIndexSelector;
import org.teiid.designer.core.index.TestWordEntryComparator;
import org.teiid.designer.core.metadata.runtime.TestMetadataRecord;
import org.teiid.designer.core.metadata.runtime.TestRuntimeAdapter;
import org.teiid.designer.core.metamodel.TestMultiplicity;
import org.teiid.designer.core.metamodel.aspect.TestAbstractMetamodelAspect;
import org.teiid.designer.core.search.runtime.TestSearchRuntimeAdapter;
import org.teiid.designer.core.transaction.TestSourcedNotificationImpl;
import org.teiid.designer.core.transaction.TestTransactionStateConstants;
import org.teiid.designer.core.transaction.TestUnitOfWorkProviderImpl;
import org.teiid.designer.core.types.TestDatatypeConstants;
import org.teiid.designer.core.util.StringUtilitiesTest;
import org.teiid.designer.core.util.TestBasicUriPathConverter;
import org.teiid.designer.core.util.TestColumnRecordComparator;
import org.teiid.designer.core.util.TestFlatRegistry;
import org.teiid.designer.core.util.TestIoUtilities;
import org.teiid.designer.core.util.TestModelStatistics;
import org.teiid.designer.core.util.TestModelStatisticsVisitor;
import org.teiid.designer.core.util.TestOverflowingLRUCache;
import org.teiid.designer.core.util.TestPrimaryMetamodelStatisticsVisitor;
import org.teiid.designer.core.util.TestUriValidator;
import org.teiid.designer.core.validation.TestValidationContext;
import org.teiid.designer.core.validation.TestValidationProblemImpl;
import org.teiid.designer.core.validation.TestValidationResultImpl;
import org.teiid.designer.core.validation.rules.TestCoreValidationRulesUtil;
import org.teiid.designer.core.validation.rules.TestStringNameValidator;
import org.teiid.designer.core.workspace.ResourceAnnotationHelperTest;
import org.teiid.designer.core.workspace.TestDotProjectUtil;
import org.teiid.designer.core.workspace.TestModelBufferCache;
import org.teiid.designer.core.workspace.TestModelBufferImpl;
import org.teiid.designer.core.workspace.TestModelBufferManager;
import org.teiid.designer.core.workspace.TestModelProjectImpl;
import org.teiid.designer.core.workspace.TestModelProjectInfo;
import org.teiid.designer.core.workspace.TestModelResourceInfo;
import org.teiid.designer.core.workspace.TestModelStatusImpl;
import org.teiid.designer.core.workspace.TestModelUtil;
import org.teiid.designer.core.workspace.TestModelWorkspaceCache;
import org.teiid.designer.core.workspace.TestModelWorkspaceImpl;
import org.teiid.designer.core.workspace.TestModelWorkspaceInfo;
import org.teiid.designer.core.workspace.TestModelWorkspaceItemCache;
import org.teiid.designer.core.workspace.TestModelWorkspaceItemInfo;
import org.teiid.designer.core.workspace.TestModelWorkspaceSelections;
import org.teiid.designer.core.workspace.TestOpenableModelWorkspaceItemInfo;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtilTest;


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
    TestTransactionStateConstants.class, TestAbstractMetamodelAspect.class, WorkspaceResourceFinderUtilTest.class} )
public class AllTests {
    // nothing to do
}
