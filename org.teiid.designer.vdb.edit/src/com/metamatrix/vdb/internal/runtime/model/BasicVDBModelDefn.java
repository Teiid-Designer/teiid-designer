/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.runtime.model;

import java.util.Date;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.runtime.BasicModelInfo;


/**
 * The VDBModelDefn contains the information needed to import a mode.
 * The <code>fileName</code> indicates the physical file that contains the xml model.
 */

public class BasicVDBModelDefn extends BasicModelInfo {


    /**
     */
    private static final long serialVersionUID = 1L;
    private String fullPath;


    public BasicVDBModelDefn(String name) {
        super(name);
        this.setVersion("");//$NON-NLS-1$
    }
    

    /**
     * CTOR used when the vdb is being sent to the 
     * runtime for creation.
     * @param model
     */
    public BasicVDBModelDefn(ModelInfo model) {
        super(model);
        if (model instanceof BasicVDBModelDefn) {
            BasicVDBModelDefn mi = (BasicVDBModelDefn)model;
            this.setFullPath(mi.getFullPath());
        }
    }
        
    
    public BasicVDBModelDefn(ModelReference modelRef) {
        String fullPath = (modelRef.getModelLocation() == null ? "NoPath" : modelRef.getModelLocation()); //$NON-NLS-1$

        Path p = new Path(fullPath);
        String name = p.lastSegment();
        
        IPath np = new Path(name);
        np = np.removeFileExtension();
        
        this.setName(np.toString());
        
        String v = (modelRef.getVersion() == null ? "0" : modelRef.getVersion()); //$NON-NLS-1$
        this.setVersion(v);
     
        this.setModelType(modelRef.getModelType().getValue());
        this.setModelURI(modelRef.getPrimaryMetamodelUri());
                
        this.setIsVisible(modelRef.isVisible()); 

        String uuid = (modelRef.getUuid() == null ? "NoUUID" : modelRef.getUuid()); //$NON-NLS-1$
        this.setUuid(uuid);

        this.setVersionDate( new Date());
        this.setVersionedBy(""); //$NON-NLS-1$
        
        setFullPath(fullPath);  
               
    }    

    public BasicVDBModelDefn(String name, String version) {
        super(name);
        this.setVersion(version);
    }
    
    
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
    
    public String getFullPath() {
        return this.fullPath;
    }    
    
    
    /**
     * Returns the name of the binding, this 
     * is used by the console
     * 
     * @return
     */
   
//    public List getConnectorBindingNames() {
//        if (connectorBindingNames == null) {
//            return Collections.EMPTY_LIST;            
//        }
//        
//        List bindings = new ArrayList(connectorBindingNames.size());
//        bindings.addAll(this.connectorBindingNames);
//        return bindings;              
//    }
//    
//    public void setConnectorBindingNames(Collection names) {
//        if (connectorBindingNames == null) {
//            connectorBindingNames = new HashSet();
//        }
//        this.connectorBindingNames.addAll(names);
//        
//    }
    
}
