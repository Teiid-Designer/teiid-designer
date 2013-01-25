/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import java.util.List;
import java.util.Set;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.ICommandCollectorVisitor;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.query.sql.IFunctionCollectorVisitor;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.IGroupsUsedByElementsVisitor;
import org.teiid.designer.query.sql.IPredicateCollectorVisitor;
import org.teiid.designer.query.sql.IReferenceCollectorVisitor;
import org.teiid.designer.query.sql.IResolverVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitorCallback;
import org.teiid.designer.query.sql.IValueIteratorProviderCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.udf.FunctionMethodDescriptor;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.validator.IUpdateValidator;
import org.teiid.designer.validator.IUpdateValidator.TransformUpdateType;
import org.teiid.designer.validator.IValidator;
import org.teiid.designer.xml.IMappingDocumentFactory;

/**
 *
 */
public interface IQueryService {

    /**
     * Get the query parser
     * 
     * @return implementation of {@link IQueryParser}
     */
    IQueryParser getQueryParser();

    /**
     * Is the given word a reserved part of the SQL syntax
     * 
     * @param word
     * 
     * @return true if the word is reserved.
     */
    boolean isReservedWord(final String word);

    /**
     * Is the given word a reserved part of the Procedure SQL syntax
     * 
     * @param word
     * 
     * @return true if the word is reserved.
     */
    boolean isProcedureReservedWord(final String word);

    /**
     * Get the SQL reserved words
     * 
     * @return set of reserved words
     */
    Set<String> getReservedWords();

    /**
     * Get the SQL non-reserved words
     * 
     * @return set of non-reserved words
     */
    Set<String> getNonReservedWords();

    /**
     * Get the name of the JDCB type that conforms to the
     * given index number
     * 
     * @param jdbcType
     * 
     * @return type name
     */
    String getJDBCSQLTypeName(int jdbcType);

    /**
     * Create a new default function library
     * 
     * @return instance of {@link IFunctionLibrary}
     */
    IFunctionLibrary createFunctionLibrary();

    /**
     * Create a new function library with custom functions
     * derived from the given list of descriptors
     * 
     * @param functionMethodDescriptors
     * 
     * @return instance of {@link IFunctionLibrary}
     */
    IFunctionLibrary createFunctionLibrary(List<FunctionMethodDescriptor> functionMethodDescriptors);

    /**
     * Create a query language factory
     *
     * @return factory
     */
    IQueryFactory createQueryFactory();
    
    /**
     * Create an xml mapping document factory
     * 
     * @return factory
     */
    IMappingDocumentFactory getMappingDocumentFactory();

    /**
     * Get the symbol name version of the
     * given expression
     * 
     * @param expression
     * 
     * @return name of given expression
     */
    String getSymbolName(IExpression expression);

    /**
     * Get the symbol short name version of the
     * given name
     * 
     * @param name
     * 
     * @return short name of given name
     */
    String getSymbolShortName(String name);

    /**
     * Get the symbol short name version of the
     * given expression
     * 
     * @param expression
     * 
     * @return short name of given expression
     */
    String getSymbolShortName(IExpression expression);

    /**
     * Get the visitor that converts SQL objects into their
     * SQL syntax
     * 
     * @return SQL string visitor
     */
    ISQLStringVisitor getSQLStringVisitor();

    /**
     * An {@link ISQLStringVisitor} that can be extended
     * using the given callback
     * 
     * @param visitorCallback
     * 
     * @return instance of {@link ISQLStringVisitor}
     */
    ISQLStringVisitor getCallbackSQLStringVisitor(ISQLStringVisitorCallback visitorCallback);

    /**
     * This visitor class will traverse a language object tree and collect all group
     * symbol references it finds.  It uses a collection to collect the groups in so
     * different collections will give you different collection properties - for instance,
     * using a Set will remove duplicates.
     * 
     * @param removeDuplicates 
     * 
     * @return instance of {@link IGroupCollectorVisitor}
     */
    IGroupCollectorVisitor getGroupCollectorVisitor(boolean removeDuplicates);

    /**
     * Get the visitor the retrieves the groups used by elements
     * in a collection.
     * 
     * @return instance of {@link IGroupsUsedByElementsVisitor}
     */
    IGroupsUsedByElementsVisitor getGroupsUsedByElementsVisitor();

    /**
     * This visitor class will traverse a language object tree and collect all element
     * symbol references it finds.  It uses a collection to collect the elements in so
     * different collections will give you different collection properties - for instance,
     * using a Set will remove duplicates.
     * 
     * @param removeDuplicates 
     * 
     * @return instance of {@link IElementCollectorVisitor}
     */
    IElementCollectorVisitor getElementCollectorVisitor(boolean removeDuplicates);

    /**
    * This visitor class will traverse a language object tree and collect all sub-commands 
    * it finds.  It uses a List to collect the sub-commands in the order they're found.
    * 
    * @return instance of {@link ICommandCollectorVisitor}
    */
    ICommandCollectorVisitor getCommandCollectorVisitor();
    
    /**
     * <p>This visitor class will traverse a language object tree and collect all Function
     * references it finds.  It uses a collection to collect the Functions in so
     * different collections will give you different collection properties - for instance,
     * using a Set will remove duplicates.</p>
     * 
     * <p>This visitor can optionally collect functions of only a specific name</p>
     * 
     * @param removeDuplicates 
     *
     * @return instance of {@link IFunctionCollectorVisitor}
     */
    IFunctionCollectorVisitor getFunctionCollectorVisitor(boolean removeDuplicates);

    /**
     * <p>Walk a tree of language objects and collect any predicate criteria that are found.
     * A predicate criteria is of the following types: </p>
     *
     * <ul>
     * <li>{@link ICompareCriteria} CompareCriteria</li>
     * <li>{@link IMatchCriteria} MatchCriteria</li>
     * <li>{@link ISetCriteria} SetCriteria</li>
     * <li>{@link ISubquerySetCriteria} SubquerySetCriteria</li>
     * <li>{@link IIsNullCriteria} IsNullCriteria</li>
     * </ul>
     * 
     * @return instance of {@link IPredicateCollectorVisitor} 
     */
    IPredicateCollectorVisitor getPredicateCollectorVisitor();
    
    /**
     * This visitor class will traverse a language object tree and collect all
     * references it finds.
     * 
     * @return instance of {@link IReferenceCollectorVisitor}
     */
    IReferenceCollectorVisitor getReferenceCollectorVisitor();
    
    /**
     * This visitor class will traverse a language object tree and collect all language
     * objects that implement {@link ISubqueryContainer}.
     * 
     * @return instance of {@link IValueIteratorProviderCollectorVisitor}
     */
    IValueIteratorProviderCollectorVisitor getValueIteratorProviderCollectorVisitor();
    
    /**
     * This visitor class will traverse and resolve the given language object
     * 
     * @return instance of {@link IResolverVisitor}
     */
    IResolverVisitor getResolverVisitor();
    
    /**
     * Get the validator
     * 
     * @return instance of {@link IValidator}
     */
    IValidator getValidator();

    /**
     * Get the update validator
     * 
     * @param metadata
     * @param insertType
     * @param updateType 
     * @param deleteType
     * 
     * @return instance of {@link IUpdateValidator}
     */
    IUpdateValidator getUpdateValidator(IQueryMetadataInterface metadata, TransformUpdateType insertType, TransformUpdateType updateType, TransformUpdateType deleteType);

    /**
     * Resolve the given group
     * 
     * @param groupSymbol
     * @param metadata
     * @throws Exception 
     */
    void resolveGroup(IGroupSymbol groupSymbol, IQueryMetadataInterface metadata) throws Exception;

    /**
     * Convert all elements in a command to their fully qualified names.
     * 
     * @param command Command to convert
     */
    void fullyQualifyElements(ICommand command);

    /**
     * Get the query resolver
     * 
     * @return instance of {@link IQueryResolver}
     */
    IQueryResolver getQueryResolver();

}
