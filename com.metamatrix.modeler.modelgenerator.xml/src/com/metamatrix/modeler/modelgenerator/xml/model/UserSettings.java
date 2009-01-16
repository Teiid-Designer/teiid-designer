/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.modelgenerator.xml.model;

import java.util.List;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class UserSettings {
    private static final String REQUEST_RESPONSE_TABLE_XPATH = "."; //$NON-NLS-1$

    private List roots; // contains the Elements that the user has
    // chosen to be roots

    private boolean useSchemaTypes; // Whether to convert to dates, numbers, etc. or just use strings
    private boolean updatedRootSelections = true;

    private int C_threshold;
    private int P_threshold;
    private int F_threshold;
    private String requestTableLocalName;
    private static String mergedChildSep;
    private int sourceType = 0;

    private Object syncObject;

    public UserSettings( Object syncObject ) {
        this.syncObject = syncObject;
        useSchemaTypes = false;
    }

    public void setUseSchemaTypes( boolean useSchemaTypes ) {
        synchronized (syncObject) {
            this.useSchemaTypes = useSchemaTypes;
        }
    }

    public boolean isUseSchemaTypes() {
        synchronized (syncObject) {
            return useSchemaTypes;
        }
    }

    public void setUseAsRoot( SchemaObject table,
                              boolean useAsRoot ) {
        synchronized (syncObject) {
            if (useAsRoot) {
                if (!roots.contains(table)) {
                    roots.add(table);
                    setUpdatedRootSelections(true);
                }
            } else {
                if (roots.contains(table)) {
                    roots.remove(table);
                    setUpdatedRootSelections(true);
                }
            }
        }
    }

    public boolean isUseAsRoot( SchemaObject table ) {
        synchronized (syncObject) {
            return roots.contains(table);
        }
    }

    public void set_C_threshold( int c_threshold ) {
        synchronized (syncObject) {
            C_threshold = c_threshold;
        }
    }

    public int get_C_threshold() {
        synchronized (syncObject) {
            return C_threshold;
        }
    }

    public void set_P_threshold( int p_threshold ) {
        synchronized (syncObject) {
            P_threshold = p_threshold;
        }
    }

    public int get_P_threshold() {
        synchronized (syncObject) {
            return P_threshold;
        }
    }

    public void set_F_threshold( int f_threshold ) {
        synchronized (syncObject) {
            F_threshold = f_threshold;
        }
    }

    public int get_F_threshold() {
        synchronized (syncObject) {
            return F_threshold;
        }
    }

    public void setRequestTableLocalName( String requestTableLocalName ) {
        synchronized (syncObject) {
            this.requestTableLocalName = requestTableLocalName;
        }
    }

    public String getRequestTableLocalName() {
        synchronized (syncObject) {
            return requestTableLocalName;
        }
    }

    public void setMergedChildSep( String mergedChildSep ) {
        synchronized (syncObject) {
            UserSettings.mergedChildSep = mergedChildSep;
        }
    }

    public static String getMergedChildSep() {
        return mergedChildSep;
    }

    public void setSourceType( int sourceType ) {
        synchronized (syncObject) {
            this.sourceType = sourceType;
        }
    }

    public int getSourceType() {
        synchronized (syncObject) {
            return sourceType;
        }
    }

    public String getRequestResponseTableXpath() {
        return REQUEST_RESPONSE_TABLE_XPATH;
    }

    public boolean isUpdatedRootSelections() {
        return updatedRootSelections;
    }

    public void setUpdatedRootSelections( boolean updatedRootSelections ) {
        this.updatedRootSelections = updatedRootSelections;
    }
}
