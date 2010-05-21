/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.util;

import com.metamatrix.metadata.runtime.api.ElementID;
import com.metamatrix.metadata.runtime.api.GroupID;
import com.metamatrix.metadata.runtime.api.KeyID;
import com.metamatrix.metadata.runtime.api.MetadataID;
import com.metamatrix.metadata.runtime.api.ModelID;
import com.metamatrix.metadata.runtime.api.ProcedureID;
import com.metamatrix.metadata.runtime.api.VirtualDatabaseID;

/**
 * The RuntimeIDParser provides static methods to obtain specific information from a fully qualfied name. This would include
 * asking for the path, group name, or data source name.
 */
public class RuntimeIDParser {
    private final static String PERIOD = "."; //$NON-NLS-1$
    private final static String BLANK = ""; //$NON-NLS-1$

    /**
     * Returns the path for the given id. If no path then an empty <code>String</code> is returned.
     * 
     * @param MetadataID id to be parsed.
     * @return String path of the id.
     */
    public static String getPath( MetadataID id ) {

        if (id instanceof VirtualDatabaseID || id instanceof ModelID) {
            return BLANK;
        }

        int startIDX = 1;
        int endIDX = -1;

        int size = id.getNameComponents().size();

        if (id instanceof GroupID || id instanceof ProcedureID) {

            // if size is less than 2 then the name only contains the DataSourceName and its name
            if (size <= 2) {
                return BLANK;
            }

            endIDX = size - 1;
        } else if (id instanceof ElementID || id instanceof KeyID) {
            // if size is less than 3 then the name only contains the DataSourceName, GroupName and its name
            if (size <= 3) {
                return BLANK;
            }
            endIDX = size - 2;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = startIDX; i < endIDX; i++) {
            if (i > startIDX) {
                sb.append(PERIOD);
            }
            sb.append(id.getNameComponent(i));
        }

        return sb.toString();
    }

    /**
     * Returns the group name for the given id.
     * 
     * @param MetadataID id to be parsed.
     * @return String group name of the id.
     */
    public static String getGroupName( MetadataID id ) {
        if (id instanceof ElementID || id instanceof KeyID) {
            int size = id.getNameComponents().size();
            return id.getNameComponent(size - 2);
        }
        return BLANK;
    }

    /**
     * Returns the fully qualified group name for the given id.
     * 
     * @param MetadataID id to be parsed.
     * @return String group name of the id.
     */
    public static String getGroupFullName( MetadataID id ) {
        if (id instanceof ElementID || id instanceof KeyID) {
            int size = id.getNameComponents().size();
            return id.getFullName().substring(0, id.getFullName().indexOf(id.getNameComponent(size - 1)) - 1);
        }
        return BLANK;
    }
}
