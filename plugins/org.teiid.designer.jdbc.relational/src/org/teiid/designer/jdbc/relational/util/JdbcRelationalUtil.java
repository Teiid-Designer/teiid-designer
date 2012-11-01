/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.CoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcManager;
import org.teiid.designer.jdbc.JdbcManagerImpl;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;


/**
 * @since 8.0
 */
public class JdbcRelationalUtil implements ModelerJdbcRelationalConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcRelationalUtil.class);

    private static final String ESCAPE_CHARACTER = "\""; //$NON-NLS-1$

    private static JdbcManager mgr;

    /**
     * @param password may be null.
     * @since 4.0
     */
    public static Connection connect( final JdbcSource source,
                                      final String password ) throws CoreException, JdbcException, SQLException {
        CoreArgCheck.isNotNull(source);
        return getJdbcManager().createConnection(source, password);
    }

    // Surround database object name with escape characters if
    // it contains a space or starts with a numeric character
    public static String escapeDatabaseObjectName( String name ) {
        if (Character.isDigit(name.charAt(0)) || name.indexOf(' ') >= 0) {
            if (!name.startsWith(ESCAPE_CHARACTER)) {
                name = ESCAPE_CHARACTER + name;
            }
            if (!name.endsWith(ESCAPE_CHARACTER)) {
                name = name + ESCAPE_CHARACTER;
            }
        }
        return name;
    }

    /**
     * @return the JDBC manager (never <code>null</code>)
     * @since 4.0
     */
    public static JdbcManager getJdbcManager() {
        return JdbcPlugin.getJdbcManager();
    }

    /**
     * Return the physical modifiable Relational ModelResource ancestor of the specified object, if one is present, which may be the
     * specified object itself.
     */
    public static ModelResource getPhysicalModifiableRelationalModel( final Object object ) throws ModelWorkspaceException {
        final ModelResource model = getRelationalModel(object);
        if (model != null && model.getModelType().getValue() == ModelType.PHYSICAL) {
            return model;
        }
        return null;
    }

    /**
     * Return the Relational ModelResource ancestor of the specified object, if one is present, which may be the specified object
     * itself.
     */
    public static ModelResource getRelationalModel( final Object object ) throws ModelWorkspaceException {
        final ModelResource model = ModelUtil.getModel(object);
        if (model != null && RelationalPackage.eNS_URI.equals(model.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
            return model;
        }
        return null;
    }

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util.getString(I18N_PREFIX + id);
    }

    /**
     * Prevents instantiation.
     * 
     * @since 4.0
     */
    private JdbcRelationalUtil() {
    }
}
