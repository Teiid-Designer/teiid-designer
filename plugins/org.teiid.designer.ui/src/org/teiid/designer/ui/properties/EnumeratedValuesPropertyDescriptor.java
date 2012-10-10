/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


class EnumeratedValuesPropertyDescriptor implements ITransientPropertyDescriptor, UiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(EnumeratedValuesPropertyDescriptor.class);

    private EObject obj;
    private ILabelProvider labelProvider;
    List enumeratedValues;

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
	public CellEditor createPropertyEditor( Composite theParent ) {
        CellEditor editor = new DialogCellEditor(theParent) {
            @Override
            protected Object openDialogBox( Control cellEditorWindow ) {
                ListDialog dialog = new ListDialog(cellEditorWindow.getShell());
                dialog.setInput(enumeratedValues);
                dialog.setContentProvider(new ListSCP());
                dialog.setLabelProvider(getLabelProvider());
                dialog.setTitle(Util.getStringOrKey(PREFIX + "displayName")); //$NON-NLS-1$
                dialog.open();
                return null;
            }
        };
        return editor;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     * @since 4.3
     */
    @Override
	public String getCategory() {
        return Util.getStringOrKey(PREFIX + "category"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     * @since 4.3
     */
    @Override
	public String getDescription() {
        return Util.getStringOrKey(PREFIX + "description"); //$NON-NLS-1$
    }

    @Override
	public String getDisplayName() {
        return Util.getStringOrKey(PREFIX + "displayName"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     * @since 4.3
     */
    @Override
	public String[] getFilterFlags() {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#getPropertyValue()
     * @since 4.3
     */
    @Override
	public Object getPropertyValue() {
        if (this.obj == null) {
            throw new IllegalStateException(Util.getStringOrKey(PREFIX + "errorMsg.objectNotSet")); //$NON-NLS-1$
        }
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(this.obj);
        if (mmAspect instanceof SqlColumnAspect) {
            SqlColumnAspect colAspect = (SqlColumnAspect)mmAspect;
            Object originalValue = colAspect.getDatatype(this.obj);
            if (originalValue instanceof XSDSimpleTypeDefinition) {
                this.enumeratedValues = new ArrayList();
                List enums = ((XSDSimpleTypeDefinition)originalValue).getEnumerationFacets();
                Iterator iter = enums.iterator();
                while (iter.hasNext()) {
                    XSDEnumerationFacet ef = (XSDEnumerationFacet)iter.next();
                    List value = ef.getValue();
                    if (value.size() > 1) {
                        // skip this, it is a multi-element
                        this.enumeratedValues.addAll(value);
                    } else if (value.size() == 1) {
                        // single entry, use it:
                        this.enumeratedValues.add(value.get(0));
                    } else {
                        // no elements; try the lex value:
                        this.enumeratedValues.add(ef.getLexicalValue());
                    } // endif
                }
            }
        }
        if (enumeratedValues != null && !enumeratedValues.isEmpty()) {
            return enumeratedValues.get(0);
        }
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getHelpContextIds()
     * @since 4.3
     */
    @Override
	public Object getHelpContextIds() {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     * @since 4.3
     */
    @Override
	public Object getId() {
        return Util.getStringOrKey(PREFIX + "id"); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     * @since 4.3
     */
    @Override
	public ILabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new LabelProvider();
        } // endif

        return labelProvider;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#isCompatibleWith(org.eclipse.ui.views.properties.IPropertyDescriptor)
     * @since 4.3
     */
    @Override
	public boolean isCompatibleWith( IPropertyDescriptor theAnotherProperty ) {
        return false;
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#setObject(java.lang.Object)
     * @since 4.3
     */
    @Override
	public void setObject( Object theObject ) {
        if ((theObject != null) && supports(theObject)) {
            this.obj = (EObject)theObject;
        } else {
            throw new IllegalArgumentException(Util.getString(PREFIX + "errorMsg.objectNotSupported", this.obj)); //$NON-NLS-1$
        }
    }

    /**
     * @see org.teiid.designer.ui.properties.ITransientPropertyDescriptor#supports(java.lang.Object)
     * @since 4.3
     */
    @Override
	public boolean supports( Object theObject ) {
        if (theObject != null && theObject instanceof EObject) {
            EObject eObj = (EObject)theObject;
            MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObj);
            if (mmAspect instanceof SqlColumnAspect) {
                SqlColumnAspect colAspect = (SqlColumnAspect)mmAspect;
                Object originalValue = colAspect.getDatatype(eObj);
                if (originalValue instanceof XSDSimpleTypeDefinition) {
                    List enums = ((XSDSimpleTypeDefinition)originalValue).getEnumerationFacets();
                    if (enums != null && !enums.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    class ListSCP implements IStructuredContentProvider {
        @Override
		public Object[] getElements( Object inputElement ) {
            if (inputElement != null) {
                return ((Collection)inputElement).toArray();
            } // endif
            return new Object[0];
        }

        @Override
		public void dispose() {
            // ignore
        }

        @Override
		public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // ignore
        }
    } // endclass ListSCP
}
