/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.xerces.util.EncodingMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationDescriptor;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * XsdTextEditorPage is a ModelEditorPage for displaying the source of an XMLSchema file.
 */
public class XsdTextEditorPage extends TextEditor implements ModelEditorPage, ISelectionProvider, IDocumentListener {

    private static final String NAME = ModelerXsdUiConstants.Util.getString("xsdTextEditor.name"); //$NON-NLS-1$
    private static final String TOOLTIP = ModelerXsdUiConstants.Util.getString("xsdTextEditor.tooltip"); //$NON-NLS-1$

    protected ISourceViewer sourceViewer;
    protected Timer timer = new Timer();
    protected TimerTask timerTask;
    protected boolean timerCancelled = false;
    protected ModelResource modelResource;

    /** This keeps track of the root object of the model. */
    protected XSDSchema xsdSchema;
    /** This is the model resource for the current xsd file being displayed in the editor */
    private Resource xsdResource;

    /** This keeps track eclipse text document. */
    protected IDocument document;

    /** The {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this editor. */
    protected Collection selectionChangedListeners = new ArrayList();
    /** This keeps track of the selection of the editor as a whole. */
    protected ISelection editorSelection;

    /** This listens to selections from other viewers. */
    protected ISelectionChangedListener selectionChangedListener;

    protected INotifyChangedListener notifyChangedListener = new INotifyChangedListener() {
        public void notifyChanged( Notification notification ) {
            if (getXsdSchema() != null) {
                if (notification instanceof SourcedNotification) {
                    if (((SourcedNotification)notification).getSource() != null) {
                        // then it was not produced by this editor

                        // Still need to check the damn notifier and be sure that we only respond to changes
                        // to this resource. Else this listener will respond to every sourced notification. BAD!!!!
                        Object changedObject = ModelerCore.getModelEditor().getChangedObject(notification);
                        if (changedObject != null) {
                            ModelResource mr = null;
                            if (changedObject instanceof EObject) {
                                mr = ModelUtilities.getModelResourceForModelObject((EObject)changedObject);
                            } else if (changedObject instanceof ModelResource) {
                                mr = (ModelResource)changedObject;
                            }

                            if (mr != null && mr.equals(modelResource)) {
                                handleStructuredModelChange(true);
                            } else if (changedObject.equals(xsdSchema)) {
                                handleStructuredModelChange(true);
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * Construct an instance of XsdTextEditorPage.
     */
    public XsdTextEditorPage() {
        super();
    }

    /**
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    @Override
    protected ISourceViewer createSourceViewer( Composite parent,
                                                IVerticalRuler ruler,
                                                int styles ) {
        {
            final ISourceViewer result = super.createSourceViewer(parent, ruler, styles);
            result.getTextWidget().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown( MouseEvent event ) {
                    handleSourceCaretPosition();
                }
            });
            result.getTextWidget().addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed( KeyEvent event ) {
                    switch (event.keyCode) {
                        case SWT.ARROW_UP:
                        case SWT.ARROW_DOWN:
                        case SWT.ARROW_LEFT:
                        case SWT.ARROW_RIGHT:
                        case SWT.PAGE_UP:
                        case SWT.PAGE_DOWN: {
                            handleSourceCaretPosition();
                            break;
                        }
                    }
                }
            });
            sourceViewer = result;
            return result;
        }
    }

    protected void initDocument() {
        boolean requiredStart = ModelerCore.startTxn(false, false, "Create XSD Editor Model", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            createModel();
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        String encoding = determineEncoding();
        IEncodingSupport encodingSupport = (IEncodingSupport)getAdapter(IEncodingSupport.class);
        if (encodingSupport != null && encoding != null) {
            encodingSupport.setEncoding(encoding);
        }

        document = getDocumentProvider().getDocument(getEditorInput());
        document.addDocumentListener(this);

        modelResource = ModelUtilities.getModelResource(xsdResource, false);
    }

    protected void createModel() {
        // Do the work within an operation because this is a long running activity that modifies the workbench.
        //
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            // This is the method that gets invoked when the operation runs.
            @Override
            protected void execute( IProgressMonitor progressMonitor ) {
                try {
                    progressMonitor.beginTask(ModelerXsdUiConstants.Util.getString("XsdEditor._22"), 10); //$NON-NLS-1$

                    IFileEditorInput modelFile = (IFileEditorInput)getEditorInput();
                    IFile file = modelFile.getFile();

                    ResourceSet resourceSet = ModelerCore.getModelContainer();
                    resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_PROGRESS_MONITOR, progressMonitor);
                    createResource(file.getLocation().toString(), resourceSet);
                    resourceSet.getLoadOptions().remove(XSDResourceImpl.XSD_PROGRESS_MONITOR);

                    progressMonitor.worked(1);
                    final String msg = ModelerXsdUiConstants.Util.getString("_UI_Validating_message"); //$NON-NLS-1$
                    progressMonitor.subTask(msg);

                    // only validate if user pref is on and no diagnostics exist
                    if (isValidateSchemaPreferenceOn() && getXsdSchema().getAllDiagnostics().isEmpty()) {
                        getXsdSchema().validate();
                        getXsdSchema().eResource().setModified(false);
                    }

                    progressMonitor.worked(1);
                    progressMonitor.subTask(ModelerXsdUiConstants.Util.getString("_UI_ReportingErrors_message")); //$NON-NLS-1$

                    // must do this to get the DOM objects properly synched with the display
                    handleDocumentChange(false);
                    // handleStructuredModelChange();
                } catch (Throwable t) {
                    ModelerXsdUiConstants.Util.log(t);
                } finally {
                    progressMonitor.done();
                }
            }
        };

        try {
            // This runs the operation, and shows progress.
            // (It appears to be a bad thing to fork this onto another thread.)
            new ProgressMonitorDialog(getSite().getShell()).run(false, false, operation);
        } catch (Exception exception) {
            ModelerXsdUiConstants.Util.log(exception);
        }
    }

    /**
     * Indicates if the preference for validating schema has been set to perform validation.
     * 
     * @return <code>true</code>if the schema should be validated; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean isValidateSchemaPreferenceOn() {
        boolean result = false;
        String pref = getValidateSchemaUserPreference();

        if (pref.equals(ValidationDescriptor.ERROR) || pref.equals(ValidationDescriptor.WARNING)
            || pref.equals(ValidationDescriptor.INFO)) {
            result = true;
        }

        return result;
    }

    /**
     * Obtains the user preference for schema validation.
     * 
     * @return the preference
     * @since 4.2
     * @see ValidationDescriptor#ERROR
     * @see ValidationDescriptor#WARNING
     * @see ValidationDescriptor#INFO
     * @see ValidationDescriptor#IGNORE
     * @see ValidationDescriptor#NOT_SET
     */
    private String getValidateSchemaUserPreference() {
        IEclipsePreferences prefs = ModelerCore.getPreferences(ModelerCore.PLUGIN_ID);
        
        String value = prefs.get(ValidationPreferences.XSD_MODEL_VALIDATION, null);
        
        if (value == null) {
            prefs = ModelerCore.getDefaultPreferences(ModelerCore.PLUGIN_ID);
            value = prefs.get(ValidationPreferences.XSD_MODEL_VALIDATION, ValidationDescriptor.WARNING);
        }

        return value;
    }

    /**
     * Obtains the {@link IMarker} severity attribute level for the validate schema user preference. This is the highest level of
     * severity the user wants to see.
     * 
     * @return the severity level or -1 if user preference is set to not validate the schema
     * @since 4.2
     */
    private int getValidateSchemaUserPreferenceMarkerSeverity() {
        int result = -1;
        String pref = getValidateSchemaUserPreference();

        if (pref.equals(ValidationDescriptor.ERROR)) {
            result = IMarker.SEVERITY_ERROR;
        } else if (pref.equals(ValidationDescriptor.WARNING)) {
            result = IMarker.SEVERITY_WARNING;
        } else if (pref.equals(ValidationDescriptor.INFO)) {
            result = IMarker.SEVERITY_INFO;
        }

        return result;
    }

    protected void createResource( String uri,
                                   ResourceSet resourceSet ) {
        extendedCreateResource(uri, resourceSet);
    }

    protected void extendedCreateResource( String uri,
                                           ResourceSet resourceSet ) {
        resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION, Boolean.TRUE);
        try {
            XSDResourceImpl xsdResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(uri), true);
            xsdSchema = xsdResource.getSchema();

            // code to close the editor when the resource is deleted
            getDocumentProvider().addElementStateListener(new IElementStateListener() {
                public void elementDirtyStateChanged( Object element,
                                                      boolean isDirty ) {
                }

                public void elementContentAboutToBeReplaced( Object element ) {
                }

                public void elementContentReplaced( Object element ) {
                }

                public void elementDeleted( Object element ) {
                    Display display = getSite().getShell().getDisplay();
                    display.asyncExec(new Runnable() {
                        public void run() {
                            if (sourceViewer != null) {
                                getSite().getPage().closeEditor(XsdTextEditorPage.this, false);
                            }
                        }
                    });
                }

                public void elementMoved( Object originalElement,
                                          Object movedElement ) {
                }
            });
        } catch (Exception exception) {
            ModelerXsdUiConstants.Util.log(exception);
        }
    }

    protected void handleDocumentChange( boolean setModified ) {
        if (sourceViewer != null) {
            try {
                XSDParser xsdParser = new XSDParser(null);
                IDocument iDocument = sourceViewer.getDocument();
                if (iDocument != null) {
                    String documentContent = iDocument.get();
                    byte[] bytes = documentContent.getBytes();
                    xsdParser.parse(new ByteArrayInputStream(bytes));
                    xsdParser.setSchema(getXsdSchema());

                    // only validate if user pref is on and no diagnostics exist
                    if (isValidateSchemaPreferenceOn() && getXsdSchema().getAllDiagnostics().isEmpty()) {
                        getXsdSchema().validate();
                    }

                    if (setModified) {
                        getXsdSchema().eResource().setModified(true);
                    }

                    handleDiagnostics(null);
                }
            } catch (Exception exception) {
                ModelerXsdUiConstants.Util.log(exception);
            }
        }
    }

    protected boolean handledStructuredModelChange = false;

    protected void handleStructuredModelChange( boolean setModified ) {
        final IDocument document = getDocumentProvider().getDocument(getEditorInput());
        if (getXsdSchema().getElement() == null) {
            getXsdSchema().updateElement();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            getXsdSchema().eResource().save(out, null);
            String encoding = determineEncoding();
            String newContent = encoding == null ? out.toString() : out.toString(encoding);
            String oldContent = document.get();

            int startIndex = 0;
            while (startIndex < newContent.length() && startIndex < oldContent.length()
                   && newContent.charAt(startIndex) == oldContent.charAt(startIndex)) {
                ++startIndex;
            }
            int newEndIndex = newContent.length() - 1;
            int oldEndIndex = oldContent.length() - 1;
            while (newEndIndex >= startIndex && oldEndIndex >= startIndex
                   && newContent.charAt(newEndIndex) == oldContent.charAt(oldEndIndex)) {
                --newEndIndex;
                --oldEndIndex;
            }

            final int start = startIndex;
            final String replacement = newContent.substring(startIndex, newEndIndex + 1);
            final int length = oldEndIndex - startIndex + 1;
            handledStructuredModelChange = true;
            UiUtil.runInSwtThread(new Runnable() {
                public void run() {
                    try {
                        document.replace(start, length, replacement);
                    } catch (Exception exception) {
                        ModelerXsdUiConstants.Util.log(exception);
                    }
                }
            }, false);
            if (setModified) {
                getXsdSchema().eResource().setModified(true);
            }
        } catch (Exception exception) {
            ModelerXsdUiConstants.Util.log(exception);
        }
    }

    protected String determineEncoding() {
        String encoding = (String)((XSDResourceImpl)getXsdSchema().eResource()).getDefaultSaveOptions().get(XSDResourceImpl.XSD_ENCODING);
        if (encoding != null && EncodingMap.getIANA2JavaMapping(encoding) != null) {
            encoding = EncodingMap.getIANA2JavaMapping(encoding);
        }
        return encoding;
    }

    /**
     * Called when the mouse is clicked inside the document.
     */
    protected void handleSourceCaretPosition() {
        if (sourceViewer != null) {
            int offset = sourceViewer.getTextWidget().getCaretOffset();
            Element element = getXsdSchema().getElement();
            if (element != null) {
                IDocument document = sourceViewer.getDocument();
                int line = 0;
                int lineOffset = 0;
                try {
                    line = document.getLineOfOffset(offset);
                    lineOffset = document.getLineOffset(line);
                } catch (BadLocationException exception) {
                }
                int column = offset - lineOffset;
                // System.out.println("[" + line + "," + column + "]");

                Element bestElement = findBestElement(element, line + 1, column + 1);
                if (bestElement != null) {
                    handleSelectedNodes(Collections.singleton(bestElement));
                }
            }
        }
    }

    /**
     * Called by handleSourceCaretPosition to find an Element for a given location in the document
     * 
     * @param element
     * @param line
     * @param column
     * @return
     */
    protected Element findBestElement( Element element,
                                       int line,
                                       int column ) {
        int startLine = XSDParser.getStartLine(element);
        int startColumn = XSDParser.getStartColumn(element);
        int endLine = XSDParser.getEndLine(element);
        int endColumn = XSDParser.getEndColumn(element);

        Element candidate = null;
        if ((line == startLine ? column >= startColumn : line > startLine)
            && (line == endLine ? column <= endColumn : line < endLine)) {
            candidate = element;
            for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element)child;
                    Element betterCandidate = findBestElement(childElement, line, column);
                    if (betterCandidate != null) {
                        candidate = betterCandidate;
                        break;
                    }
                }
            }
        }
        return candidate;
    }

    public void handleSelectedNodes( Collection nodes ) {
        Collection selection = new ArrayList();
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            XSDConcreteComponent bestXSDConcreteComponent = getXsdSchema().getCorrespondingComponent(node);
            if (bestXSDConcreteComponent != null) {
                boolean add = true;
                for (XSDConcreteComponent parent = bestXSDConcreteComponent; parent != null; parent = parent.getContainer()) {
                    if (selection.contains(parent)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    XSDConcreteComponent container = bestXSDConcreteComponent.getContainer();
                    if (container instanceof XSDParticle || container instanceof XSDAttributeUse) {
                        bestXSDConcreteComponent = container;
                    }
                    selection.add(bestXSDConcreteComponent);
                }
            }
        }
        if (!selection.isEmpty()) {
            ISelection newSelection = new StructuredSelection(selection.toArray());
            handleContentOutlineSelectionForTextEditor(newSelection, false);
        }
    }

    /**
     * This deals with how we want selection in the outliner to affect the text editor.
     */
    public void handleContentOutlineSelectionForTextEditor( ISelection selection,
                                                            boolean reveal ) {
        Object o = ((IStructuredSelection)selection).iterator().next();
        if (o instanceof XSDConcreteComponent) {
            XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)o;
            if (xsdConcreteComponent instanceof XSDParticle) {
                XSDParticle xsdParticle = (XSDParticle)xsdConcreteComponent;
                XSDConcreteComponent content = xsdParticle.getContent();
                if (content != null) {
                    xsdConcreteComponent = content;
                }
            }

            Element element = xsdConcreteComponent.getElement();
            if (element != null) {
                try {
                    IDocument document = getDocumentProvider().getDocument(getEditorInput());
                    int startLine = XSDParser.getStartLine(element);
                    int startColumn = XSDParser.getStartColumn(element);
                    int endLine = XSDParser.getEndLine(element);
                    int endColumn = XSDParser.getEndColumn(element);

                    int startOffset = document.getLineOffset(startLine - 1);
                    startOffset += startColumn - 1;
                    int endOffset = document.getLineOffset(endLine - 1);
                    endOffset += endColumn - 1;
                    if (startLine == endLine) {
                        setHighlightRange(startOffset, endOffset - startOffset, false);
                        if (reveal) {
                            selectAndReveal(startOffset, endOffset - startOffset);
                        }
                    } else {
                        setHighlightRange(startOffset, endOffset - startOffset, reveal);
                    }
                } catch (Exception exception) {
                    ModelerXsdUiConstants.Util.log(exception);
                }
            }
        }

    }

    protected XSDSchema getXsdSchema() {
        if (xsdSchema != null && xsdSchema.eResource() != null) {
            return xsdSchema;
        }

        if (xsdResource != null && xsdResource instanceof XSDResourceImpl) {
            XSDResourceImpl rsrc = (XSDResourceImpl)xsdResource;
            if (rsrc != null) {
                xsdSchema = rsrc.getSchema();
            }
        }

        return xsdSchema;
    }

    protected void initializeMarkerPosition( IMarker marker,
                                             XSDDiagnostic xsdDiagnostic ) throws CoreException {
        Node node = xsdDiagnostic.getNode();
        if (node != null && node.getNodeType() == Node.ATTRIBUTE_NODE) {
            node = ((Attr)node).getOwnerElement();
        }
        if (node != null && /* !xsdDiagnostic.isSetLine() && */
        XSDParser.getUserData(node) != null) {
            int startLine = XSDParser.getStartLine(node) - 1;
            int startColumn = XSDParser.getStartColumn(node);
            int endLine = XSDParser.getEndLine(node) - 1;
            int endColumn = XSDParser.getEndColumn(node);

            marker.setAttribute(IMarker.LINE_NUMBER, startLine);

            try {
                IDocument document = getDocumentProvider().getDocument(getEditorInput());
                marker.setAttribute(IMarker.CHAR_START, document.getLineOffset(startLine) + startColumn - 1);
                marker.setAttribute(IMarker.CHAR_END, document.getLineOffset(endLine) + endColumn - 1);
            } catch (BadLocationException exception) {
            }
        } else {
            marker.setAttribute(IMarker.LINE_NUMBER, xsdDiagnostic.getLine());
        }
    }

    protected void handleDiagnostics( IProgressMonitor progressMonitor ) {
        if (!isValidateSchemaPreferenceOn()) {
            return;
        }

        if (progressMonitor == null) {
            // Do the work within an operation because this is a long running activity that modifies the workbench.
            //
            IWorkspaceRunnable operation = new IWorkspaceRunnable() {

                // This is the method that gets invoked when the operation runs.
                public void run( IProgressMonitor localProgressMonitor ) {
                    handleDiagnostics(localProgressMonitor);
                }
            };

            try {
                ResourcesPlugin.getWorkspace().run(operation, new NullProgressMonitor());
                // getSite().getWorkbenchWindow().run(false, false, operation);
            } catch (Exception exception) {
                ModelerXsdUiConstants.Util.log(exception);
            }
        } else {
            XSDConcreteComponent newSelection = null;
            try {
                // the userPref will be used to determine if the marker severity should be changed
                int userPref = getValidateSchemaUserPreferenceMarkerSeverity();

                // I assume that the input is a file object.
                //
                IFileEditorInput modelFile = (IFileEditorInput)getEditorInput();
                IFile file = modelFile.getFile();

                IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
                Collection deletableMarkers = new ArrayList(Arrays.asList(markers));

                for (Iterator xsdDiagnostics = getXsdSchema().getAllDiagnostics().iterator(); xsdDiagnostics.hasNext();) {
                    XSDDiagnostic xsdDiagnostic = (XSDDiagnostic)xsdDiagnostics.next();
                    String uriReferencePath = getXsdSchema().eResource().getURIFragment(xsdDiagnostic);

                    IMarker marker = null;
                    for (int i = 0; i < markers.length; ++i) {
                        if (markers[i].getAttribute(XSDDiagnostic.URI_FRAGMENT_ATTRIBUTE,
                                                    ModelerXsdUiConstants.Util.getString("XsdEditor._29")).equals(uriReferencePath)) //$NON-NLS-1$
                        {
                            marker = markers[i];
                            deletableMarkers.remove(marker);
                            break;
                        }
                    }

                    if (marker == null) {
                        marker = file.createMarker(XSDDiagnostic.MARKER);
                        marker.setAttribute(XSDDiagnostic.URI_FRAGMENT_ATTRIBUTE, uriReferencePath);
                    }

                    initializeMarkerPosition(marker, xsdDiagnostic);

                    marker.setAttribute(IMarker.MESSAGE, xsdDiagnostic.getMessage());

                    switch (xsdDiagnostic.getSeverity().getValue()) {
                        case XSDDiagnosticSeverity.FATAL:
                        case XSDDiagnosticSeverity.ERROR: {
                            if (newSelection == null) {
                                newSelection = xsdDiagnostic.getPrimaryComponent();
                            }
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                            break;
                        }
                        case XSDDiagnosticSeverity.WARNING: {
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                            break;
                        }
                        case XSDDiagnosticSeverity.INFORMATION: {
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                            break;
                        }
                    }

                    // if necessary adjust severity based on user preferences
                    Object markerSeverity = marker.getAttribute(IMarker.SEVERITY);

                    if ((userPref != -1) && (markerSeverity != null) && (markerSeverity instanceof Integer)) {
                        int severity = ((Integer)markerSeverity).intValue();

                        if (severity > userPref) {
                            do {
                                --severity;
                            } while (severity > userPref);

                            marker.setAttribute(IMarker.SEVERITY, severity);
                        }
                    }
                }

                for (Iterator i = deletableMarkers.iterator(); i.hasNext();) {
                    IMarker marker = (IMarker)i.next();
                    marker.delete();
                }
            } catch (Exception exception) {
                ModelerXsdUiConstants.Util.log(exception);
            }

            // This will refresh the status.
            //
            if (editorSelection != null) {
                setSelectionInternal(editorSelection);
            }
            // This is the startup case.
            //
            else if (newSelection != null) {
                final IStructuredSelection errorSelection = new StructuredSelection(newSelection);
                getSite().getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        setSelectionInternal(errorSelection);
                        handleSourceCaretPosition();
                    }
                });
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     */
    public boolean canDisplay( IEditorInput input ) {
        if (input instanceof IFileEditorInput) {
            return ModelUtil.isXsdFile(((IFileEditorInput)input).getFile());
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext( Object input ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getActionBarContributor()
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public Control getControl() {
        return super.getSourceViewer().getTextWidget();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        if (selectionChangedListener == null) {
            // Create the listener on demand.
            selectionChangedListener = new ISelectionChangedListener() {
                // This just notifies those things that are affected by the section.
                public void selectionChanged( SelectionChangedEvent selectionChangedEvent ) {
                    setSelection(selectionChangedEvent.getSelection());
                }
            };
        }
        return selectionChangedListener;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    public INotifyChangedListener getNotifyChangedListener() {
        return notifyChangedListener;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input ) {
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input,
                             boolean forceRefresh ) {

    }

    /**
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#initializeEditorPage()
     * @since 5.0.2
     */
    public void initializeEditorPage() {
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    public void setLabelProvider( ILabelProvider provider ) {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        handleStructuredModelChange(false);
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        super.init(site, input);
        if (input instanceof IFileEditorInput) {
            // get the XSD resource.
            try {
                // Get the IFile associated with the editor
                final IResource xsdFile = ((IFileEditorInput)input).getFile();
                if (xsdFile == null) {
                    final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }

                // Get the EMF resource for the IFile in the workspace
                final String xsdLocation = xsdFile.getLocation().toString();
                final URI xsdUri = URI.createFileURI(xsdLocation);
                xsdResource = ModelerCore.getModelContainer().getResource(xsdUri, true);

                // The resource must exist in the container
                if (xsdResource == null) {
                    final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_1", input.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }

                initDocument();
                ModelUtilities.addNotifyChangedListener(notifyChangedListener);

            } catch (ModelWorkspaceException e) {
                final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            } catch (CoreException e) {
                final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            }

        } else {
            throw new PartInitException(
                                        ModelerXsdUiConstants.Util.getString("XsdEditor.Invalid_Input__Must_be_IFileEditorInput._33")); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    @Override
    public String getTitle() {
        return NAME;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return TOOLTIP;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     */
    public void setTitleText( String title ) {
        // do nothing;
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        if (listener != null && !selectionChangedListeners.contains(listener)) {
            selectionChangedListeners.add(listener);
        }
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.remove(listener);
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
     */
    public ISelection getSelection() {
        return editorSelection;
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection. Calling this
     * result will notify the listeners.
     */
    public void setSelection( ISelection selection ) {
        if (SelectionUtilities.isSingleSelection(selection)) {
            if (SelectionUtilities.isAllEObjects(selection)) {
                EObject selectedObject = SelectionUtilities.getSelectedEObject(selection);
                if (this.xsdResource.equals(selectedObject.eResource())) {
                    editorSelection = selection;
                    handleContentOutlineSelectionForTextEditor(selection, true);
                }
            }
        }
    }

    void setSelectionInternal( ISelection selection ) {
        editorSelection = selection;
        for (Iterator iter = this.selectionChangedListeners.iterator(); iter.hasNext();) {
            SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
            ((ISelectionChangedListener)iter.next()).selectionChanged(event);
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        cancelRunningTimer();
        if (document != null) document.removeDocumentListener(this);
        ModelUtilities.removeNotifyChangedListener(notifyChangedListener);
        notifyChangedListener = null;
        if (getSite() != null) super.dispose();

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#preDispose()
     */
    public void preDispose() {
        ModelUtilities.removeNotifyChangedListener(notifyChangedListener);
        if (document != null) document.removeDocumentListener(this);
        notifyChangedListener = null;
        cancelRunningTimer();
        if (timer != null) {
            timer.cancel();
        }
    }

    private boolean cancelRunningTimer() {
        if (!timerCancelled) {
            timerCancelled = true;
            if (timerTask != null) {
                timerTask.cancel();
            }
            return true;
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     */
    public void updateReadOnlyState( boolean isReadOnly ) {
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        if (getEditorInput() != null) {
            ModelResourceEvent event = (ModelResourceEvent)obj;
            final IResource file = event.getResource();
            final IFile currentFile = ((FileEditorInput)getEditorInput()).getFile();
            if (file.equals(currentFile)) {
                if (event.getType() == ModelResourceEvent.RELOADED || event.getType() == ModelResourceEvent.CHANGED) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            setInput(getEditorInput());
                            handleStructuredModelChange(true);
                        }
                    });
                }
            }
        }
    }

    /**
     * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
     * @since 4.2
     */
    public void documentAboutToBeChanged( DocumentEvent documentEvent ) {
    }

    /**
     * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
     * @since 4.2
     */
    public void documentChanged( final DocumentEvent documentEvent ) {
        try {
            // This is need for the Properties view.
            //
            cancelRunningTimer();

            if (handledStructuredModelChange) {
                handledStructuredModelChange = false;
                // defect 18435 -- make sure we don't infinitely recurse by calling
                // handleDocumentChange(true), which eventually will cause things
                // to come back here.
                // the following line displays errors, but they won't get updated
                // unless the user types a change in the source editor.
                handleDiagnostics(null);

            } else {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        getSite().getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                handleDocumentChange(true);
                            }
                        });
                    }
                };
                timerCancelled = false;
                timer.schedule(timerTask, 1000);
            }
        } catch (Exception exception) {
            ModelerXsdUiConstants.Util.log(exception);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openComplete()
     * @since 4.2
     */
    public void openComplete() {
        // Default Implementation
    }

    /**
     * @return False.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst( IEditorInput input ) {
        return false;
    }
}
