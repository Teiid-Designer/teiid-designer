/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.AbstractResourceCallback;

/**
 *
 */
public class DeleteResourcesWizard extends RefactoringWizard {

    /**
     * @param refactoring
     * @param pageTitle
     */
    public DeleteResourcesWizard(DeleteResourcesRefactoring refactoring, String pageTitle) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(pageTitle);
    }

    @Override
    protected void addUserInputPages() {
        List<IResource> resources = ((AbstractResourcesRefactoring)getRefactoring()).getResources();
        addPage(new DeleteResourcesConfigurationPage(resources));
    }

    private static class DeleteResourcesConfigurationPage extends UserInputWizardPage {

        private Button deleteContentsButton;

        public DeleteResourcesConfigurationPage(List<IResource> resources) {
            super(RefactorResourcesUtils.getString("DeleteRefactoring.DeleteResourcesConfigurationPage")); //$NON-NLS-1$
            CoreArgCheck.isNotEmpty(resources, ""); //$NON-NLS-1$
        }

        @Override
        public DeleteResourcesRefactoring getRefactoring() {
            return (DeleteResourcesRefactoring)super.getRefactoring();
        }

        @Override
        public void createControl(Composite parent) {
            initializeDialogUnits(parent);

            Point defaultSpacing = LayoutConstants.getSpacing();

            Composite composite = new Composite(parent, SWT.NONE);
            GridLayout gridLayout = new GridLayout(2, false);
            gridLayout.horizontalSpacing = defaultSpacing.x * 2;
            gridLayout.verticalSpacing = defaultSpacing.y;

            composite.setLayout(gridLayout);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            composite.setFont(parent.getFont());

            Image image = parent.getDisplay().getSystemImage(SWT.ICON_QUESTION);
            Label imageLabel = new Label(composite, SWT.NULL);
            imageLabel.setBackground(image.getBackground());
            imageLabel.setImage(image);
            imageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));

            List<IResource> resources = getRefactoring().getResources();
            Label label = new Label(composite, SWT.WRAP);
            label.setFont(composite.getFont());

            boolean onlyProjects = RefactorResourcesUtils.containsOnlyProjects(resources);
            if (onlyProjects) {
                if (resources.size() == 1) {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelSingleProject", //$NON-NLS-1$
                                                  resources.get(0).getName()));
                } else {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelMultiProjects", //$NON-NLS-1$
                                                  new Integer(resources.size())));
                }
            } else if (RefactorResourcesUtils.containsLinkedResource(resources)) {
                if (resources.size() == 1) {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelSingleLinked", //$NON-NLS-1$
                                                  resources.get(0).getName()));
                } else {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelMultiLinked", //$NON-NLS-1$
                                                  new Integer(resources.size())));
                }
            } else {
                if (resources.size() == 1) {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelSingle", //$NON-NLS-1$
                                                  resources.get(0).getName()));
                } else {
                    label.setText(RefactorResourcesUtils.getString("DeleteRefactoring.labelMulti", //$NON-NLS-1$
                                                  new Integer(resources.size())));
                }
            }
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(gridData);

            Composite supportArea = new Composite(composite, SWT.NONE);
            supportArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            gridLayout = new GridLayout(1, false);
            gridLayout.horizontalSpacing = defaultSpacing.x * 2;
            gridLayout.verticalSpacing = defaultSpacing.y;

            supportArea.setLayout(gridLayout);

            if (onlyProjects) {
                createProjectInfoArea(supportArea, resources);
            } else {
                createResourceInfoArea(supportArea, resources);
            }

            setControl(composite);
        }

        /**
         * @param composite
         * @param resources
         * @param parent
         */
        private void createProjectInfoArea(Composite parent, List<IResource> resources) {
            GridData gridData;

            deleteContentsButton = new Button(parent, SWT.CHECK);
            deleteContentsButton.setFont(parent.getFont());
            deleteContentsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            deleteContentsButton.setText(RefactorResourcesUtils.getString("DeleteRefactoring.projectDeleteContents")); //$NON-NLS-1$
            deleteContentsButton.setFocus();
            deleteContentsButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    getRefactoring().setDeleteContents(deleteContentsButton.getSelection());
                }
            });

            Label projectLocationsLabel = new Label(parent, SWT.NONE);
            GridData labelData = new GridData(SWT.FILL, SWT.FILL, true, false);
            labelData.verticalIndent = 5;
            projectLocationsLabel.setLayoutData(labelData);
            projectLocationsLabel.setText(resources.size() == 1 ?
                RefactorResourcesUtils.getString("DeleteRefactoring.projectLocation") :  //$NON-NLS-1$
                    RefactorResourcesUtils.getString("DeleteRefactoring.projectLocations")); //$NON-NLS-1$

            int style = SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL;
            if (resources.size() != 1) style |= SWT.BORDER;
            StyledText projectLocationsList = new StyledText(parent, style);
            projectLocationsList.setAlwaysShowScrollBars(false);
            labelData.horizontalIndent = projectLocationsList.getLeftMargin();
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            projectLocationsList.setLayoutData(gridData);
            projectLocationsList.setBackground(projectLocationsList.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

            StringBuffer buf = new StringBuffer();
            for (IResource resource : resources) {
                String location = resource.getFullPath().toOSString();
                if (buf.length() > 0)
                    buf.append('\n');

                buf.append(location);
            }
            projectLocationsList.setText(buf.toString());
            gridData.heightHint = Math.min(convertHeightInCharsToPixels(5),
                                           projectLocationsList.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        }

        /**
         * @param supportArea
         * @param resources
         */
        private void createResourceInfoArea(Composite parent, List<IResource> resources) {
            Group group = WidgetFactory.createGroup(parent,
                                                    RefactorResourcesUtils.getString("DeleteRefactoring.deleteResourceLabel"), //$NON-NLS-1$
                                                    GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | 
                                                    GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);

            TreeViewer viewer = new TreeViewer(group, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

            viewer.setContentProvider(new ResourcesContentProvider());
            viewer.setLabelProvider(new ModelExplorerLabelProvider());
            viewer.setAutoExpandLevel(5);
            viewer.setInput(resources);

            Label detailLabel = new Label(parent, SWT.NONE);
            detailLabel.setText(RefactorResourcesUtils.getString("DeleteRefactoring.previewOrOKLabel")); //$NON-NLS-1$
        }
    }

    private static class ResourcesContentProvider implements ITreeContentProvider {

        private Set<IResource> rootCollection = new HashSet<IResource>();

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement == null)
                return new Object[0];

            if (parentElement instanceof List) {
                return ((List) parentElement).toArray();
            }
            else if (rootCollection.contains(parentElement)) {
                  RefactoringStatus status = new RefactoringStatus();
                  IndexResourceCallback callback = new IndexResourceCallback();
                  RefactorResourcesUtils.calculateRelatedResources((IResource) parentElement, status, callback, Relationship.DEPENDENT);
                  RefactorResourcesUtils.calculateRelatedVdbResources((IResource) parentElement, status, callback);

                  return callback.getResources().toArray();
            }

            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof IResource) {
                return ((IResource) element).getParent();
            }

            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof IContainer)
                return true;

            if (rootCollection.contains(element))
                return true;

            return false;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public void dispose() {
            rootCollection.clear();
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            rootCollection.clear();
            if (newInput instanceof List) {
                for (Object element : (List) newInput) {
                    if (element instanceof IResource)
                        rootCollection.add((IResource) element);
                }
            }
        }
    }

    private static class IndexResourceCallback extends AbstractResourceCallback {

        private Set<IResource> resources = new HashSet<IResource>();

        /**
         * @return the resources
         */
        public Set<IResource> getResources() {
            return this.resources;
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception {
            resources.add(relatedFile);
            RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, status, this);
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status) {
            resources.add(vdbFile);
        }
    }
}
