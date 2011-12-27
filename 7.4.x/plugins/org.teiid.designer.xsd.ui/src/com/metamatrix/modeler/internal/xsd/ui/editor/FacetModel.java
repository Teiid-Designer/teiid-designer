/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleFinal;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.search.MetadataSearch;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.forms.ComponentCategory;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;
import com.metamatrix.modeler.internal.ui.forms.LinkedComponentSet;
import com.metamatrix.modeler.internal.ui.views.DatatypeHierarchyView;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The FacetModel is an insulating layer above metadata, and serves as the underlying model for the SDE View.
 */
public class FacetModel implements ComponentSetMonitor{

    static final String TITLE_BASE_NOT_ALLOWED = GUIFacetHelper.getString("FacetModel.title_BaseNotAllowed"); //$NON-NLS-1$
    static final String DESC_BASE_NOT_ALLOWED_SAME = GUIFacetHelper.getString("FacetModel.desc_BaseNotAllowedSame"); //$NON-NLS-1$
    static final String DESC_BASE_NOT_ALLOWED_CHILD = GUIFacetHelper.getString("FacetModel.desc_BaseNotAllowedChild"); //$NON-NLS-1$

    private ComponentCategory[] ccats = GUIFacetHelper.getCategories(this);
    XSDSimpleTypeDefinition simpleType;
    boolean isReadOnly;
    boolean editorIsReadOnly;
    boolean ignoreEvents;
    private Map idToFacet = new HashMap();
    private Map idToCategory = new HashMap();
    XSDSchema schema;

    public FacetModel() {
        // hook myself up to all the LCSs:
        for (int i = 0; i < ccats.length; i++) {
            ComponentCategory cat = ccats[i];
            cat.setMonitor(this);
            idToCategory.put(cat.getID(), cat);

            // set up a quick mapping so I can easily update fields:
            Iterator itor = cat.getComponentSets().iterator();
            while (itor.hasNext()) {
                LinkedComponentSet set = (LinkedComponentSet)itor.next();
                idToFacet.put(set.getID(), set);
            } // endwhile
        } // endfor
    }

    public ComponentCategory[] getCategories() {
        return ccats;
    }

    public XSDSimpleTypeDefinition getSimpleType() {
        return simpleType;
    }

    public void setSchema( XSDSchema schema ) {
        this.schema = schema;
        LinkedComponentSet lcs = getComponentSet(GUIFacetHelper.FAKE_FACET_NAMESPACE);
        lcs.setValue(schema.getTargetNamespace());

        // enable editing of the targ namespace if possible:
        boolean readOnly;
        ModelResource mRes = ModelUtilities.getModelResourceForModelObject(schema);
        if (mRes != null) {
            readOnly = mRes.isReadOnly();
        } else {
            readOnly = true;
        } // endif
        lcs.setEditible(readOnly);
    }

    public void setSimpleType( XSDSimpleTypeDefinition simpleType ) {
        this.simpleType = null; // don't update last selection any longer
        ignoreEvents = true;

        // get readonly state:
        determineReadOnly(simpleType);

        // set fields:
        if (simpleType != null) {
            // clear the fields and set new data in:
            clear();
            setFields(simpleType);

            // show/hide facets:
            resetDisplayedCategories(simpleType);

        } else {
            // hide things, don't bother clearing:
            getCategory(GUIFacetHelper.CATEGORY_ID).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_INHERITANCE).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_ENTERPRISE).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_BOUNDS).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_NUMERIC).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_FORMAT).setVisible(false);
            getCategory(GUIFacetHelper.CATEGORY_LENGTH).setVisible(false);
        } // endif

        ignoreEvents = false;
        this.simpleType = simpleType;

        setGUIReadOnly(isReadOnly);
    }

    void resetDisplayedCategories( XSDSimpleTypeDefinition simpleType ) {
        boolean needsNumeric = FacetHelper.needsNumeric(simpleType);
        boolean needsBounds = FacetHelper.needsBounds(simpleType);
        // boolean nn2 = simpleType.getNumericFacet().isValue();
        // boolean nb2 = simpleType.getBoundedFacet().isValue();

        // show main things:
        getCategory(GUIFacetHelper.CATEGORY_ID).setVisible(true);
        getCategory(GUIFacetHelper.CATEGORY_INHERITANCE).setVisible(true);
        getCategory(GUIFacetHelper.CATEGORY_ENTERPRISE).setVisible(true);
        // show other things:
        getCategory(GUIFacetHelper.CATEGORY_BOUNDS).setVisible(needsBounds);
        getCategory(GUIFacetHelper.CATEGORY_NUMERIC).setVisible(needsNumeric);
        getCategory(GUIFacetHelper.CATEGORY_FORMAT).setVisible(!needsBounds); // because anything needing bounds is not string
        getCategory(GUIFacetHelper.CATEGORY_LENGTH).setVisible(!needsBounds); // because anything needing bounds is not string
    }

    private void determineReadOnly( XSDSimpleTypeDefinition simpleType ) {
        ModelResource mRes = ModelUtilities.getModelResourceForModelObject(simpleType);
        if (mRes != null) {
            isReadOnly = mRes.isReadOnly();
        } else {
            isReadOnly = true;
        } // endif
        
        ModelResource schemaModelResource = ModelUtilities.getModelResourceForModelObject(this.schema);
        if( schemaModelResource != null ) {
        	setEditorReadOnly(schemaModelResource.isReadOnly());
        }
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setGUIReadOnly( boolean ro ) {
        // set proper readonly state:
        determineReadOnly(simpleType);

        if (ro == true // can always set to read only
            || isReadOnly == false) { // but only allow writing if isRO says so.
            // loop through all categories:
            for (int i = 0; i < ccats.length; i++) {
                ComponentCategory cat = ccats[i];
                cat.setEnabled(!ro);
            } // endfor
        } // endif
    }
    
    void setEditorReadOnly(boolean editorIsReadOnly) {
    	this.editorIsReadOnly = editorIsReadOnly;
    }
    
    boolean isEditorReadOnly() {
    	return this.editorIsReadOnly;
    }

    void setFields( XSDSimpleTypeDefinition simpleType ) {
        boolean prevIgnore = ignoreEvents;
        ignoreEvents = true;

        // ID:
        getComponentSet(GUIFacetHelper.FAKE_FACET_NAME).setValue(simpleType.getName());
        getComponentSet(GUIFacetHelper.FAKE_FACET_NAMESPACE).setValue(simpleType.getTargetNamespace());
        getComponentSet(GUIFacetHelper.FAKE_FACET_DESCRIPTION).setValue(CoreStringUtil.collapseWhitespace(ModelObjectUtilities.getDescription(simpleType)));

        // Inherit:
        getComponentSet(GUIFacetHelper.FAKE_FACET_BASETYPE).setValue(simpleType.getBaseTypeDefinition());
        getComponentSet(GUIFacetHelper.GROUP_INHERITANCE_LINKS_AND_PREVENT).setValue(new Object[] {simpleType,
            getStringFinals(simpleType.getLexicalFinal())});

        // Enterprise:
        FacetValue entFV = FacetHelper.getEnterpriseFacetValue(simpleType);
        final boolean isEnterprise = ModelerCore.getWorkspaceDatatypeManager().isEnterpriseDatatype(simpleType);
        getComponentSet(GUIFacetHelper.FAKE_FACET_ENTERPRISE).setValue(new Boolean(isEnterprise));
        final LinkedComponentSet entcs = getComponentSet(GUIFacetHelper.FAKE_FACET_RUNTIME);
        entcs.setValue(entFV);

        // change editible state later:
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                entcs.setEditible(isEnterprise && !isReadOnly);
            }
        });

        // Other facets:
        Set setFacets = FacetHelper.getUsefulFacets(simpleType);
        Set multisToRefresh = new HashSet();

        Iterator itor = setFacets.iterator();
        while (itor.hasNext()) {
            EObject eobj = (EObject)itor.next();

            if (!(eobj instanceof XSDConstrainingFacet)) {
                continue;
            } // endif

            XSDConstrainingFacet facet = (XSDConstrainingFacet)eobj;
            String facetID = FacetHelper.getFacetName(facet);

            LinkedComponentSet lcs = getComponentSet(facetID);
            if (lcs != null) {
                FacetValue fv = FacetHelper.getFacetValue(simpleType, facet);
                if (lcs instanceof MultiFacetSet) {
                    // enum or pattern:
                    MultiFacetSet mfs = (MultiFacetSet)lcs;
                    mfs.addValue(fv, false);
                    multisToRefresh.add(mfs);
                } else {
                    // just a single dude:
                    lcs.setValue(fv);
                } // endif -- multi
            } // endif -- component set present
        } // endwhile -- over useful facet set

        itor = multisToRefresh.iterator();
        while (itor.hasNext()) {
            MultiFacetSet mfs = (MultiFacetSet)itor.next();
            mfs.reflow();
        } // endwhile

        ignoreEvents = prevIgnore;
    }

    void clear() {
        // unset fields:
        Iterator itor = idToFacet.values().iterator();
        while (itor.hasNext()) {
            LinkedComponentSet lcs = (LinkedComponentSet)itor.next();
            // never clear namespace:
            if (lcs.getID() != GUIFacetHelper.FAKE_FACET_NAMESPACE) {
                lcs.setValue(null);
            } // endif
        } // endwhile
    }

    LinkedComponentSet getComponentSet( String facetID ) {
        // translate to proper facet id:
        if (facetID == FacetHelper.FACET_MAX_EXCLUSIVE || facetID == FacetHelper.FACET_MAX_INCLUSIVE) {
            facetID = FacetHelper.FAKE_FACET_MAXIMUM;
        } else if (facetID == FacetHelper.FACET_MIN_EXCLUSIVE || facetID == FacetHelper.FACET_MIN_INCLUSIVE) {
            facetID = FacetHelper.FAKE_FACET_MINIMUM;
        } // endif

        // get the LCS:
        return (LinkedComponentSet)idToFacet.get(facetID);
    }

    private ComponentCategory getCategory( String catID ) {
        return (ComponentCategory)idToCategory.get(catID);
    }

    private static Collection getStringFinals( Collection simpleFinals ) {
        Collection rv = new ArrayList(simpleFinals.size());

        Iterator itor = simpleFinals.iterator();
        while (itor.hasNext()) {
            XSDSimpleFinal fin = (XSDSimpleFinal)itor.next();
            rv.add(fin.getName());
        } // endwhile

        return rv;
    }

    public void update( final ComponentSetEvent event ) {
        if (!ignoreEvents && simpleType != null) {
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) {
                    final String id = event.componentSet.getID();
                    if (event.value instanceof FacetValue && !GUIFacetHelper.FAKE_FACET_RUNTIME.equals(id)) {
                        FacetValue fv = (FacetValue)event.value;
                        // see if we need to do an extra dereference (Table support)
                        if (fv.value instanceof FacetValue) {
                            fv = (FacetValue)fv.value;
                        } // endif

                        // need to add, edit, or remove?
                        if (event.isDelete) {
                            // may need to remove, if we have a facet:
                            if (fv.facet != null) {
                                // remove:
                                FacetHelper.removeFacet(simpleType, fv.facet);
                                fv.clear();
                            } // endif
                        } else {
                            // add or edit:
                            fv.facet = FacetHelper.addOrSetFacetValue(simpleType, id, fv);
                        } // endif

                    } else {
                        // must have come from one of the fundamental things:
                        // switch on ID:
                        if (GUIFacetHelper.FAKE_FACET_NAME == id) { // ----------------------------------------------
                            ModelObjectUtilities.rename(simpleType, (String)event.value, this);

                        } else if (GUIFacetHelper.FAKE_FACET_NAMESPACE == id) { // ----------------------------------
                            XsdUtil.setTargetNamespace((XSDSchema)ModelObjectUtilities.getRealEObject(simpleType.getSchema()),
                                                       (String)event.value);
                            // force the base type component to pick up the change, if applicable:
                            LinkedComponentSet baseType = getComponentSet(GUIFacetHelper.FAKE_FACET_BASETYPE);
                            baseType.setValue(null);
                            baseType.setValue(simpleType.getBaseTypeDefinition());

                        } else if (GUIFacetHelper.FAKE_FACET_PREVENT_RESTRICTIONS == id) { // -----------------------
                            // get the monitored list:
                            List current = simpleType.getLexicalFinal();
                            // clear the current settings:
                            current.clear();
                            // iterate through all selected:
                            Set newVals = (Set)event.value;
                            Iterator itor = newVals.iterator();
                            while (itor.hasNext()) {
                                String element = (String)itor.next();
                                current.add(XSDSimpleFinal.get(element));
                            } // endwhile

                        } else if (GUIFacetHelper.FAKE_FACET_RUNTIME == id) { // ------------------------------------
                            FacetValue fv = (FacetValue)event.value;
                            FacetHelper.setEnterpriseFacetValue(simpleType, fv);

                        } else if (GUIFacetHelper.FAKE_FACET_ENTERPRISE == id) { // ---------------------------------
                            // enable/disable things as needed:
                            boolean isEnterprise = ((Boolean)event.value).booleanValue();
                            LinkedComponentSet rtlcs = getComponentSet(GUIFacetHelper.FAKE_FACET_RUNTIME);
                            rtlcs.setEditible(isEnterprise);
                            if (isEnterprise) {
                                // we are enterprise, set:
                                FacetHelper.setEnterpriseFacetValue(simpleType, ((AbstractFacetSet)rtlcs).getFacetValue());
                            } else {
                                // not enterprise, remove:
                                ModelerCore.getModelEditor().unsetEnterpriseDatatypePropertyValue(simpleType);
                            } // endif

                        } else if (GUIFacetHelper.FAKE_FACET_BASETYPE == id) { // -----------------------------------
                            XSDSimpleTypeDefinition newBaseType = (XSDSimpleTypeDefinition)event.value;

                            if (newBaseType == simpleType) {
                                // we do not allow the base type to be the same as the current type:
                                event.doit = false;
                                MessageDialog.openError(null, TITLE_BASE_NOT_ALLOWED, DESC_BASE_NOT_ALLOWED_SAME);

                            } else if (FacetHelper.isSubtypeOf(newBaseType, simpleType)) {
                                // we do not allow subtypes to be set as base types:
                                event.doit = false;
                                MessageDialog.openError(null, TITLE_BASE_NOT_ALLOWED, DESC_BASE_NOT_ALLOWED_CHILD);

                            } else {
                                ModelerCore.getDatatypeManager(simpleType).setBasetypeDefinition(simpleType, newBaseType);
                                // reread everything, so that we can get new info, including new inherited facets:
                                ignoreEvents = true;
                                clear();
                                // show/hide facets:
                                resetDisplayedCategories(simpleType);
                                setFields(simpleType);
                                ignoreEvents = false;
                            } // endif

                        } else if (GUIFacetHelper.FAKE_FACET_DESCRIPTION == id) { // --------------------------------
                            ModelObjectUtilities.setDescription(simpleType, (String)event.value, this);

                        } else if (GUIFacetHelper.FAKE_FACET_CREATE_SUBTYPE == id) { // -----------------------------
                        	// We need to check the read-only state of the datatypes editor, rather than the "selected datatype"
                        	// This will allow creating a sub-type of a built-in datatype that IS read-only
                        	if( isEditorReadOnly() ) {
                        		MessageDialog.openConfirm(null, ModelerXsdUiConstants.Util.getString("FacetModel.read_only.title"),  //$NON-NLS-1$
                        				ModelerXsdUiConstants.Util.getString("FacetModel.read_only.cannotCreateSubtypeMessage")); //$NON-NLS-1$
                        		return null;
                        	}
                            final XSDSimpleTypeDefinition newType = GUIFacetHelper.createType(schema, simpleType);
                            if (newType != null) {
                                uow.setDescription(ModelerXsdUiConstants.Util.getString("FacetModel.transaction_create", newType.getName())); //$NON-NLS-1$
                                uow.setSource(schema);
                                Display.getDefault().asyncExec(new Runnable() {
                                    public void run() {
                                        // run later to help selection work right:
                                        GUIFacetHelper.showObject(newType);
                                    }
                                });
                            } // endif

                        } else if (GUIFacetHelper.FAKE_FACET_FIND_SUBTYPES == id) {
                            MessageDialog.openInformation(null,
                                                          "Find subtypes", "find subtypes not implemented; specified was: " + event.value);//$NON-NLS-1$ //$NON-NLS-2$

                        } else if (GUIFacetHelper.FAKE_FACET_OPEN_HIERARCHY == id) {
                            try {
                                DatatypeHierarchyView datatypes = (DatatypeHierarchyView)UiUtil.getWorkbenchPage().showView(UiConstants.Extensions.DATATYPE_HIERARCHY_VIEW);
                                datatypes.revealType(simpleType);
                            } catch (PartInitException ex) {
                                ModelerXsdUiConstants.Util.log(ex);
                            } // endtry

                        } else if (GUIFacetHelper.FAKE_FACET_FIND_USES == id) {
                            MetadataSearch search = ModelerCore.createMetadataSearch();
                            search.setDatatype(simpleType, false);
                            search.execute(new NullProgressMonitor());
                            List resultsl = search.getResults();
                            ListDialog ld = new ListDialog(null) {
                                @Override
                                protected int getTableStyle() {
                                    return SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
                                }
                            };
                            ld.setInput(resultsl);
                            ld.setAddCancelButton(true);
                            ld.setBlockOnOpen(true);
                            ld.setContentProvider(new ListSCP());
                            ld.setLabelProvider(ModelUtilities.getEMFLabelProvider());
                            ld.setMessage("Pat's zany message"); //$NON-NLS-1$
                            ld.setTitle("Pat's zany title"); //$NON-NLS-1$
                            ld.setInitialElementSelections(resultsl);
                            ld.open();
                            MessageDialog.openInformation(null,
                                                          "Find uses", "find uses not implemented; specified was: " + event.value);//$NON-NLS-1$ //$NON-NLS-2$

                        } // endif -- id switch
                    } // endif -- fake or real facet
                    return null;
                }
            }; // endanon transaction

            try {
                ModelerCore.getModelEditor().executeAsTransaction(runnable,
                                                                  ModelerXsdUiConstants.Util.getString("FacetModel.Set_on_simple_data_type_properties"), true, true, this); //$NON-NLS-1$
            } catch (ModelerCoreException mce) {
                ModelerXsdUiConstants.Util.log(mce);
            }
        } // endif -- ignorable
    }

    //
    // Inner classes:
    //
    public class ListSCP implements IStructuredContentProvider {
        public Object[] getElements( Object inputElement ) {
            if (inputElement != null) {
                return ((Collection)inputElement).toArray();
            } // endif
            return new Object[0];
        }

        public void dispose() {
            // ignore
        }

        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // ignore
        }
    } // endclass ListSCP
}
