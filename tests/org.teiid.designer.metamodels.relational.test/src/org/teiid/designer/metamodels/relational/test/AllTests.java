package org.teiid.designer.metamodels.relational.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.metamodels.relational.aspects.validation.rules.TestColumnNativeTypeRule;
import org.teiid.designer.metamodels.relational.provider.TestForeignKeyAssociationDescriptor;
import org.teiid.designer.metamodels.relational.provider.TestForeignKeyAssociationProvider;
import org.teiid.designer.metamodels.relational.util.TestRelationalTypeMapping;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestColumnNativeTypeRule.class, TestForeignKeyAssociationDescriptor.class,
    TestForeignKeyAssociationProvider.class, TestRelationalTypeMapping.class} )
public class AllTests {
    // nothing to do
}
