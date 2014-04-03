package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.language.TestReservedWords;
import org.teiid.query.resolver.v7.Test7AccessPattern;
import org.teiid.query.resolver.v7.Test7AlterResolving;
import org.teiid.query.resolver.v7.Test7FunctionResolving;
import org.teiid.query.resolver.v7.Test7ProcedureResolving;
import org.teiid.query.resolver.v7.Test7Resolver;
import org.teiid.query.resolver.v7.Test7XMLResolver;
import org.teiid.query.resolver.v8.Test8AccessPattern;
import org.teiid.query.resolver.v8.Test8AlterResolving;
import org.teiid.query.resolver.v8.Test8FunctionResolving;
import org.teiid.query.resolver.v8.Test8ProcedureResolving;
import org.teiid.query.resolver.v8.Test8Resolver;
import org.teiid.query.resolver.v8.Test8XMLResolver;
import org.teiid.query.resolver.v85.Test85AccessPattern;
import org.teiid.query.resolver.v85.Test85AlterResolving;
import org.teiid.query.resolver.v85.Test85FunctionResolving;
import org.teiid.query.resolver.v85.Test85ProcedureResolving;
import org.teiid.query.resolver.v85.Test85Resolver;
import org.teiid.query.resolver.v85.Test85XMLResolver;
import org.teiid.query.sql.lang.TestCriteriaOperator;
import org.teiid.query.sql.v7.Test7Cloning;
import org.teiid.query.sql.v7.Test7Create;
import org.teiid.query.sql.v7.Test7CreateDrop;
import org.teiid.query.sql.v7.Test7SQLStringVisitor;
import org.teiid.query.sql.v7.TestQuery7Parser;
import org.teiid.query.sql.v8.Test8Cloning;
import org.teiid.query.sql.v8.Test8Create;
import org.teiid.query.sql.v8.Test8CreateDrop;
import org.teiid.query.sql.v8.Test8SQLStringVisitor;
import org.teiid.query.sql.v8.TestQuery8Parser;
import org.teiid.query.sql.v85.Test85Cloning;
import org.teiid.query.sql.v85.Test85Create;
import org.teiid.query.sql.v85.Test85CreateDrop;
import org.teiid.query.sql.v85.Test85SQLStringVisitor;
import org.teiid.query.sql.v85.TestQuery85Parser;
import org.teiid.query.validator.v7.Test7AlterValidation;
import org.teiid.query.validator.v7.Test7FunctionMetadataValidator;
import org.teiid.query.validator.v7.Test7UpdateValidator;
import org.teiid.query.validator.v7.Test7Validator;
import org.teiid.query.validator.v8.Test8AlterValidation;
import org.teiid.query.validator.v8.Test8FunctionMetadataValidator;
import org.teiid.query.validator.v8.Test8UpdateValidator;
import org.teiid.query.validator.v8.Test8Validator;
import org.teiid.query.validator.v85.Test85AlterValidation;
import org.teiid.query.validator.v85.Test85FunctionMetadataValidator;
import org.teiid.query.validator.v85.Test85UpdateValidator;
import org.teiid.query.validator.v85.Test85Validator;
import org.teiid.types.Test7DataTypeManagerService;
import org.teiid.types.Test8DataTypeManagerService;

@SuppressWarnings( "javadoc" )
@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        // language
                                        TestReservedWords.class,

                                        // query.sql.lang
                                        TestCriteriaOperator.class,

                                        // query.sql
                                        Test7Cloning.class,
                                        Test7Create.class,
                                        Test7CreateDrop.class,
                                        TestQuery7Parser.class,
                                        Test7SQLStringVisitor.class,
                                        Test8Cloning.class,
                                        Test8Create.class,
                                        Test8CreateDrop.class,
                                        TestQuery8Parser.class,
                                        Test8SQLStringVisitor.class,
                                        Test85Cloning.class,
                                        Test85Create.class,
                                        Test85CreateDrop.class,
                                        TestQuery85Parser.class,
                                        Test85SQLStringVisitor.class,

                                        // query.resolver
                                        Test7Resolver.class,
                                        Test7AccessPattern.class,
                                        Test7AlterResolving.class,
                                        Test7FunctionResolving.class,
                                        Test7ProcedureResolving.class,
                                        Test7XMLResolver.class,
                                        Test8Resolver.class,
                                        Test8AccessPattern.class,
                                        Test8AlterResolving.class,
                                        Test8FunctionResolving.class,
                                        Test8ProcedureResolving.class,
                                        Test8XMLResolver.class,
                                        Test85Resolver.class,
                                        Test85AccessPattern.class,
                                        Test85AlterResolving.class,
                                        Test85FunctionResolving.class,
                                        Test85ProcedureResolving.class,
                                        Test85XMLResolver.class,

                                        // query.validator
                                        Test7AlterValidation.class,
                                        Test7FunctionMetadataValidator.class,
                                        Test7Validator.class,
                                        Test7UpdateValidator.class,
                                        Test8AlterValidation.class,
                                        Test8FunctionMetadataValidator.class,
                                        Test8Validator.class,
                                        Test8UpdateValidator.class,
                                        Test85AlterValidation.class,
                                        Test85FunctionMetadataValidator.class,
                                        Test85Validator.class,
                                        Test85UpdateValidator.class,

                                        // types
                                        Test7DataTypeManagerService.class,
                                        Test8DataTypeManagerService.class
                                    } )
public class AllTests {
    // nothing to do
}
