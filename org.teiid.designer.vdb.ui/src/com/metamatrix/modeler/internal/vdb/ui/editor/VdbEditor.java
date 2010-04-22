/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import static org.teiid.designer.core.util.StringConstants.EMPTY_STRING;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.EditorPart;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.vdb.ui.editor.TableAndButtonsGroup.RemoveButtonProvider;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * @since 4.0
 */
// TODO: problems, read-only, undo/redo, model dependencies
public final class VdbEditor extends EditorPart {

    private static final String DESCRIPTION_GROUP = i18n("descriptionGroup"); //$NON-NLS-1$
    private static final String MODELS_GROUP = i18n("modelsGroup"); //$NON-NLS-1$
    private static final String OTHER_FILES_GROUP = i18n("otherFilesGroup"); //$NON-NLS-1$

    static final String MODEL_COLUMN_NAME = i18n("modelColumnName"); //$NON-NLS-1$
    static final String FILE_COLUMN_NAME = i18n("fileColumnName"); //$NON-NLS-1$
    static final String PATH_COLUMN_NAME = i18n("pathColumnName"); //$NON-NLS-1$
    static final String SYNCHRONIZED_COLUMN_NAME = i18n("synchronizedColumnName"); //$NON-NLS-1$
    static final String VISIBLE_COLUMN_NAME = i18n("visibleColumnName"); //$NON-NLS-1$;
    static final String DATA_SOURCE_COLUMN_NAME = i18n("dataSourceColumnName"); //$NON-NLS-1$;
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;

    static final String SYNCHRONIZED_TOOLTIP = i18n("synchronizedTooltip"); //$NON-NLS-1$
    static final String UNSYNCHRONIZED_TOOLTIP = i18n("unsynchronizedTooltip"); //$NON-NLS-1$
    static final String SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP = i18n("synchronizationNotApplicableTooltip"); //$NON-NLS-1$

    static final String VISIBLE_TOOLTIP = i18n("visibleTooltip"); //$NON-NLS-1$
    static final String NOT_VISIBLE_TOOLTIP = i18n("notVisibleTooltip"); //$NON-NLS-1$

    static final String ADD_FILE_DIALOG_TITLE = i18n("addFileDialogTitle"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_MESSAGE = i18n("addFileDialogMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE = i18n("addFileDialogInvalidSelectionMessage"); //$NON-NLS-1$

    static final String CONFIRM_DIALOG_TITLE = i18n("confirmDialogTitle"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_MESSAGE = i18n("confirmSynchronizeMessage"); //$NON-NLS-1$
    static final String CONFIRM_SYNCHRONIZE_ALL_MESSAGE = i18n("confirmSynchronizeAllMessage"); //$NON-NLS-1$
    static final String CONFIRM_REMOVE_MESSAGE = i18n("confirmRemoveMessage"); //$NON-NLS-1$

    private static final String SYNCHRONIZE_ALL_BUTTON = i18n("synchronizeAllButton"); //$NON-NLS-1$

    private static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }

    Vdb vdb;
    StyledTextEditor textEditor;
    Button synchronizeAllButton;
    PropertyChangeListener vdbListener;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public void createPartControl( final Composite parent ) {
        // Compute height of description text area
        // For some reason, this can't be less than 4, or the vertical scrollbar widgets never appear (at least on a OSX)
        final GC gc = new GC(parent);
        gc.setFont(parent.getFont());
        final int height = Dialog.convertHeightInCharsToPixels(gc.getFontMetrics(), 4);
        gc.dispose();

        // Insert a ScrolledComposite so controls don't disappear if the panel shrinks
        final ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setLayout(new GridLayout());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        // Tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = scroller.getHorizontalBar();
        if (bar != null) bar.setIncrement(height / 4);
        bar = scroller.getVerticalBar();
        if (bar != null) bar.setIncrement(height / 4);

        final Composite pg = WidgetFactory.createPanel(scroller, SWT.NONE, GridData.FILL_BOTH, 1, 1);
        scroller.setContent(pg);

        final Group group = WidgetFactory.createGroup(pg, DESCRIPTION_GROUP, GridData.FILL_HORIZONTAL);
        this.textEditor = new StyledTextEditor(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 1;
        gridData.heightHint = height;
        gridData.minimumHeight = height;
        this.textEditor.setLayoutData(gridData);
        this.textEditor.setText(vdb.getDescription());
        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {

            public void documentAboutToBeChanged( final DocumentEvent event ) {
            }

            public void documentChanged( final DocumentEvent event ) {
                vdb.setDescription(textEditor.getText());
            }

        });

        final TextColumnProvider pathColumnProvider = new TextColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return PATH_COLUMN_NAME;
            }

            @Override
            public String getValue( final VdbEntry element ) {
                return element.getName().removeLastSegments(1).toString();
            }
        };
        final CheckBoxColumnProvider syncColumnProvider = new CheckBoxColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return SYNCHRONIZED_COLUMN_NAME;
            }

            @Override
            public String getToolTip( final VdbEntry element ) {
                if (element.getSynchronization() == Synchronization.Synchronized) return SYNCHRONIZED_TOOLTIP;
                if (element.getSynchronization() == Synchronization.NotSynchronized) return UNSYNCHRONIZED_TOOLTIP;
                return SYNCHRONIZATION_NOT_APPLICABLE_TOOLTIP;
            }

            @Override
            public Boolean getValue( final VdbEntry element ) {
                return element.getSynchronization() == Synchronization.Synchronized;
            }

            @Override
            public boolean isEditable( final VdbEntry element ) {
                return element.getSynchronization() == Synchronization.NotSynchronized;
            }

            @Override
            public void setValue( final VdbEntry element,
                                  final Boolean value ) {
                if (MessageDialog.openConfirm(pg.getShell(), CONFIRM_DIALOG_TITLE, CONFIRM_SYNCHRONIZE_MESSAGE)) element.synchronize(new NullProgressMonitor());
            }
        };
        final TextColumnProvider descriptionColumnProvider = new TextColumnProvider<VdbEntry>() {

            @Override
            public String getName() {
                return DESCRIPTION_COLUMN_NAME;
            }

            @Override
            public String getValue( final VdbEntry element ) {
                return element.getDescription();
            }

            @Override
            public boolean isEditable( final VdbEntry element ) {
                return true;
            }

            @Override
            public void setValue( final VdbEntry element,
                                  final String value ) {
                element.setDescription(value);
            }
        };
        final TableAndButtonsGroup modelsGroup = new TableAndButtonsGroup(
                                                                          pg,
                                                                          MODELS_GROUP,
                                                                          new DefaultTableProvider<VdbModelEntry>() {

                                                                              @Override
                                                                              public void doubleClicked( final VdbModelEntry element ) {
                                                                                  openEditor(element);
                                                                              }

                                                                              @Override
                                                                              public VdbModelEntry[] getElements() {
                                                                                  final Set<VdbModelEntry> modelEntries = vdb.getModelEntries();
                                                                                  return modelEntries.toArray(new VdbModelEntry[modelEntries.size()]);
                                                                              }

                                                                              @Override
                                                                              public boolean isDoubleClickSupported() {
                                                                                  return true;
                                                                              }
                                                                          },
                                                                          new TextColumnProvider<VdbModelEntry>() {

                                                                              @Override
                                                                              public Image getImage( final VdbModelEntry element ) {
                                                                                  return ModelIdentifier.getModelImage(element.findFileInWorkspace());
                                                                              }

                                                                              @Override
                                                                              public String getName() {
                                                                                  return MODEL_COLUMN_NAME;
                                                                              }

                                                                              @Override
                                                                              public String getValue( final VdbModelEntry element ) {
                                                                                  return element.getName().lastSegment();
                                                                              }
                                                                          }, pathColumnProvider, syncColumnProvider,
                                                                          new CheckBoxColumnProvider<VdbModelEntry>() {

                                                                              @Override
                                                                              public String getName() {
                                                                                  return VISIBLE_COLUMN_NAME;
                                                                              }

                                                                              @Override
                                                                              public String getToolTip( final VdbModelEntry element ) {
                                                                                  return element.isVisible() ? VISIBLE_TOOLTIP : NOT_VISIBLE_TOOLTIP;
                                                                              }

                                                                              @Override
                                                                              public Boolean getValue( final VdbModelEntry element ) {
                                                                                  return element.isVisible();
                                                                              }

                                                                              @Override
                                                                              public boolean isEditable( final VdbModelEntry element ) {
                                                                                  return true;
                                                                              }

                                                                              @Override
                                                                              public void setValue( final VdbModelEntry element,
                                                                                                    final Boolean value ) {
                                                                                  element.setVisible(value);
                                                                              }
                                                                          }, new TextColumnProvider<VdbModelEntry>() {

                                                                              @Override
                                                                              public String getName() {
                                                                                  return DATA_SOURCE_COLUMN_NAME;
                                                                              }

                                                                              @Override
                                                                              public String getValue( final VdbModelEntry element ) {
                                                                                  final String value = element.getDataSource();
                                                                                  return value == null ? EMPTY_STRING : value;
                                                                              }

                                                                              @Override
                                                                              public boolean isEditable( final VdbModelEntry element ) {
                                                                                  return true;
                                                                              }

                                                                              @Override
                                                                              public void setValue( final VdbModelEntry element,
                                                                                                    final String value ) {
                                                                                  element.setDataSource(value);
                                                                              }
                                                                          }, descriptionColumnProvider);
        final ModelLabelProvider modelLabelProvider = new ModelLabelProvider();
        final ISelectionStatusValidator validator = new ISelectionStatusValidator() {

            public IStatus validate( final Object[] selection ) {
                for (int ndx = selection.length; --ndx >= 0;)
                    if (selection[ndx] instanceof IContainer) return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0,
                                                                                ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE, null);
                return new Status(IStatus.OK, VdbUiConstants.PLUGIN_ID, 0, EMPTY_STRING, null);
            }
        };
        modelsGroup.add(modelsGroup.new AddButtonProvider() {

            @Override
            protected void add() {
                final ViewerFilter filter = new ViewerFilter() {

                    @Override
                    public boolean select( final Viewer viewer,
                                           final Object parent,
                                           final Object element ) {
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (!ModelUtilities.isModelFile(file) && !ModelUtil.isXsdFile(file)) return false;
                        for (final VdbModelEntry modelEntry : vdb.getModelEntries())
                            if (file.equals(modelEntry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                      ADD_FILE_DIALOG_MESSAGE,
                                                                                      true,
                                                                                      null,
                                                                                      new ModelingResourceFilter(filter),
                                                                                      validator,
                                                                                      modelLabelProvider);
                for (final Object model : models)
                    vdb.addModelEntry(((IFile)model).getFullPath(), new NullProgressMonitor());
            }
        });
        final RemoveButtonProvider removeButtonProvider = modelsGroup.new RemoveButtonProvider() {

            @Override
            public void selected( final IStructuredSelection selection ) {
                if (!MessageDialog.openConfirm(pg.getShell(), CONFIRM_DIALOG_TITLE, CONFIRM_REMOVE_MESSAGE)) return;
                for (final Object element : selection.toList())
                    vdb.removeEntry((VdbEntry)element);
            }
        };
        modelsGroup.add(removeButtonProvider);
        modelsGroup.setInput(vdb);

        final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
        final TableAndButtonsGroup otherFilesGroup = new TableAndButtonsGroup(
                                                                              pg,
                                                                              OTHER_FILES_GROUP,
                                                                              new DefaultTableProvider<VdbEntry>() {

                                                                                  @Override
                                                                                  public VdbEntry[] getElements() {
                                                                                      final Set<VdbEntry> entries = vdb.getEntries();
                                                                                      return entries.toArray(new VdbModelEntry[entries.size()]);
                                                                                  }
                                                                              },
                                                                              new TextColumnProvider<VdbEntry>() {

                                                                                  @Override
                                                                                  public Image getImage( final VdbEntry element ) {
                                                                                      return workbenchLabelProvider.getImage(element.findFileInWorkspace());
                                                                                  }

                                                                                  @Override
                                                                                  public String getName() {
                                                                                      return FILE_COLUMN_NAME;
                                                                                  }

                                                                                  @Override
                                                                                  public String getValue( final VdbEntry element ) {
                                                                                      return element.getName().lastSegment();
                                                                                  }
                                                                              }, pathColumnProvider, syncColumnProvider,
                                                                              descriptionColumnProvider);
        otherFilesGroup.add(otherFilesGroup.new AddButtonProvider() {

            @Override
            protected void add() {
                final ViewerFilter filter = new ViewerFilter() {

                    @Override
                    public boolean select( final Viewer viewer,
                                           final Object parent,
                                           final Object element ) {
                        if (element instanceof IContainer) return true;
                        final IFile file = (IFile)element;
                        if (ModelUtilities.isModelFile(file) || ModelUtil.isXsdFile(file) || ModelUtil.isVdbArchiveFile(file)) return false;
                        for (final VdbEntry entry : vdb.getEntries())
                            if (file.equals(entry.findFileInWorkspace())) return false;
                        return true;
                    }
                };
                final Object[] files = WidgetUtil.showWorkspaceObjectSelectionDialog(ADD_FILE_DIALOG_TITLE,
                                                                                     ADD_FILE_DIALOG_MESSAGE,
                                                                                     true,
                                                                                     null,
                                                                                     new ModelingResourceFilter(filter),
                                                                                     validator,
                                                                                     modelLabelProvider);
                for (final Object file : files)
                    vdb.addEntry(((IFile)file).getFullPath(), new NullProgressMonitor());
            }
        });
        otherFilesGroup.add(removeButtonProvider);
        otherFilesGroup.setInput(vdb);
        synchronizeAllButton = WidgetFactory.createButton(pg, SYNCHRONIZE_ALL_BUTTON, GridData.HORIZONTAL_ALIGN_CENTER);
        synchronizeAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                if (!MessageDialog.openConfirm(pg.getShell(), CONFIRM_DIALOG_TITLE, CONFIRM_SYNCHRONIZE_ALL_MESSAGE)) return;
                vdb.synchronize(new NullProgressMonitor());
                modelsGroup.getTable().getViewer().refresh();
                otherFilesGroup.getTable().getViewer().refresh();
            }
        });
        synchronizeAllButton.setEnabled(!vdb.isSynchronized());

        group.setFont(modelsGroup.getGroup().getFont());

        // pack and resize:
        pg.pack(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        if (vdb != null) try {
            vdb.removeChangeListener(vdbListener);
            vdb.close();
            if (textEditor != null) textEditor.dispose();
        } catch (final Exception err) {
            VdbUiConstants.Util.log(err);
            WidgetUtil.showError(err);
        }
        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        vdb.save(monitor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // TODO: implement
    }

    /**
     * @return the VDB being edited
     */
    public Vdb getVdb() {
        return vdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) {
        final IFile file = ((IFileEditorInput)input).getFile();
        vdb = new Vdb(file, new NullProgressMonitor());
        vdbListener = new PropertyChangeListener() {

            @Override
            public void propertyChange( final PropertyChangeEvent event ) {
                vdbNotification(event.getPropertyName());
            }
        };
        vdb.addChangeListener(vdbListener);
        setSite(site);
        setInput(input);
        setPartName(file.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return vdb.isModified();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    void openEditor( final VdbEntry entry ) {
        try {
            IDE.openEditor(UiUtil.getWorkbenchPage(), entry.findFileInWorkspace());
        } catch (final Exception error) {
            throw new RuntimeException(error);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

    void vdbNotification( final String property ) {
        if (Vdb.CLOSED.equals(property)) return;
        synchronizeAllButton.setEnabled(vdb.isModified());
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }
}
