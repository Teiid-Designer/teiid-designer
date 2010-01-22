/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.vdb.api;

import com.metamatrix.core.util.ResourceNameUtil;

public class SystemVdbUtility {

    public static final String VDB_NAME = ResourceNameUtil.SYSTEM_NAME;
    public static final String VIRTUAL_MODEL_NAME = ResourceNameUtil.SYSTEM_NAME;
    public static final String ADMIN_PHYSICAL_MODEL_NAME = ResourceNameUtil.SYSTEMADMINPHYSICAL_NAME;

    public final static String[] SYSTEM_MODEL_NAMES = {ResourceNameUtil.SYSTEM_NAME, ResourceNameUtil.SYSTEMADMIN_NAME,
        ResourceNameUtil.SYSTEMADMINPHYSICAL_NAME, ResourceNameUtil.SYSTEMSCHEMA_NAME, ResourceNameUtil.SYSTEMODBCMODEL,
        ResourceNameUtil.DATASERVICESYSTEMMODEL_NAME, ResourceNameUtil.WSDL1_1_NAME, ResourceNameUtil.WSDLSOAP_NAME,
        ResourceNameUtil.JDBCSYSTEM_NAME};

    /**
     * Return true if the specified model name matches the name of any system model of TABLE_TYPES.SYSTEM_TYPE (match ignores
     * case)
     */
    public final static boolean isSystemModelWithSystemTableType( String modelName ) {
        for (int i = 0; i < SYSTEM_MODEL_NAMES.length; i++) {
            String matchName = SYSTEM_MODEL_NAMES[i];
            if (matchName.equalsIgnoreCase(modelName)) {
                return true;
            }
        }
        return false;
    }
}
