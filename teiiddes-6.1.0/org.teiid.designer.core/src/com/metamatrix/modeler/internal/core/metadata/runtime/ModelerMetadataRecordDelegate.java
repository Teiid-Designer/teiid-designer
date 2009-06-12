/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.id.ObjectID;
import com.metamatrix.metadata.runtime.impl.MetadataRecordDelegate;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;

public class ModelerMetadataRecordDelegate extends MetadataRecordDelegate {

	private SqlAspect sqlAspect;
	private EObject eObject;
	
	private boolean fullNameSet;
	private boolean nameSet;
	private boolean nameInSourceSet;
	private boolean parentUUIDSet;
	private boolean uuidSet;
	
	public ModelerMetadataRecordDelegate(final SqlAspect sqlAspect, final EObject eObject) {
		this.sqlAspect = sqlAspect;
		this.eObject = eObject;
	}

	@Override
	public String getFullName() {
		if (this.eObject != null & !fullNameSet) {
			this.setFullName(this.sqlAspect.getFullName(eObject));
		}
		return super.getFullName();
	}

	@Override
	public String getName() {
		if (this.eObject != null & !nameSet) {
			this.setName(this.sqlAspect.getName(eObject));
		}
		return super.getName();
	}

	@Override
	public String getNameInSource() {
		if (this.eObject != null & !nameInSourceSet) {
			this.setNameInSource(this.sqlAspect.getNameInSource(eObject));
		}
		return super.getNameInSource();
	}

	@Override
	public String getParentUUID() {
		if(this.eObject != null && !parentUUIDSet) {
			Object parentID = sqlAspect.getParentObjectID(eObject);
			if(parentID != null) {
				setParentUUID(parentID.toString());
			}
		}
		return super.getParentUUID();
	}

	@Override
	public String getUUID() {
    	if(this.eObject != null && !uuidSet) {
    		setUUID(getObjectID(eObject));
    	}
		return super.getUUID();
	}

	@Override
	public void setFullName(String fullName) {
		fullNameSet = true;
		super.setFullName(fullName);
	}

	@Override
	public void setName(String name) {
		nameSet = true;
		super.setName(name);
	}

	@Override
	public void setNameInSource(String nameInSource) {
		nameInSourceSet = true;
		super.setNameInSource(nameInSource);
	}

	@Override
	public void setParentUUID(String parentUUID) {
		parentUUIDSet = true;
		super.setParentUUID(parentUUID);
	}

	@Override
	public void setUUID(String uuid) {
		uuidSet = true;
		super.setUUID(uuid);
	}
	
    String getObjectID(Object object) {
        if(object != null && object instanceof EObject) {
            ObjectID objectID = (ObjectID) this.sqlAspect.getObjectID((EObject)object);
            if(objectID != null) {
                return objectID.toString();
            }
        }
        
        return null;
    }

    List getObjectIDs(Collection eObjs) {
        if(eObjs == null) {
            return Collections.EMPTY_LIST;
        }
        List objIds = new ArrayList(eObjs.size());
        Iterator eIter = eObjs.iterator();
        while(eIter.hasNext()) {
            EObject eObj = (EObject) eIter.next();
            objIds.add(getObjectID(eObj));
        }
        return objIds;
    }

	SqlAspect getSqlAspect() {
		return sqlAspect;
	}
	
}
