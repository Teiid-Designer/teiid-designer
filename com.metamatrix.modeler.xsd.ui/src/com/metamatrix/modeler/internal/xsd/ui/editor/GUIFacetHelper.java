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
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleFinal;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpace;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.forms.CheckboxComponentSet;
import com.metamatrix.modeler.internal.ui.forms.ComponentCategory;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;
import com.metamatrix.modeler.internal.ui.forms.FormTextComponentSet;
import com.metamatrix.modeler.internal.ui.forms.FormTextObjectEditor;
import com.metamatrix.modeler.internal.ui.forms.HyperlinkComponentSet;
import com.metamatrix.modeler.internal.ui.forms.LinkedComponentSet;
import com.metamatrix.modeler.internal.ui.forms.MultiComponentSet;
import com.metamatrix.modeler.internal.ui.forms.TextComponentSet;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiPlugin;

public class GUIFacetHelper extends FacetHelper {

    public static final String CATEGORY_NONE = "NONE"; //$NON-NLS-1$
    public static final String CATEGORY_FORMAT = "format"; //$NON-NLS-1$
    public static final String CATEGORY_NUMERIC = "numeric"; //$NON-NLS-1$
    public static final String CATEGORY_BOUNDS = "bounds"; //$NON-NLS-1$
    public static final String CATEGORY_LENGTH = "length"; //$NON-NLS-1$
    public static final String CATEGORY_ENTERPRISE = "enterprise"; //$NON-NLS-1$
    public static final String CATEGORY_INHERITANCE = "inheritance"; //$NON-NLS-1$
    public static final String CATEGORY_ID = "id"; //$NON-NLS-1$

    private static final String[] FACET_CATEGORIES = {CATEGORY_NONE, CATEGORY_ID, CATEGORY_INHERITANCE, CATEGORY_ENTERPRISE,
        CATEGORY_LENGTH, CATEGORY_FORMAT, CATEGORY_BOUNDS, CATEGORY_NUMERIC};

    public static final String FAKE_FACET_PREVENT_RESTRICTIONS = "preventrestrictions"; //$NON-NLS-1$
    public static final String FAKE_FACET_RUNTIME = "runtime"; //$NON-NLS-1$
    public static final String FAKE_FACET_ENTERPRISE = "enterprise"; //$NON-NLS-1$
    public static final String FAKE_FACET_BASETYPE = "basetype"; //$NON-NLS-1$
    public static final String FAKE_FACET_DESCRIPTION = "description"; //$NON-NLS-1$
    public static final String FAKE_FACET_NAMESPACE = "namespace"; //$NON-NLS-1$
    public static final String FAKE_FACET_NAME = "name"; //$NON-NLS-1$
    public static final String FAKE_FACET_CREATE_SUBTYPE = "createsubtype"; //$NON-NLS-1$
    public static final String FAKE_FACET_FIND_SUBTYPES = "findsubtypes"; //$NON-NLS-1$
    public static final String FAKE_FACET_FIND_USES = "finduses"; //$NON-NLS-1$
    public static final String FAKE_FACET_OPEN_HIERARCHY = "openhierarchy"; //$NON-NLS-1$
    private static final String GROUP_INHERITANCE_LINKS = "inherit-links"; //$NON-NLS-1$
    public static final String GROUP_INHERITANCE_LINKS_AND_PREVENT = "links-and-prevent"; //$NON-NLS-1$

    public static final String FORM_CHANGE = getString("GUIFacetHelper.formChange"); //$NON-NLS-1$
    public static final String FORM_DESCRIPTION_ADD = getString("GUIFacetHelper.formDescription.add"); //$NON-NLS-1$

    private static final String DIALOG_NAME_TITLE_ID = "GUIFacetHelper.dialog.name.title"; //$NON-NLS-1$
    private static final String DIALOG_NAME_DESC_ID = "GUIFacetHelper.dialog.name.desc"; //$NON-NLS-1$
    private static final String DIALOG_NAMESPACE_TITLE_ID = "GUIFacetHelper.dialog.namespace.title"; //$NON-NLS-1$
    private static final String DIALOG_NAMESPACE_DESC_ID = "GUIFacetHelper.dialog.namespace.desc"; //$NON-NLS-1$
    private static final String DIALOG_CREATE_DESC = getString("GUIFacetHelper.dialog.create.desc"); //$NON-NLS-1$
    private static final String DIALOG_CREATE_TITLE = getString("GUIFacetHelper.dialog.create.title"); //$NON-NLS-1$
    static final String MESSAGE_NO_NAMESPACE = getString("GUIFacetHelper.message.noNamespace"); //$NON-NLS-1$

    private static final String PREFIX_CATEGORY = "GUIFacetHelper.facetcategory."; //$NON-NLS-1$
    private static final String PREFIX_FACET = "GUIFacetHelper.facet."; //$NON-NLS-1$
    private static final String PREFIX_RESTRICTION = "GUIFacetHelper.restriction."; //$NON-NLS-1$
    private static final String PREFIX_WHITESPACE = "GUIFacetHelper.whitespace."; //$NON-NLS-1$
    private static final String SUFFIX_NAME = ".name"; //$NON-NLS-1$
    private static final String SUFFIX_DESCRIPTION = ".desc"; //$NON-NLS-1$

    private static final String RESTRICTION_LIST = XSDSimpleFinal.LIST_LITERAL.getName();
    private static final String RESTRICTION_UNION = XSDSimpleFinal.UNION_LITERAL.getName();
    private static final String RESTRICTION_ATOMIC = XSDSimpleFinal.RESTRICTION_LITERAL.getName();
    private static final String[] RESTRICTION_OPTION_IDS = {RESTRICTION_ATOMIC, RESTRICTION_LIST, RESTRICTION_UNION};
    private static final String[] RESTRICTION_OPTION_NAMES = {getString(PREFIX_RESTRICTION + RESTRICTION_ATOMIC),
        getString(PREFIX_RESTRICTION + RESTRICTION_LIST), getString(PREFIX_RESTRICTION + RESTRICTION_UNION)};

    private static final String WHITESPACE_PRESERVE = XSDWhiteSpace.PRESERVE_LITERAL.getName();
    private static final String WHITESPACE_REPLACE = XSDWhiteSpace.REPLACE_LITERAL.getName();
    public static final String WHITESPACE_COLLAPSE = XSDWhiteSpace.COLLAPSE_LITERAL.getName();
    private static final String[] WHITESPACE_OPTION_IDS = {WHITESPACE_PRESERVE, WHITESPACE_REPLACE, WHITESPACE_COLLAPSE,};
    private static final String[] WHITESPACE_OPTION_NAMES = {getString(PREFIX_WHITESPACE + WHITESPACE_PRESERVE),
        getString(PREFIX_WHITESPACE + WHITESPACE_REPLACE), getString(PREFIX_WHITESPACE + WHITESPACE_COLLAPSE)};
    private static final int DEFAULT_MAX_SPIN = DEFAULT_MAX_BOUNDS;

    private static Map catIDtoFacetList = new HashMap();

    static {
        // TODO this stuff ought to be data-driven
        List l = new ArrayList();
        l.add(new FormTextComponentSet(FAKE_FACET_NAMESPACE, getString(PREFIX_FACET + FAKE_FACET_NAMESPACE), FORM_CHANGE, false,
                                       new TextDialogProvider(FORM_CHANGE, DIALOG_NAMESPACE_TITLE_ID, DIALOG_NAMESPACE_DESC_ID)));
        catIDtoFacetList.put(CATEGORY_NONE, l);

        // ID:
        l = new ArrayList();
        l.add(new FormTextComponentSet(FAKE_FACET_NAME, getString(PREFIX_FACET + FAKE_FACET_NAME), FORM_CHANGE, false,
                                       new TextDialogProvider(FORM_CHANGE, DIALOG_NAME_TITLE_ID, DIALOG_NAME_DESC_ID)));
        l.add(new TextComponentSet(FAKE_FACET_DESCRIPTION, GUIFacetHelper.getString(PREFIX_FACET + FAKE_FACET_DESCRIPTION),
                                   SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP));
        catIDtoFacetList.put(CATEGORY_ID, l);

        // Inheritance:
        l = new ArrayList();
        l.add(new FormTextComponentSet(FAKE_FACET_BASETYPE, getString(PREFIX_FACET + FAKE_FACET_BASETYPE), FORM_CHANGE, true,
                                       new BaseTypeDialogProvider(FORM_CHANGE)) {
            @Override
            protected String getUserDisplayString( Object o ) {
                if (o instanceof XSDSimpleTypeDefinition) {
                    XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)o;
                    String targetNamespace = std.getTargetNamespace();
                    if (targetNamespace == null) {
                        targetNamespace = MESSAGE_NO_NAMESPACE;
                    } // endif
                    return std.getName() + FormTextObjectEditor.HTML_LINK_END
                           + " (" + targetNamespace + ") " + FormTextObjectEditor.HTML_LINK_VAL_BEGIN; //$NON-NLS-1$//$NON-NLS-2$
                } // endif

                return super.getUserDisplayString(o);
            }

            @Override
            protected void valueClicked( Object value ) {
                showObject((EObject)value);
            }
        });
        MultiComponentSet linkSet = new MultiComponentSet(GROUP_INHERITANCE_LINKS, new LinkedComponentSet[] {
            new HyperlinkComponentSet(FAKE_FACET_CREATE_SUBTYPE, getString(PREFIX_FACET + FAKE_FACET_CREATE_SUBTYPE), true),
            new HyperlinkComponentSet(FAKE_FACET_OPEN_HIERARCHY, getString(PREFIX_FACET + FAKE_FACET_OPEN_HIERARCHY)),},
                                                          SWT.VERTICAL);

        MultiComponentSet linkAndCheckSet = new MultiComponentSet(GROUP_INHERITANCE_LINKS_AND_PREVENT, new LinkedComponentSet[] {
            linkSet,
            new CheckboxComponentSet(FAKE_FACET_PREVENT_RESTRICTIONS, getString(PREFIX_FACET + FAKE_FACET_PREVENT_RESTRICTIONS),
                                     RESTRICTION_OPTION_IDS, RESTRICTION_OPTION_NAMES, SWT.VERTICAL)}, SWT.HORIZONTAL);
        l.add(linkAndCheckSet);
        catIDtoFacetList.put(CATEGORY_INHERITANCE, l);

        // Enterprise:
        l = new ArrayList();
        l.add(new CheckboxComponentSet(FAKE_FACET_ENTERPRISE, getString(PREFIX_FACET + FAKE_FACET_ENTERPRISE)));
        l.add(new FormTextFacetSet(FAKE_FACET_RUNTIME, getString(PREFIX_FACET + FAKE_FACET_RUNTIME), null, false,
                                   new EnterpriseTypeDialogProvider(FORM_CHANGE)));
        catIDtoFacetList.put(CATEGORY_ENTERPRISE, l);

        // Length:
        l = new ArrayList();
        l.add(new SpinnerFacetSet(FACET_LENGTH, getString(PREFIX_FACET + FACET_LENGTH), false, 0, DEFAULT_MAX_SPIN));
        l.add(new SpinnerFacetSet(FACET_MINLENGTH, getString(PREFIX_FACET + FACET_MINLENGTH), false, 0, DEFAULT_MAX_SPIN));
        l.add(new SpinnerFacetSet(FACET_MAXLENGTH, getString(PREFIX_FACET + FACET_MAXLENGTH), false, 0, DEFAULT_MAX_SPIN));
        catIDtoFacetList.put(CATEGORY_LENGTH, l);

        // Format:
        l = new ArrayList();
        DialogProvider dlp = new FacetValueEditorDialog();
        l.add(new ComboFacetSet(FACET_WHITESPACE, getString(PREFIX_FACET + FACET_WHITESPACE), WHITESPACE_OPTION_IDS,
                                WHITESPACE_OPTION_NAMES, WHITESPACE_COLLAPSE));
        l.add(new TableFacetSet(FACET_ENUMERATION, getString(PREFIX_FACET + FACET_ENUMERATION), dlp));
        l.add(new TableFacetSet(FACET_PATTERN, getString(PREFIX_FACET + FACET_PATTERN), dlp));
        catIDtoFacetList.put(CATEGORY_FORMAT, l);

        // Bounds:
        l = new ArrayList();
        l.add(new SpinnerFacetSet(FAKE_FACET_MINIMUM, getString(PREFIX_FACET + FAKE_FACET_MINIMUM), true, -DEFAULT_MAX_SPIN,
                                  DEFAULT_MAX_SPIN));
        l.add(new SpinnerFacetSet(FAKE_FACET_MAXIMUM, getString(PREFIX_FACET + FAKE_FACET_MAXIMUM), true, -DEFAULT_MAX_SPIN,
                                  DEFAULT_MAX_SPIN));
        catIDtoFacetList.put(CATEGORY_BOUNDS, l);

        // Numeric
        l = new ArrayList();
        l.add(new SpinnerFacetSet(FACET_TOTALDIGITS, getString(PREFIX_FACET + FACET_TOTALDIGITS), false, 0, DEFAULT_MAX_SPIN));
        l.add(new SpinnerFacetSet(FACET_FRACTIONDIGITS, getString(PREFIX_FACET + FACET_FRACTIONDIGITS), false, 0,
                                  DEFAULT_MAX_SPIN));
        catIDtoFacetList.put(CATEGORY_NUMERIC, l);
    }

    public static ComponentCategory[] getCategories( Object transactionSource ) {
        ComponentCategory[] rv = new ComponentCategory[FACET_CATEGORIES.length];

        for (int i = 0; i < rv.length; i++) {
            String id = FACET_CATEGORIES[i];
            String name = getString(PREFIX_CATEGORY + id + SUFFIX_NAME);
            String desc = getString(PREFIX_CATEGORY + id + SUFFIX_DESCRIPTION);
            rv[i] = new ComponentCategory(id, name, desc, id != CATEGORY_NONE);
            addFacets(rv[i], transactionSource);
            rv[i].setVisible(false);
        } // endfor
        return rv;
    }

    private static void addFacets( ComponentCategory category,
                                   Object transactionSource ) {
        String id = category.getID();
        List l = (List)catIDtoFacetList.get(id);
        if (l != null) {
            Iterator itor = l.iterator();
            while (itor.hasNext()) {
                // make a new copy of the LCS:
                LinkedComponentSet lcs = ((LinkedComponentSet)itor.next()).cloneSet();
                lcs.setCategory(category);

                if (lcs instanceof TableFacetSet) {
                    TableFacetSet tfs = (TableFacetSet)lcs;
                    tfs.setTransactionSource(transactionSource);
                } // endif

                category.addComponentSet(lcs);
            } // endwhile

            // make sure it will show:
            category.setVisible(true);
        } else {
            // no facets available, hide:
            category.setVisible(false);
        } // endif
    }

    public static String getString( String i18id ) {
        return ModelerXsdUiConstants.Util.getString(i18id);
    }

    public static boolean isReady( Control c ) {
        return c != null && !c.isDisposed();
    }

    public static void showObject( EObject o ) {
        ModelResource mRes = ModelUtilities.getModelResourceForModelObject(o);
        if (mRes != null) {
            // MEM can handle the request:
            ModelEditorManager.open(o, true);
        } else {
            // no modelResource; likely a built-in type. Try to navigate to it:
            IEditorPart activeEditor = ModelerXsdUiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
            if (activeEditor instanceof ModelEditor) {
                ModelEditor me = (ModelEditor)activeEditor;
                activeEditor = me.getActiveEditor();
                if (activeEditor instanceof XsdSimpleDatatypesEditorPage) {
                    XsdSimpleDatatypesEditorPage simpPage = (XsdSimpleDatatypesEditorPage)activeEditor;
                    simpPage.openContext(o);
                } // endif
            } // endif
        } // endif
    }

    public static XSDSimpleTypeDefinition createType( XSDSchema location,
                                                      XSDSimpleTypeDefinition baseType ) {
        return createType(null, location, baseType);
    }

    public static XSDSimpleTypeDefinition createType( Shell s,
                                                      XSDSchema location,
                                                      XSDSimpleTypeDefinition baseType ) {
        // TODO eventually provide a file picker to choose where to new this at.
        InputDialog idlg = new InputDialog(s, DIALOG_CREATE_TITLE, DIALOG_CREATE_DESC, "", null); //$NON-NLS-1$
        idlg.setBlockOnOpen(true);
        idlg.open();
        String typeName = idlg.getValue();

        XSDSimpleTypeDefinition std = null;
        if (typeName != null) {
            // not cancelled; create:
            std = FacetHelper.createAtomicSimpleTypeDefinintion(location, typeName, baseType);
        } // endif

        return std;
    }
}
