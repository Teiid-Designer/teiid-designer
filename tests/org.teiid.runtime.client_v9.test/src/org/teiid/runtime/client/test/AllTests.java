package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.language.TestReservedWords;
import org.teiid.query.resolver.v9.Test9AccessPattern;
import org.teiid.query.resolver.v9.Test9AlterResolving;
import org.teiid.query.resolver.v9.Test9FunctionResolving;
import org.teiid.query.resolver.v9.Test9ProcedureResolving;
import org.teiid.query.resolver.v9.Test9Resolver;
import org.teiid.query.resolver.v9.Test9XMLResolver;
import org.teiid.query.sql.lang.TestCriteriaOperator;
import org.teiid.query.sql.v9.Test9Cloning;
import org.teiid.query.sql.v9.Test9Create;
import org.teiid.query.sql.v9.Test9CreateDrop;
import org.teiid.query.sql.v9.Test9SQLStringVisitor;
import org.teiid.query.sql.v9.TestQuery9Parser;
import org.teiid.query.validator.v9.Test9AlterValidation;
import org.teiid.query.validator.v9.Test9FunctionMetadataValidator;
import org.teiid.query.validator.v9.Test9UpdateValidator;
import org.teiid.query.validator.v9.Test9Validator;
import org.teiid.types.Test9DataTypeManagerService;

@SuppressWarnings( "javadoc" )
@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        // language
                                        TestReservedWords.class,

                                        // query.sql.lang
                                        TestCriteriaOperator.class,

                                        
                                        // query.sql
                                        Test9Cloning.class,
                                        Test9Create.class,
                                        Test9CreateDrop.class,
                                        TestQuery9Parser.class,
                                        Test9SQLStringVisitor.class,

                                        // query.resolver
                                        Test9Resolver.class,
                                        Test9AccessPattern.class,
                                        Test9AlterResolving.class,
                                        Test9FunctionResolving.class,
                                        Test9ProcedureResolving.class,
                                        Test9XMLResolver.class,

                                        // query.validator

                                        Test9AlterValidation.class,
                                        Test9FunctionMetadataValidator.class,
                                        Test9Validator.class,
                                        Test9UpdateValidator.class,

                                        // types
                                        Test9DataTypeManagerService.class
                                    } )
public class AllTests {
    // nothing to do
}
