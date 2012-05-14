/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.core.util.FileUtils;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import com.metamatrix.core.io.FileUrl;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.procedure.XsdInstanceNode;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 4.2
 */
public class WebServiceUiUtil implements FileUtils.Constants, IInternalUiConstants {

    // ===========================================================================================================================
    // Constants

    /**
     * XSD extensions prefixed with the file wildcard and extension separator. Suitable for use in the
     * {@link org.eclipse.swt.widgets.FileDialog}.
     * 
     * @since 4.2
     */
    public static final String[] FILE_DIALOG_XSD_EXTENSIONS;

    /** Prefix for properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(WebServiceUiUtil.class);

    public static final String THIS_CLASS = "WebServiceUiUtil"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Initializer

    static {
        // create XSD file dialog extension array
        FILE_DIALOG_XSD_EXTENSIONS = new String[] {createFileDialogExtension(ModelUtil.EXTENSION_XSD)};
    }

    // ===========================================================================================================================
    // Static Methods

    private static void addVariableStatements( XsdInstanceNode node,
                                               Block block,
                                               Input input,
                                               List nodes,
                                               List variables ) {
        if (node.isSelectable()) {
            nodes.add(node);
            DeclareStatement declaration = WebServiceUiUtil.createDeclareStatement(node, input);
            variables.add(declaration.getVariable());
            block.getStatements().add(0, declaration);
        }
        XsdInstanceNode[] children = node.getChildren();
        for (int ndx = children.length; --ndx >= 0;) {
            addVariableStatements(children[ndx], block, input, nodes, variables);
        } // for
    }

    /**
     * Copies the <code>toString()</code> of each object in the selection, separated by a linefeed, to the clipboard. If selection
     * is <code>null</code> or empty nothing is copied.
     * 
     * @param theSelection the selection being copied to the clipboard
     * @since 4.2
     */
    public static void copyToClipboard( ISelection theSelection ) {
        List objects = new ArrayList(SelectionUtilities.getSelectedObjects(theSelection));

        if (!objects.isEmpty()) {
            for (int size = objects.size(), i = 0; i < size; i++) {
                objects.set(i, getText(objects.get(i)));
            }

            SystemClipboardUtilities.copyToClipboard(new StructuredSelection(objects));
        }
    }

    /**
     * @param node
     * @return
     * @since 5.0.1
     */
    public static DeclareStatement createDeclareStatement( XsdInstanceNode node,
                                                           Input input ) {
        String name = AspectManager.getSqlAspect(input).getFullName(input);

        DeclareStatement statement = new DeclareStatement(new ElementSymbol(getQualifiedInputVariableName(node.getName())),
                                                          DatatypeConstants.RuntimeTypeNames.STRING);
        statement.setValue(new Function(WebServiceUtil.XPATHVALUE, new Expression[] {new ElementSymbol(name),
            new Constant(WebServiceUtil.createXPath(node))}));

        return statement;
    }

    /**
     * Creates an extension which can be used in a {@link org.eclipse.swt.widgets.FileDialog}. Prefixes the specified extension
     * with the file name wildcard and the extension separator character.
     * 
     * @param theExtension the extension being used
     * @since 4.2
     */
    public static String createFileDialogExtension( String theExtension ) {
        return new StringBuffer().append(FILE_NAME_WILDCARD).append(FILE_EXTENSION_SEPARATOR_CHAR).append(theExtension).toString();
    }

    /**
     * Splits the specified map into separate node and variable arrays, and calls
     * {@link #ensureVariablesUnique(XsdInstanceNode[], ElementSymbol[])}.
     * 
     * @param nodesToDeclarations
     * @since 5.0.1
     */
    public static void ensureVariablesUnique( Map nodesToDeclarations ) {
        XsdInstanceNode[] nodes = new XsdInstanceNode[nodesToDeclarations.size()];
        ElementSymbol[] vars = new ElementSymbol[nodes.length];
        Iterator iter = nodesToDeclarations.entrySet().iterator();
        for (int ndx = 0; iter.hasNext(); ++ndx) {
            Entry entry = (Entry)iter.next();
            nodes[ndx] = (XsdInstanceNode)entry.getKey();
            vars[ndx] = ((DeclareStatement)entry.getValue()).getVariable();
        } // for
        WebServiceUiUtil.ensureVariablesUnique(nodes, vars);
    }

    /**
     * Ensures the names of the list of specified variables are unique within that list, changing the names when necessary. Name
     * conflicts are first resolved by prefixing the names of ancestors of the corresponding XSD instance nodes until unique. If
     * names are still in conflict after all ancestor names have been prefixed, then the names are prefixed by the segments of the
     * namespaces of the corresponding XSD instance nodes, from last to first, until unique. By the end of this routine, all of
     * the specified variables names are guaranteed to be unique.
     * 
     * @param nodes The XSD instance nodes corresponding to the specified variables.
     * @param variables The variables to ensure are unique.
     * @since 5.0.1
     */
    public static void ensureVariablesUnique( XsdInstanceNode[] nodes,
                                              ElementSymbol[] variables ) {
        for (int ndx1 = variables.length; --ndx1 > 0;) {
            ElementSymbol var1 = variables[ndx1];
            String name1 = var1.getShortName();
            for (int ndx2 = ndx1; --ndx2 >= 0;) {
                ElementSymbol var2 = variables[ndx2];
                if (name1.equalsIgnoreCase(var2.getShortName())) {
                    XsdInstanceNode node1 = nodes[ndx1];
                    XsdInstanceNode node2 = nodes[ndx2];
                    if (!prefixAncestorNamesUntilUnique(var1, var2, node1.getParent(), node2.getParent())) {
                        prefixNamespaceSegmentsUntilUnique(var1, var2, node1.getTargetNamespace(), node2.getTargetNamespace());
                    }
                    ensureVariablesUnique(nodes, variables);
                    return;
                }
            } // for
        } // for
    }

    /**
     * Convenience method to retrieve a Web Service UI image.
     * 
     * @param theImageName the name of the image being requested
     * @return the image or <code>null</code> if not found
     * @since 4.2
     */
    public static Image getImage( String theImageName ) {
        return WebServiceUiPlugin.getDefault().getImage(theImageName);
    }

    /**
     * Convenience method to retrieve a Web Service UI image descriptors.
     * 
     * @param theImageName the name of the image descriptor being requested
     * @return the image descriptor or <code>null</code> if not found
     * @since 4.2
     */
    public static ImageDescriptor getImageDescriptor( String theImageName ) {
        return WebServiceUiPlugin.getDefault().getImageDescriptor(theImageName);
    }

    /**
     * @param object
     * @return The nearest operation to the specified object. In the case of an {@link Interface}, this will be the first
     *         operation defined for that {@link Interface}.
     * @since 5.0.2
     */
    public static Operation getOperation( Object object ) {
        if (object instanceof SqlTransformationMappingRoot) {
            object = ((SqlTransformationMappingRoot)object).getTarget();
        } else if (object instanceof Diagram) {
            object = ((Diagram)object).getTarget();
        }
        if (object instanceof Interface) {
            List ops = ((Interface)object).getOperations();
            return (ops.isEmpty() ? null : (Operation)ops.get(0));
        } else if (object instanceof EObject) {
            for (EObject eObj = (EObject)object; eObj != null; eObj = eObj.eContainer()) {
                if (eObj instanceof Operation) {
                    return (Operation)eObj;
                } else if (object instanceof ModelAnnotation) {
                    // Let's find the first Interface and get it's first operation
                    ModelAnnotation annot = (ModelAnnotation)object;
                    List eObjs = annot.eResource().getContents();
                    for (Iterator iter = eObjs.iterator(); iter.hasNext();) {
                        Object nextObj = iter.next();
                        if (nextObj instanceof Interface) {
                            List ops = ((Interface)nextObj).getOperations();
                            return (ops.isEmpty() ? null : (Operation)ops.get(0));
                        }
                    }
                }
            } // for
        }
        return null;
    }

    /**
     * Method which returns the first Interface it can find within an WS model.
     * 
     * @param object
     * @return The nearest {@link Interface}.
     * @since 5.0.2
     */
    public static Interface getFirstInterface( Object object ) {
        if (object instanceof Interface) {
            return (Interface)object;
        } else if (object instanceof EObject) {
            for (EObject eObj = (EObject)object; eObj != null; eObj = eObj.eContainer()) {
                if (object instanceof ModelAnnotation) {
                    // Let's find the first Interface and get it's first operation
                    ModelAnnotation annot = (ModelAnnotation)object;
                    List eObjs = annot.eResource().getContents();
                    for (Iterator iter = eObjs.iterator(); iter.hasNext();) {
                        Object nextObj = iter.next();
                        if (nextObj instanceof Interface) {
                            return (Interface)nextObj;
                        }
                    }
                }
            } // for
        }
        return null;
    }

    /**
     * @param name A variable name not qualified by it's group name.
     * @return The fully-qualified name of the variable as it would appear within the global {@value
     *         ProcedureReservedWords.VARIABLES} group.
     * @since 5.0.1
     */
    private static String getQualifiedInputVariableName( String name ) {
        return (WebServiceUtil.INPUT_VARIABLE_PREFIX + name);
    }

    /**
     * Convenience method to retrieve workbench shared images.
     * 
     * @param theImageName the name of the image being requested
     * @return the image or <code>null</code> if not found
     * @since 4.2
     */
    public static Image getSharedImage( String theImageName ) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(theImageName);
    }

    /**
     * Convenience method to retrieve workbench shared image descriptors.
     * 
     * @param theImageName the name of the image descriptor being requested
     * @return the image descriptor or <code>null</code> if not found
     * @since 4.2
     */
    public static ImageDescriptor getSharedImageDescriptor( String theImageName ) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(theImageName);
    }

    /**
     * Obtains an appropriate image for the specified status.
     * 
     * @param theStatus the status whose image is being requested
     * @return the image or <code>null</code> if image not found
     * @since 4.2
     */
    public static Image getStatusImage( IStatus theStatus ) {
        Image result = null;

        switch (theStatus.getSeverity()) {
            case IStatus.ERROR: {
                result = getSharedImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                break;
            }
            case IStatus.WARNING: {
                result = getSharedImage(ISharedImages.IMG_OBJS_WARN_TSK);
                break;
            }
            case IStatus.INFO: {
                result = getSharedImage(ISharedImages.IMG_OBJS_INFO_TSK);
                break;
            }
        }

        return result;
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     * @since 4.2
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Gets a string representation of the specified object for use within the Web Service UI.
     * 
     * @param theObject the object whose string representation is being requested
     * @return the localized text
     * @since 4.2
     */
    public static String getText( Object theObject ) {
        String result = ""; //$NON-NLS-1$

        if (theObject != null) {
            if (theObject instanceof IWebServiceResource) {
                IWebServiceResource resource = (IWebServiceResource)theObject;
                result = new StringBuffer().append(getString("property.namespace")).append('=').append(resource.getNamespace()) //$NON-NLS-1$
                .append(", ") //$NON-NLS-1$
                .append(getString("property.resolvedPath")).append('=').append(resource.getFullPath()) //$NON-NLS-1$
                .toString();
            } else if (theObject instanceof IFile) {
                result = ((IFile)theObject).getFullPath().toOSString();
            } else if (theObject instanceof FileUrl) {
                result = ((FileUrl)theObject).getOriginalUrlString();
            } else if (theObject instanceof File) {
                result = theObject.toString();
            } else {
                result = theObject.toString();
            }
        }

        return result;
    }

    /**
     * Initializes the specified operation's transformation to include:
     * <ul>
     * <li>A virtual procedure containing variable declarations and assignments for all "selectable" components in the input's
     * content XSD element, and</li>
     * <li>A default SELECT statement that selects all mapped columns defined in the output document.
     * </ul>
     * 
     * @param operation
     * @param transactionSource
     * @param replace True if all previous variable declarations and assignments should be replaced.
     * @since 5.0.1
     */
    public static void initializeProcedure( Operation operation,
                                            Object transactionSource,
                                            boolean replace ) {
        SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(operation);
        CreateUpdateProcedureCommand proc = null;
        if (!TransformationHelper.isEmptySelect(root)) {
            proc = (CreateUpdateProcedureCommand)TransformationHelper.getCommand(root, QueryValidator.SELECT_TRNS);
        }
        boolean initVar = true;
        boolean initSelect = true;
        if (proc == null) {
            String sqlString = TransformationHelper.getSelectSqlString(root);
            if (sqlString != null && sqlString.length() > 0) {
                initVar = false;
                initSelect = false;
            } else {
                proc = new CreateUpdateProcedureCommand(new Block());
                proc.setUpdateProcedure(false);
            }
        } else {
            Block block = proc.getBlock();
            if (block == null) {
                proc.setBlock(new Block());
            } else {
                for (Iterator statementIter = block.getStatements().iterator(); statementIter.hasNext();) {
                    Object statement = statementIter.next();
                    if (statement instanceof DeclareStatement) {
                        if (replace
                            && ((DeclareStatement)statement).getVariable().getName().startsWith(WebServiceUtil.INPUT_VARIABLE_PREFIX)) {
                            statementIter.remove();
                        } else {
                            initVar = false;
                        }
                    } else if (statement instanceof AssignmentStatement) {
                        if (replace
                            && ((AssignmentStatement)statement).getVariable().getName().startsWith(WebServiceUtil.INPUT_VARIABLE_PREFIX)) {
                            statementIter.remove();
                        } else {
                            initVar = false;
                        }
                    } else {
                        if (replace) {
                            statementIter.remove();
                        } else {
                            initSelect = false;
                        }
                    }
                } // for
            }
        }
        if (initVar && replace) {
            Input input = operation.getInput();
            if (input != null) {
                XSDElementDeclaration elem = input.getContentElement();
                if (elem != null) {
                    List nodes = new ArrayList();
                    List vars = new ArrayList();
                    addVariableStatements(new XsdInstanceNode(elem), proc.getBlock(), input, nodes, vars);
                    WebServiceUiUtil.ensureVariablesUnique((XsdInstanceNode[])nodes.toArray(new XsdInstanceNode[nodes.size()]),
                                                           (ElementSymbol[])vars.toArray(new ElementSymbol[vars.size()]));
                }
            }
        }
        if (initSelect) {
            Output output = operation.getOutput();
            if (output != null) {
                XmlDocument doc = output.getXmlDocument();
                if (doc != null) {
                    proc.getBlock().addStatement(new CommandStatement(TransformationSqlHelper.createDefaultQuery(doc)));
                }
            }
        }
        if (initVar || initSelect) {
            TransformationHelper.setSelectSqlString(root, proc.toString(), true, transactionSource);
        }
    }

    /**
     * This method sets additional properties on a Web Services's Operation's Output object In particular, it calls
     * setXmlDocument() and setContentViaElement()
     * 
     * @param transformation
     * @param xmlDocument
     * @param txnSource
     * @since 5.0
     */
    public static void addXmlDocumentAsSource( final SqlTransformationMappingRoot transformation,
                                               final XmlDocument xmlDocument,
                                               final Object txnSource ) {
        // Ensure root's target is Web Service Operation object
        Object target = transformation.getTarget();
        if (!(target instanceof Operation)) {
            return;
        }
        Operation operation = (Operation)target;

        // Ensure there is only one new value and it's an XML document
        if (xmlDocument != null && operation.getOutput() != null) {
            boolean requiredStart = false;
            boolean succeeded = false;
            try {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Set Output Property Values", txnSource); //$NON-NLS-1$$

                // call setXmlDocument() on the Output
                if (operation.getOutput().getXmlDocument() == null) {
                    operation.getOutput().setXmlDocument(xmlDocument);
                } else if (operation.getOutput().getXmlDocument() != xmlDocument) {
                    operation.getOutput().setXmlDocument(xmlDocument);
                }
                // setContentViaElement() on the Output using the document's underlying XSD Element if it exists
                XSDElementDeclaration xsdElementDec = (XSDElementDeclaration)xmlDocument.getRoot().getXsdComponent();
                if (xsdElementDec != null) {
                    if (operation.getOutput().getContentElement() == null
                        || operation.getOutput().getContentElement() != xsdElementDec) {
                        operation.getOutput().setContentElement(xsdElementDec);
                    }
                }

                succeeded = true;
            } catch (Exception ex) {
                UTIL.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".addXmlDocumentAsSource()"); //$NON-NLS-1$  //$NON-NLS-2$
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * This method sets additional properties on a Web Services's Operation's Output object In particular, it calls
     * setXmlDocument() and setContentViaElement()
     * 
     * @param transformation
     * @param xmlDocument
     * @param txnSource
     * @since 5.0
     */
    public static void clearXmlDocumentAsSource( final SqlTransformationMappingRoot transformation,
                                                 final boolean forceClearContentViaElement,
                                                 final Object txnSource ) {
        TransformationHelper.clearXmlDocumentAsSource(transformation, forceClearContentViaElement, txnSource);
    }

    /**
     * This method sets the XML Document property on a Web Services's Operation's Output object In particular, it calls
     * setXmlDocument()
     * 
     * @param transformation
     * @param xmlDocument
     * @param txnSource
     * @since 5.0
     */
    public static void setXmlDocumentAsSource( final SqlTransformationMappingRoot transformation,
                                               final XmlDocument xmlDocument,
                                               final Object txnSource ) {
        // Ensure root's target is Web Service Operation object
        Object target = transformation.getTarget();
        if (!(target instanceof Operation)) {
            return;
        }
        Operation operation = (Operation)target;

        // Ensure there is only one new value and it's an XML document
        if (xmlDocument != null && operation.getOutput() != null) {
            boolean requiredStart = false;
            boolean succeeded = false;
            try {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Set Operation Output Xml Document", txnSource); //$NON-NLS-1$$

                // call setXmlDocument() on the Output
                operation.getOutput().setXmlDocument(xmlDocument);

                succeeded = true;
            } catch (Exception ex) {
                UTIL.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".addXmlDocumentAsSource()"); //$NON-NLS-1$  //$NON-NLS-2$
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Indicates if the specified file system resource is an XSD.
     * 
     * @param theFile the file being checked
     * @return <code>true</code>if an XSD file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isXsdFile( File theFile ) {
        return ModelFileUtil.isXsdFile(theFile);
    }

    /**
     * Indicates if the specified workspace resource is an XSD.
     * 
     * @param theFile the file being checked
     * @return <code>true</code>if an XSD file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isXsdFile( IFile theFile ) {
        return ModelUtil.isXsdFile(theFile);
    }

    private static boolean prefixAncestorNamesUntilUnique( ElementSymbol var1,
                                                           ElementSymbol var2,
                                                           XsdInstanceNode node1,
                                                           XsdInstanceNode node2 ) {
        String name1 = var1.getShortName();
        if (node1 != null) {
            name1 = node1.getName() + '_' + name1;
            var1.setShortName(WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX + name1);
            node1 = node1.getParent();
        }
        String name2 = var2.getShortName();
        if (node2 != null) {
            name2 = node2.getName() + '_' + name2;
            var2.setShortName(WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX + name2);
            node2 = node2.getParent();
        }
        if (!name1.equalsIgnoreCase(name2)) {
            return true;
        }
        if (node1 != null || node2 != null) {
            return prefixAncestorNamesUntilUnique(var1, var2, node1, node2);
        }
        return false;
    }

    private static void prefixNamespaceSegmentsUntilUnique( ElementSymbol var1,
                                                            ElementSymbol var2,
                                                            String namespace1,
                                                            String namespace2 ) {
        String name1 = var1.getShortName();
        if (namespace1 != null) {
            int ndx = namespace1.lastIndexOf('.');
            name1 = namespace1.substring(ndx + 1) + '_' + name1;
            var1.setShortName(WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX + name1);
            namespace1 = (ndx >= 0 ? namespace1.substring(0, ndx) : null);
        }
        String name2 = var2.getShortName();
        if (namespace2 != null) {
            int ndx = namespace2.lastIndexOf('.');
            name2 = namespace2.substring(ndx + 1) + '_' + name2;
            var2.setShortName(WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX + name2);
            namespace2 = (ndx >= 0 ? namespace2.substring(0, ndx) : null);
        }
        if (!name1.equalsIgnoreCase(name2)) {
            return;
        }
        prefixNamespaceSegmentsUntilUnique(var1, var2, namespace1, namespace2);
    }

    /**
     * View selection using system editor. Error dialog is shown if problems are encountered. If a multiple selection, only the
     * first object is opened.
     * 
     * @since 4.2
     */
    public static void viewFile( Shell theShell,
                                 ISelection theSelection ) {
        boolean result = false;
        Object file = "null"; //$NON-NLS-1$

        if ((theSelection != null) && !theSelection.isEmpty()) {
            file = SelectionUtilities.getSelectedObject(theSelection);

            if (file != null) {
                if (file instanceof IFile) {
                    result = UiUtil.openSystemEditor((IFile)file);
                } else if (file instanceof File) {
                    result = UiUtil.openSystemEditor((File)file);
                } else if (file instanceof IWebServiceResource) {
                    viewFile(theShell, new StructuredSelection(((IWebServiceResource)file).getFile()));
                }
            }
        }

        // display error dialog if problem showing system editor
        if (!result) {
            String txt = (file instanceof IFile) ? ((IFile)file).getFullPath().toString() : file.toString();

            MessageDialog.openError(theShell, UTIL.getString(PREFIX + "dialog.viewFile.title", new Object[] {txt}), //$NON-NLS-1$
                                    getString("dialog.viewFile.msg")); //$NON-NLS-1$
        }
    }

    // ===========================================================================================================================
    // Constructors

    /**
     * Don't allow construction.
     * 
     * @since 4.2
     */
    private WebServiceUiUtil() {
    }
}
