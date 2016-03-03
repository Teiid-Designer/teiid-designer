package org.teiid.runtime.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.language.TestReservedWords;
import org.teiid.query.metadata.v8.Test8MetadataValidator;
import org.teiid.query.metadata.v810.Test810MetadataValidator;
import org.teiid.query.metadata.v811.Test811MetadataValidator;
import org.teiid.query.metadata.v85.Test85MetadataValidator;
import org.teiid.query.metadata.v86.Test86MetadataValidator;
import org.teiid.query.metadata.v87.Test87MetadataValidator;
import org.teiid.query.metadata.v88.Test88MetadataValidator;
import org.teiid.query.metadata.v89.Test89MetadataValidator;
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
import org.teiid.query.resolver.v810.Test810AccessPattern;
import org.teiid.query.resolver.v810.Test810AlterResolving;
import org.teiid.query.resolver.v810.Test810FunctionResolving;
import org.teiid.query.resolver.v810.Test810ProcedureResolving;
import org.teiid.query.resolver.v810.Test810Resolver;
import org.teiid.query.resolver.v810.Test810XMLResolver;
import org.teiid.query.resolver.v811.Test811AccessPattern;
import org.teiid.query.resolver.v811.Test811AlterResolving;
import org.teiid.query.resolver.v811.Test811FunctionResolving;
import org.teiid.query.resolver.v811.Test811ProcedureResolving;
import org.teiid.query.resolver.v811.Test811Resolver;
import org.teiid.query.resolver.v811.Test811XMLResolver;
import org.teiid.query.resolver.v8124.Test8124AccessPattern;
import org.teiid.query.resolver.v8124.Test8124AlterResolving;
import org.teiid.query.resolver.v8124.Test8124FunctionResolving;
import org.teiid.query.resolver.v8124.Test8124ProcedureResolving;
import org.teiid.query.resolver.v8124.Test8124Resolver;
import org.teiid.query.resolver.v8124.Test8124XMLResolver;
import org.teiid.query.resolver.v85.Test85AccessPattern;
import org.teiid.query.resolver.v85.Test85AlterResolving;
import org.teiid.query.resolver.v85.Test85FunctionResolving;
import org.teiid.query.resolver.v85.Test85ProcedureResolving;
import org.teiid.query.resolver.v85.Test85Resolver;
import org.teiid.query.resolver.v85.Test85XMLResolver;
import org.teiid.query.resolver.v86.Test86AccessPattern;
import org.teiid.query.resolver.v86.Test86AlterResolving;
import org.teiid.query.resolver.v86.Test86FunctionResolving;
import org.teiid.query.resolver.v86.Test86ProcedureResolving;
import org.teiid.query.resolver.v86.Test86Resolver;
import org.teiid.query.resolver.v86.Test86XMLResolver;
import org.teiid.query.resolver.v87.Test87AccessPattern;
import org.teiid.query.resolver.v87.Test87AlterResolving;
import org.teiid.query.resolver.v87.Test87FunctionResolving;
import org.teiid.query.resolver.v87.Test87ProcedureResolving;
import org.teiid.query.resolver.v87.Test87Resolver;
import org.teiid.query.resolver.v87.Test87XMLResolver;
import org.teiid.query.resolver.v88.Test88AccessPattern;
import org.teiid.query.resolver.v88.Test88AlterResolving;
import org.teiid.query.resolver.v88.Test88FunctionResolving;
import org.teiid.query.resolver.v88.Test88ProcedureResolving;
import org.teiid.query.resolver.v88.Test88Resolver;
import org.teiid.query.resolver.v88.Test88XMLResolver;
import org.teiid.query.resolver.v89.Test89AccessPattern;
import org.teiid.query.resolver.v89.Test89AlterResolving;
import org.teiid.query.resolver.v89.Test89FunctionResolving;
import org.teiid.query.resolver.v89.Test89ProcedureResolving;
import org.teiid.query.resolver.v89.Test89Resolver;
import org.teiid.query.resolver.v89.Test89XMLResolver;
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
import org.teiid.query.sql.v810.Test810Cloning;
import org.teiid.query.sql.v810.Test810Create;
import org.teiid.query.sql.v810.Test810CreateDrop;
import org.teiid.query.sql.v810.Test810SQLStringVisitor;
import org.teiid.query.sql.v810.TestQuery810Parser;
import org.teiid.query.sql.v811.Test811Cloning;
import org.teiid.query.sql.v811.Test811Create;
import org.teiid.query.sql.v811.Test811CreateDrop;
import org.teiid.query.sql.v811.Test811SQLStringVisitor;
import org.teiid.query.sql.v811.TestQuery811Parser;
import org.teiid.query.sql.v8124.Test8124Cloning;
import org.teiid.query.sql.v8124.Test8124Create;
import org.teiid.query.sql.v8124.Test8124CreateDrop;
import org.teiid.query.sql.v8124.Test8124SQLStringVisitor;
import org.teiid.query.sql.v8124.TestQuery8124Parser;
import org.teiid.query.sql.v84.TestQuery84Parser;
import org.teiid.query.sql.v85.Test85Cloning;
import org.teiid.query.sql.v85.Test85Create;
import org.teiid.query.sql.v85.Test85CreateDrop;
import org.teiid.query.sql.v85.Test85SQLStringVisitor;
import org.teiid.query.sql.v85.TestQuery85Parser;
import org.teiid.query.sql.v86.Test86Cloning;
import org.teiid.query.sql.v86.Test86Create;
import org.teiid.query.sql.v86.Test86CreateDrop;
import org.teiid.query.sql.v86.Test86SQLStringVisitor;
import org.teiid.query.sql.v86.TestQuery86Parser;
import org.teiid.query.sql.v87.Test87Cloning;
import org.teiid.query.sql.v87.Test87Create;
import org.teiid.query.sql.v87.Test87CreateDrop;
import org.teiid.query.sql.v87.Test87SQLStringVisitor;
import org.teiid.query.sql.v87.TestQuery87Parser;
import org.teiid.query.sql.v88.Test88Cloning;
import org.teiid.query.sql.v88.Test88Create;
import org.teiid.query.sql.v88.Test88CreateDrop;
import org.teiid.query.sql.v88.Test88SQLStringVisitor;
import org.teiid.query.sql.v88.TestQuery88Parser;
import org.teiid.query.sql.v89.Test89Cloning;
import org.teiid.query.sql.v89.Test89Create;
import org.teiid.query.sql.v89.Test89CreateDrop;
import org.teiid.query.sql.v89.Test89SQLStringVisitor;
import org.teiid.query.sql.v89.TestQuery89Parser;
import org.teiid.query.validator.v7.Test7AlterValidation;
import org.teiid.query.validator.v7.Test7FunctionMetadataValidator;
import org.teiid.query.validator.v7.Test7UpdateValidator;
import org.teiid.query.validator.v7.Test7Validator;
import org.teiid.query.validator.v8.Test8AlterValidation;
import org.teiid.query.validator.v8.Test8FunctionMetadataValidator;
import org.teiid.query.validator.v8.Test8UpdateValidator;
import org.teiid.query.validator.v8.Test8Validator;
import org.teiid.query.validator.v810.Test810AlterValidation;
import org.teiid.query.validator.v810.Test810FunctionMetadataValidator;
import org.teiid.query.validator.v810.Test810UpdateValidator;
import org.teiid.query.validator.v810.Test810Validator;
import org.teiid.query.validator.v811.Test811AlterValidation;
import org.teiid.query.validator.v811.Test811FunctionMetadataValidator;
import org.teiid.query.validator.v811.Test811UpdateValidator;
import org.teiid.query.validator.v811.Test811Validator;
import org.teiid.query.validator.v8124.Test8124AlterValidation;
import org.teiid.query.validator.v8124.Test8124FunctionMetadataValidator;
import org.teiid.query.validator.v8124.Test8124UpdateValidator;
import org.teiid.query.validator.v8124.Test8124Validator;
import org.teiid.query.validator.v85.Test85AlterValidation;
import org.teiid.query.validator.v85.Test85FunctionMetadataValidator;
import org.teiid.query.validator.v85.Test85UpdateValidator;
import org.teiid.query.validator.v85.Test85Validator;
import org.teiid.query.validator.v86.Test86AlterValidation;
import org.teiid.query.validator.v86.Test86FunctionMetadataValidator;
import org.teiid.query.validator.v86.Test86UpdateValidator;
import org.teiid.query.validator.v86.Test86Validator;
import org.teiid.query.validator.v87.Test87AlterValidation;
import org.teiid.query.validator.v87.Test87FunctionMetadataValidator;
import org.teiid.query.validator.v87.Test87UpdateValidator;
import org.teiid.query.validator.v87.Test87Validator;
import org.teiid.query.validator.v88.Test88AlterValidation;
import org.teiid.query.validator.v88.Test88FunctionMetadataValidator;
import org.teiid.query.validator.v88.Test88UpdateValidator;
import org.teiid.query.validator.v88.Test88Validator;
import org.teiid.query.validator.v89.Test89AlterValidation;
import org.teiid.query.validator.v89.Test89FunctionMetadataValidator;
import org.teiid.query.validator.v89.Test89UpdateValidator;
import org.teiid.query.validator.v89.Test89Validator;
import org.teiid.types.Test7DataTypeManagerService;
import org.teiid.types.Test8DataTypeManagerService;

@SuppressWarnings( "javadoc" )
@RunWith( Suite.class )
@Suite.SuiteClasses( {
                                        // language
                                        TestReservedWords.class,

                                        // query.sql.lang
                                        TestCriteriaOperator.class,

                                        // query.metadata
                                        Test8MetadataValidator.class,
                                        Test85MetadataValidator.class,
                                        Test86MetadataValidator.class,
                                        Test87MetadataValidator.class,
                                        Test88MetadataValidator.class,
                                        Test89MetadataValidator.class,
                                        Test810MetadataValidator.class,
                                        Test811MetadataValidator.class,
                                        
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
                                        TestQuery84Parser.class,
                                        Test8SQLStringVisitor.class,
                                        Test85Cloning.class,
                                        Test85Create.class,
                                        Test85CreateDrop.class,
                                        TestQuery85Parser.class,
                                        Test85SQLStringVisitor.class,
                                        Test86Cloning.class,
                                        Test86Create.class,
                                        Test86CreateDrop.class,
                                        TestQuery86Parser.class,
                                        Test86SQLStringVisitor.class,
                                        Test87Cloning.class,
                                        Test87Create.class,
                                        Test87CreateDrop.class,
                                        TestQuery87Parser.class,
                                        Test87SQLStringVisitor.class,
                                        Test88Cloning.class,
                                        Test88Create.class,
                                        Test88CreateDrop.class,
                                        TestQuery88Parser.class,
                                        Test88SQLStringVisitor.class,
                                        Test89Cloning.class,
                                        Test89Create.class,
                                        Test89CreateDrop.class,
                                        TestQuery89Parser.class,
                                        Test89SQLStringVisitor.class,
                                        Test810Cloning.class,
                                        Test810Create.class,
                                        Test810CreateDrop.class,
                                        TestQuery810Parser.class,
                                        Test810SQLStringVisitor.class,
                                        Test811Cloning.class,
                                        Test811Create.class,
                                        Test811CreateDrop.class,
                                        TestQuery811Parser.class,
                                        Test811SQLStringVisitor.class,
                                        Test8124Cloning.class,
                                        Test8124Create.class,
                                        Test8124CreateDrop.class,
                                        TestQuery8124Parser.class,
                                        Test8124SQLStringVisitor.class,

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
                                        Test86Resolver.class,
                                        Test86AccessPattern.class,
                                        Test86AlterResolving.class,
                                        Test86FunctionResolving.class,
                                        Test86ProcedureResolving.class,
                                        Test86XMLResolver.class,
                                        Test87Resolver.class,
                                        Test87AccessPattern.class,
                                        Test87AlterResolving.class,
                                        Test87FunctionResolving.class,
                                        Test87ProcedureResolving.class,
                                        Test87XMLResolver.class,
                                        Test88Resolver.class,
                                        Test88AccessPattern.class,
                                        Test88AlterResolving.class,
                                        Test88FunctionResolving.class,
                                        Test88ProcedureResolving.class,
                                        Test88XMLResolver.class,
                                        Test89Resolver.class,
                                        Test89AccessPattern.class,
                                        Test89AlterResolving.class,
                                        Test89FunctionResolving.class,
                                        Test89ProcedureResolving.class,
                                        Test89XMLResolver.class,
                                        Test810Resolver.class,
                                        Test810AccessPattern.class,
                                        Test810AlterResolving.class,
                                        Test810FunctionResolving.class,
                                        Test810ProcedureResolving.class,
                                        Test810XMLResolver.class,
                                        Test811Resolver.class,
                                        Test811AccessPattern.class,
                                        Test811AlterResolving.class,
                                        Test811FunctionResolving.class,
                                        Test811ProcedureResolving.class,
                                        Test811XMLResolver.class,
                                        Test8124Resolver.class,
                                        Test8124AccessPattern.class,
                                        Test8124AlterResolving.class,
                                        Test8124FunctionResolving.class,
                                        Test8124ProcedureResolving.class,
                                        Test8124XMLResolver.class,

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
                                        Test86AlterValidation.class,
                                        Test86FunctionMetadataValidator.class,
                                        Test86Validator.class,
                                        Test86UpdateValidator.class,
                                        Test87AlterValidation.class,
                                        Test87FunctionMetadataValidator.class,
                                        Test87Validator.class,
                                        Test87UpdateValidator.class,
                                        Test88AlterValidation.class,
                                        Test88FunctionMetadataValidator.class,
                                        Test88Validator.class,
                                        Test88UpdateValidator.class,
                                        Test89AlterValidation.class,
                                        Test89FunctionMetadataValidator.class,
                                        Test89Validator.class,
                                        Test89UpdateValidator.class,
                                        Test810AlterValidation.class,
                                        Test810FunctionMetadataValidator.class,
                                        Test810Validator.class,
                                        Test810UpdateValidator.class,
                                        Test811AlterValidation.class,
                                        Test811FunctionMetadataValidator.class,
                                        Test811Validator.class,
                                        Test811UpdateValidator.class,
                                        Test8124AlterValidation.class,
                                        Test8124FunctionMetadataValidator.class,
                                        Test8124Validator.class,
                                        Test8124UpdateValidator.class,

                                        // types
                                        Test7DataTypeManagerService.class,
                                        Test8DataTypeManagerService.class
                                    } )
public class AllTests {
    // nothing to do
}
