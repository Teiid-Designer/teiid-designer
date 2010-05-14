/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.navigator.DeepPreOrderNavigator;

/**
 * SqlConverter
 */
public class SqlConverter {

	/** Delimiter character used when specifying fully qualified entity names */
	public static final char DELIMITER_CHAR = UuidUtil.DELIMITER_CHAR;

    // Metadata Resolver  
    private static final SymbolUUIDMappingVisitor MAPPING_VISITOR = new SymbolUUIDMappingVisitor();
    
    
    private static final List SYSTEM_RESOURCES =  Arrays.asList(ModelerCore.getSystemVdbResources());

    /**
     * convert the specified Sql String to UID form
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param cmdType the sql command type
     */
    public static String convertToUID(final String sqlString, final EObject transMappingRoot, final int cmdType) {
        return convertToUID(sqlString,transMappingRoot,cmdType,false);
    }

	/**
	 * convert the specified Sql String to UID form
	 * @param sqlString the SQL String to convert
	 * @param transMappingRoot the transformationMappingRoot for the supplied sql
	 * @param cmdType the sql command type
	 * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
	 * or if the whole workspace needs to be searched
	 */
	public static String convertToUID(final String sqlString, final EObject transMappingRoot, final int cmdType, final boolean scopeSearch) {
		return convertSql(sqlString, transMappingRoot, cmdType, true, scopeSearch, null);
	}

    /**
     * convert the specified Sql String to String form
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param cmdType the sql command type
     */
    public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType) {
        return convertToString(sqlString, transMappingRoot, cmdType, false, null);
    }

	/**
	 * convert the specified Sql String to String form
	 * @param sqlString the SQL String to convert
	 * @param transMappingRoot the transformationMappingRoot for the supplied sql
	 * @param cmdType the sql command type
	 * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
	 * or if the whole workspace needs to be searched
	 */
	public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType, final boolean restrictSearch) {
		return convertSql(sqlString, transMappingRoot, cmdType, false, restrictSearch, null);
	}

    /**
     * convert the specified Sql String to String form
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param cmdType the sql command type
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched
     */
    public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType, 
                                         final boolean restrictSearch, final ValidationContext context) {
        return convertSql(sqlString, transMappingRoot, cmdType, false, restrictSearch, context);
    }

    /**
     * convert the specified Query using the MappingVisitor
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param convertSymbolsToUUIDs the flag which specifies which way to convert.
     * 'true' converts symbol names to UUIDs, 'false' converts UUIDs to symbol names
	 * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
	 * or if the whole workspace needs to be searched
     * @param context the ValidationContext to use; may be null
     */
    private static synchronized String convertSql(final String sqlString, final EObject transMappingRoot, 
                                                  final int cmdType, final boolean convertSymbolsToUUIDs, 
                                                  final boolean restrictSearch, final ValidationContext context) {
		if(CoreStringUtil.isEmpty(sqlString)) return null;

		CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, transMappingRoot);

        final SqlTransformationMappingRoot sqlTransMappingRoot = (SqlTransformationMappingRoot)transMappingRoot;
        final TransformationValidator validator = new TransformationValidator(sqlTransMappingRoot, context, false, restrictSearch);

        // Attempt to Parse, Resolve and Validate
        try{
            // disabling caching for now seeing problems in the query editor
            QueryValidationResult validationResult = validator.validateSql(sqlString, cmdType, !convertSymbolsToUUIDs, false);

            if(convertSymbolsToUUIDs && !validationResult.isValidatable()) {
                return null;
            }        

            // Query must be valid to do conversion
            if(validationResult.isValidatable()) {
                Command command = validationResult.getCommand();

                // Set Conversion Flag
                // true - converts symbols to UUIDs
                // false- converts UUIDs to symbols
                MAPPING_VISITOR.convertToUUID(convertSymbolsToUUIDs);
                MAPPING_VISITOR.setQueryMetadata(validator.getQueryMetadata());

                // set the visitor on the top of the LanguageObject tree
                DeepPreOrderNavigator.doVisit(command, MAPPING_VISITOR);

                // If converting uuids to Names, and the string still contains uuids -
                // Try to convert everything possible...
                String newString = command.toString();
                if( !convertSymbolsToUUIDs && CoreStringUtil.indexOfIgnoreCase(newString,UUID.PROTOCOL) != -1 ) {
                    final Container container = (context != null ? context.getResourceContainer() : ModelerCore.getContainer(sqlTransMappingRoot));
                    MAPPING_VISITOR.setQueryMetadata(null);
                    return convertUUIDsToFullNames(newString,container);
                }
                MAPPING_VISITOR.setQueryMetadata(null);
                // return the new query string
                return newString;
            }
        }catch(Exception e){
            //If we encounter any exception trying to validate the query... fall through
            //to brute force implementation below.
        }

        if( !convertSymbolsToUUIDs && CoreStringUtil.indexOfIgnoreCase(sqlString,UUID.PROTOCOL) != -1 ) { 
            final Container container = (context != null ? context.getResourceContainer() : ModelerCore.getContainer(sqlTransMappingRoot));
            return convertUUIDsToFullNames(sqlString,container);
        }
        return sqlString;
    }

    /**
     * Convert the specified SQL string in UUID form to user form by resolving all
     * UUID tokens in the SQL to EObject instances contained within the specified
     * resource set.  The resource set defines the extend of resources using in resolution.
     * The method will return the user form of the SQL string replacing UUIDs with fully
     * qualified names.  If a UUID could not be resolved in the resource set, that token
     * is left as a UUID
     * @param sqlString the SQL String to convert
     * @param resourceSet the resource set containing all models used to resolve UUIDs
     * @param cmdType the sql command type
     * @return the user form of the SQL
     */
    public static synchronized String convertSql(final String sqlString, final ResourceSet resourceSet, final int cmdType) {
        final Collection eResources = (resourceSet != null ? resourceSet.getResources() : Collections.EMPTY_LIST);
        return convertSql(sqlString, eResources, cmdType);
    }

    /**
     * Convert the specified SQL string in UUID form to user form by resolving all
     * UUID tokens in the SQL to EObject instances contained within the specified
     * resource set.  The resource set defines the extend of resources using in resolution.
     * The method will return the user form of the SQL string replacing UUIDs with fully
     * qualified names.  If a UUID could not be resolved in the resource set, that token
     * is left as a UUID
     * @param sqlString the SQL String to convert
     * @param resourceSet the resource set containing all models used to resolve UUIDs
     * @param cmdType the sql command type
     * @return the user form of the SQL
     */
    public static synchronized String convertSql(final String sqlString, final Collection eResources, final int cmdType) {
        if (CoreStringUtil.isEmpty(sqlString)) {
            return null;
        }
        CoreArgCheck.isNotNull(eResources);
        
        // Get the command for the sql string
        Command command = null;
        try {
            // QueryParser is not thread-safe, get new parser each time
            QueryParser parser = new QueryParser();
            command = parser.parseCommand(sqlString);
        } catch (Exception e) {
            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
        
        if (command != null) {
            final SqlSymbolUUIDMappingVisitor sqlMappingVisitor = new SqlSymbolUUIDMappingVisitor();
            sqlMappingVisitor.setResources(eResources);

            // Set the visitor on the top of the LanguageObject tree
            DeepPreOrderNavigator.doVisit(command, sqlMappingVisitor);

            // Convert uuids to Names ...
            String newString = command.toString();

            // return the new query string
            return newString;
        }
        return null;
    }

    /**
     * Converts a String containing UUIDs of the form "mmuuid:2b7de341-7836-1e3f-be17-fda290a3df83"
     * into resolved full names as much as possible.  If an object cannot be found
     * in the specified Container (or the Container is null), it is left as a UUID.
     * @param uuidString the string containing the UUIDs
     * @param containerContext the {@link Container} of the models with the objects to be searched; may not be null
     * @return the string containing the fullname for each UUID that could be resolved
     */
    public static String convertUUIDsToFullNames(final String uuidString, final Container containerContext ) {
        if ( containerContext == null ) {
            // Can't do anything if there is no container!
            return uuidString;
        }
        
        final StringBuffer buf = new StringBuffer();
        final String idToken = UUID.PROTOCOL;        
        final int uuidLength = 43;

        if(uuidString == null){
            return buf.toString();
        }

        //True if there are groups that are not resolved.  Special process to remove unnecessary commas in from clause.
        boolean requiresSpecialProcessing = false;
        
        int index = uuidString.indexOf(idToken);
        if ( index == -1 ) {
            // there were no UUIDs in the string
            return uuidString;
        }
        // there was at least one UUID in the string
        buf.append(uuidString.substring(0, index));
        while ( index != -1 ) {  
            String id = null;              
            try {
                id = uuidString.substring(index, index + uuidLength);
                final ObjectID uuid = IDGenerator.getInstance().stringToObject(id, idToken);
                EObject obj = (EObject) containerContext.getEObjectFinder().find(uuid);
                if ( obj != null ) {
                    final SqlAspect aspect = AspectManager.getSqlAspect(obj);
                    String name = null;
                    if(aspect != null) {
                        // check the character preceeding idToken
                        if (index > 0 && uuidString.charAt(index - 1) == '.') {
                            // if the preceeding character was . then this is an 
                            //   aliased element symbol, so use the short name
                            name = aspect.getName(obj);
                        } else {
                            name = aspect.getFullName(obj);
                        }
                    }

                    if(!CoreStringUtil.isEmpty(name)){
                        buf.append(name);
                    }else{
                        if(uuidString.charAt(index + uuidLength) == ','){
                            index++;
                        }else{
                            requiresSpecialProcessing = true;                                
                        }
                    }
                } else {
                    
                    obj = findEobjectInSystemResources(uuid.toString());
                                        
                    buf.append(id);
                }

                final int nextIndex = uuidString.indexOf(idToken, index + uuidLength);
                if ( nextIndex == -1 ) {
                    buf.append(uuidString.substring(index + uuidLength));
                    break;
                }
                buf.append(uuidString.substring(index + uuidLength, nextIndex));
                index = nextIndex;
            } catch (InvalidIDException e) {
                buf.append(id);
                final int nextIndex = uuidString.indexOf(idToken, index + uuidLength);
                buf.append(uuidString.substring(index + uuidLength, nextIndex));
                index = nextIndex;
            }
        }

        if(requiresSpecialProcessing){
            final String result = buf.toString().toUpperCase();
            int i = CoreStringUtil.indexOfIgnoreCase(result, ReservedWords.WHERE);
            if(i > -1){
                boolean done = false;
                while(!done){
                    char next = buf.charAt(--i);
                    if(next == ','){
                        done = true;
                        buf.deleteCharAt(i);
                    }else if (Character.isLetterOrDigit(next) ){
                        done = true;
                    }
                }
            }
        }

        return buf.toString();
    }

    /**
     * Converts a String containing UUIDs of the form "mmuuid:2b7de341-7836-1e3f-be17-fda290a3df83"
     * into resolved MetaObject full names as much as possible.  If an object cannot be found
     * in the specified MetadataSession, it is left as a uuid.
     */
    public static String convertUUIDsToFullNames(final String uuidString, final Collection eResources) {

        final StringBuffer buf = new StringBuffer();
        final int uuidLength = 43;

        if(uuidString == null){
            return buf.toString();
        }

        //True if there are groups that are not resolved.  Special process to remove unnecessary commas in from clause.
        boolean requiresSpecialProcessing = false;
        
        int index = uuidString.indexOf(UUID.PROTOCOL);
        if ( index == -1 ) {
            // there were no UUIDs in the string
            return uuidString;
        }
        // there was at least one UUID in the string
        buf.append(uuidString.substring(0, index));
        while ( index != -1 ) {  
            String id = null;              
            try {
                id = uuidString.substring(index, index + uuidLength);
                final ObjectID uuid = IDGenerator.getInstance().stringToObject(id, UUID.PROTOCOL);
                final EObject obj = findEObjectInResourceSet(uuid.toString(),eResources);
                if ( obj != null ) {
                    final SqlAspect aspect = AspectManager.getSqlAspect(obj);
                    String name = null;
                    if(aspect != null) {
                        // check the character preceeding UUID.PROTOCOL
                        if (index > 0 && uuidString.charAt(index - 1) == '.') {
                            // if the preceeding character was . then this is an 
                            //   aliased element symbol, so use the short name
                            name = aspect.getName(obj);
                        } else {
                            name = aspect.getFullName(obj);
                        }
                    }

                    if(!CoreStringUtil.isEmpty(name)){
                        buf.append(name);
                    }else{
                        if(uuidString.charAt(index + uuidLength) == ','){
                            index++;
                        }else{
                            requiresSpecialProcessing = true;                                
                        }
                    }
                } else {
                    buf.append(id);
                }

                final int nextIndex = uuidString.indexOf(UUID.PROTOCOL, index + uuidLength);
                if ( nextIndex == -1 ) {
                    buf.append(uuidString.substring(index + uuidLength));
                    break;
                }
                buf.append(uuidString.substring(index + uuidLength, nextIndex));
                index = nextIndex;
            } catch (InvalidIDException e) {
                buf.append(id);
                final int nextIndex = uuidString.indexOf(UUID.PROTOCOL, index + uuidLength);
                buf.append(uuidString.substring(index + uuidLength, nextIndex));
                index = nextIndex;
            }
        }

        if(requiresSpecialProcessing){
            final String result = buf.toString().toUpperCase();
            int i = CoreStringUtil.indexOfIgnoreCase(result, ReservedWords.WHERE);
            if(i > -1){
                boolean done = false;
                while(!done){
                    char next = buf.charAt(--i);
                    if(next == ','){
                        done = true;
                        buf.deleteCharAt(i);
                    }else if (Character.isLetterOrDigit(next) ){
                        done = true;
                    }
                }
            }
        }

        return buf.toString();
    }
    
    private static EObject findEObjectInResourceSet(final String uuid, final Collection eResources) {
        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
            final Resource resource = (Resource)iter.next();
            if (resource instanceof EmfResource) {
                if (!resource.isLoaded()) {
                    try {
                        resource.load(Collections.EMPTY_MAP);
                    } catch (IOException e) {
                        TransformationPlugin.Util.log(IStatus.ERROR,e.getLocalizedMessage());
                    }
                }
                final EObject eObj = resource.getEObject(uuid);
                if (eObj != null) {
                    return eObj;
                }
            }
        }
        
        // if not found, check the System Resources
        return findEobjectInSystemResources(uuid);
    }
    
    private static EObject findEobjectInSystemResources(final String uuid) {
        // if not found, check the System Resources
        for (final Iterator iter = SYSTEM_RESOURCES.iterator(); iter.hasNext();) {
            final Resource resource = (Resource)iter.next();
            if (resource instanceof EmfResource) {
                final EObject eObj = resource.getEObject(uuid);
                if (eObj != null) {
                    return eObj;
                }
            }
        }
        return null;
    }

}
