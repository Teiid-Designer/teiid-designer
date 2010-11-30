package org.teiid.designer.metamodels.relational.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.metamodels.relational.aspects.validation.rules.TestColumnNativeTypeRule;
import com.metamatrix.metamodels.relational.provider.TestForeignKeyAssociationDescriptor;
import com.metamatrix.metamodels.relational.provider.TestForeignKeyAssociationProvider;
import com.metamatrix.metamodels.relational.util.TestRelationalTypeMapping;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestColumnNativeTypeRule.class, TestForeignKeyAssociationDescriptor.class,
    TestForeignKeyAssociationProvider.class, TestRelationalTypeMapping.class} )
public class AllTests {
    // nothing to do
}
