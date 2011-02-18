/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.query.sql.lang.Command;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * SqlTransformationResult
 */
public class SqlTransformationResult implements QueryValidationResult {
    
    private boolean isParsable = false;
    private boolean isResolvable = false;
    private boolean isValidatable = false;
    private IStatus targetValidStatus = null;
    private String sqlString = null;
    private Command command = null;
    private Collection sourceGroups = Collections.EMPTY_LIST;
    private Map externalMetadataMap = null;
    private Collection<IStatus> statuses = null;

    /**
     * Construct an instance of SqlTransformationResult.
     */
    public SqlTransformationResult() {}

    /**
     * Construct an instance of SqlTransformationResult.
     */
    public SqlTransformationResult(final Command command, Collection<IStatus> statuses) {
        this.command = command;
        this.statuses = statuses;
        isParsable = command != null ? true : false;        
    }
    
    /**
     * Construct an instance of SqlTransformationResult.
     */
    public SqlTransformationResult(final Command command, IStatus status) {
        this(command, new HashSet<IStatus>(1));
        if (status != null) {
            this.statuses.add(status);      
        }
    }

    /**
     * Construct an instance of SqlTransformationResult.
     */
    public SqlTransformationResult(boolean parsable,boolean resolvable,boolean validatable,
                                   IStatus targetValidStatus, String sqlString,
                                   Command command,Set sourceGroups,Map externalMap) {
        this.isParsable = parsable;
        this.isResolvable = resolvable;
        this.isValidatable = validatable;
        this.targetValidStatus = targetValidStatus;
        this.sqlString = sqlString;
        this.command = command;
        setSourceGroups(sourceGroups);
        this.externalMetadataMap = externalMap;
    }

    /**
     * set the parsable status
     * @param parsable the parsable status. 
     */
    public void setParsable(boolean parsable) {
        this.isParsable = parsable;
    }

    /**
     * set the resolvable status
     * @param resolvable the resolvable status. 
     */
    public void setResolvable(boolean resolvable) {
        this.isResolvable = resolvable;
    }

    /**
     * set the validatable status
     * @param validatable the validatable status.
     */
    public void setValidatable(boolean validatable) {
        this.isValidatable = validatable;
    }

    /**
     * set the command language object
     * @param sqlString the SQL text string. 
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * set the sourceGroups
     * @param sourceGroups the source groups for this transformation
     */
    public void setSourceGroups(Collection sourceGroups) {
        if(sourceGroups !=null) {
            this.sourceGroups = sourceGroups;
        }
    }

    /**
     * set the externalMetadataMap
     * @param map the Map 
     */
    public void setExternalMetadataMap(Map map) {
        this.externalMetadataMap = map;
    }

    /**
     * set the SQL String text
     * @param sqlString the SQL text string. 
     */
    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }

    /**
     * get the Parsable status
     * @return 'true' if parsable, 'false' if not
     */
    public boolean isParsable( ) {
        return this.isParsable;
    }
    
    /**
     * get the Resolvable status
     * @return 'true' if resolvable, 'false' if not
     */
    public boolean isResolvable( ) {
        return this.isResolvable;
    }
    
    /**
     * get the Validatable status
     * @return 'true' if validatable, 'false' if not
     */
    public boolean isValidatable( ) {
        return this.isValidatable;
    }
    
    /**
     * get the Target Valid status
     * @return 'true' if target is valid, 'false' if not
     */
    public boolean isTargetValid( ) {
        return (this.targetValidStatus == null || this.targetValidStatus.isOK());
    }
    
    /**
     * get the Target Valid status
     * @return 'true' if target is valid, 'false' if not
     */
    public IStatus getTargetValidStatus( ) {
        return this.targetValidStatus;
    }

	/**
	 * Get the Command language object.  This will be null if the
	 * SQL String was not parsable.
	 * @return the SQL command
	 */
	public Command getCommand( ) {
		return this.command;
	}

    /**
     * Get the source groups for this transformation.  Will be empty Set if
     * SQL was invalid.
     * @return the Set of source groups for this transformation
     */
    public Collection getSourceGroups( ) {
        return this.sourceGroups;
    }
    
    /**
     * Determine if the supplied source Group is used as a source in this transformation.
     * @param sourceGrp the supplied source group
     * @return 'true' if the supplied source is used in this transformation, 'false' if not.
     */
    public boolean hasSourceGroup(Object sourceGrp) {
        if(this.sourceGroups!=null && this.sourceGroups.contains(sourceGrp)) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the source Groups have valid resources.  If a model is removed the resource
     * will not be found, thus invalid resource.
     * @return 'true' if the source groups are valid, 'false' if not.
     */
    public boolean areSrcGroupMdlResourcesValid() {
        boolean isValid = true;
        if(this.sourceGroups==null || this.sourceGroups.isEmpty()) {
            isValid = false;
        } else {
            Iterator iter = this.sourceGroups.iterator();
            while(iter.hasNext()) {
                EObject sourceGrp = (EObject)iter.next();
                ModelResource mdlRsrc = ModelerCore.getModelEditor().findModelResource(sourceGrp);
                if ( mdlRsrc == null || !mdlRsrc.exists()){
                    isValid=false;
                    break;
                }
            }
        }
        return isValid;
    }

    /**
     * set the externalMetadataMap
     * @return the Map 
     */
    public Map getExternalMetadataMap() {
        return this.externalMetadataMap;
    }

    /**
     * get the SQL text string
     * @return the SQL string
     */
    public String getSqlString( ) {
        return this.sqlString;
    }
	
    /** 
     * @see com.metamatrix.query.resolver.util.QueryValidationResult#getStatusList()
     * @since 4.2
     */
    public Collection<IStatus> getStatusList() {
        return this.statuses;
    }	

    /** 
     * @param targetValidStatus The targetValidStatus to set.
     * @since 4.2
     */
    public void setTargetValidStatus(IStatus targetValidStatus) {
        this.targetValidStatus = targetValidStatus;
    }
}
