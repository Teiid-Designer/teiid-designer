/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;

/**
 * ModelAspect
 */
public class AnnotationAspect extends AbstractMetamodelAspect implements SqlAnnotationAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    public AnnotationAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    private Annotation getAnnotation(EObject eObject) {
        CoreArgCheck.isInstanceOf(Annotation.class, eObject);
        return (Annotation) eObject;
    }

    private EObject getTarget(EObject eObject) {
        Annotation annotation = getAnnotation(eObject);
        return annotation.getAnnotatedObject();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        switch ( recordType ) {
            case IndexConstants.RECORD_TYPE.ANNOTATION:
            case IndexConstants.RECORD_TYPE.PROPERTY:
                return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(target);
        if (aspect != null) {
            return aspect.getName(target);
        }
        ModelEditor editor = ModelerCore.getModelEditor();
        IPath path = editor.getModelRelativePathIncludingModel(target);
        if(path == null) {
            return null;
        }
        return path.lastSegment();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(target);
        if (aspect != null) {
            return aspect.getNameInSource(target);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(target);
        if (aspect != null) {
            return aspect.getFullName(target);
        }
        ModelEditor editor = ModelerCore.getModelEditor();
        IPath path = editor.getModelRelativePathIncludingModel(target);
        if(path == null) {
            return null;
        }
        return path.toString().replace(IPath.SEPARATOR, DELIMITER_CHAR);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(target);
        if (aspect != null) {
            return aspect.getObjectID(target);
        }
        return ModelerCore.getObjectId(target);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(target);
        if (aspect != null) {
            return aspect.getPath(target);
        }
        ModelEditor editor = ModelerCore.getModelEditor();
        return editor.getModelRelativePathIncludingModel(target);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription(EObject eObject) {
        final Annotation annotation = getAnnotation(eObject);
        return annotation.getDescription();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getKeywords(org.eclipse.emf.ecore.EObject)
     */
    public List getKeywords(EObject eObject) {
        final Annotation annotation = getAnnotation(eObject);
        return annotation.getKeywords();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getTags(org.eclipse.emf.ecore.EObject)
     */
    public Map getTags(EObject eObject) {
        final Annotation annotation = getAnnotation(eObject);
        final EMap tags = annotation.getTags();
        if ( tags != null && tags.size() != 0 ) {
            return tags.map();
        }
        return Collections.EMPTY_MAP;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getURI(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getURI(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        return ModelerCore.getModelEditor().getUri(target);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect#getEClass(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public URI getMetaclassURI(EObject eObject) {
        final EObject target = getTarget(eObject);
        if (target == null) {
            return null;
        }
        return ModelerCore.getModelEditor().getUri(target.eClass());
    }


}
