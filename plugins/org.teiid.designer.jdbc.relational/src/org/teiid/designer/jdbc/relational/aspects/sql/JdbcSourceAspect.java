/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.aspects.sql;

import java.util.Iterator;
import java.util.Properties;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlModelSourceAspect;
import org.teiid.designer.jdbc.JdbcImportOptions;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.JdbcPackage;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.JdbcSourceProperty;


/**
 * RelationalEntityAspect
 *
 * @since 8.0
 */
public class JdbcSourceAspect extends AbstractMetamodelAspect implements SqlModelSourceAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    protected JdbcSourceAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(final EObject eObject) {
        CoreArgCheck.isInstanceOf(JdbcSource.class, eObject); 
        JdbcSource entity = (JdbcSource) eObject;   
        return entity.getName();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(final EObject eObject) {
        CoreArgCheck.isInstanceOf(JdbcSource.class, eObject); 
        JdbcSource entity = (JdbcSource) eObject;       
        return entity.getName();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelSourceAspect#getProperties(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Properties getProperties(final EObject eObject) {
        CoreArgCheck.isInstanceOf(JdbcSource.class, eObject); 

        Properties props = new Properties();
        this.addJdbcSourceProperties((JdbcSource)eObject, props);
        
        for (Iterator iter = eObject.eAllContents(); iter.hasNext();) {
            EObject eObj = (EObject)iter.next();
            
            if (eObj instanceof JdbcSourceProperty) {
                this.addJdbcSourcePropertyProperties((JdbcSourceProperty)eObj, props);
                
            } else if (eObj instanceof JdbcImportSettings) {
                this.addJdbcImportSettingProperties((JdbcImportSettings)eObj, props);
                
            } else if (eObj instanceof JdbcImportOptions) {
                this.addJdbcImportOptionProperties((JdbcImportOptions)eObj, props);
            }
        }
        return props;
    }
    
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================
    
    private void addJdbcSourceProperties(final JdbcSource source, final Properties props) {
        final String propNamePrefix = JdbcSource.class.getName() + "."; //$NON-NLS-1$
        
        String propName = propNamePrefix + source.eClass().getEStructuralFeature(JdbcPackage.JDBC_SOURCE__NAME).getName(); 
        if (source.getName() != null) {
            props.setProperty(propName, source.getName());
        }

        propName = propNamePrefix + source.eClass().getEStructuralFeature(JdbcPackage.JDBC_SOURCE__DRIVER_CLASS).getName(); 
        if (source.getDriverClass() != null) {
            props.setProperty(propName, source.getDriverClass());
        }

        propName = propNamePrefix + source.eClass().getEStructuralFeature(JdbcPackage.JDBC_SOURCE__DRIVER_NAME).getName(); 
        if (source.getDriverName() != null) {
            props.setProperty(propName, source.getDriverName());
        }

        propName = propNamePrefix + source.eClass().getEStructuralFeature(JdbcPackage.JDBC_SOURCE__USERNAME).getName(); 
        if (source.getUsername() != null) {
            props.setProperty(propName, source.getUsername());
        }

        propName = propNamePrefix + source.eClass().getEStructuralFeature(JdbcPackage.JDBC_SOURCE__URL).getName(); 
        if (source.getUrl() != null) {
            props.setProperty(propName, source.getUrl());
        }
    }
    
    private void addJdbcSourcePropertyProperties(final JdbcSourceProperty sourcePropery, final Properties props) {
        final String propNamePrefix = JdbcSourceProperty.class.getName() + "."; //$NON-NLS-1$;

        String propName = propNamePrefix + sourcePropery.getName();
        if (sourcePropery.getValue() != null) {
            props.setProperty(propName, sourcePropery.getValue());
        }
    }
    
    private void addJdbcImportSettingProperties(final JdbcImportSettings importSettings, final Properties props) {
    }
    
    private void addJdbcImportOptionProperties(final JdbcImportOptions importOptions, final Properties props) {
        final String propNamePrefix = JdbcImportOptions.class.getName() + "."; //$NON-NLS-1$;
        
        String propName = propNamePrefix + importOptions.getName();
        if (importOptions.getValue() != null) {
            props.setProperty(propName, importOptions.getValue());
        }
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
