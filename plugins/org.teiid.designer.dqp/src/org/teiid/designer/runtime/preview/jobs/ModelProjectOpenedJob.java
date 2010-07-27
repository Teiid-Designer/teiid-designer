/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.runtime.preview.PreviewManager;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * The <code>ModelProjectOpenedJob</code> ensures there is a Preview VDB for each model in the opened project.
 */
public final class ModelProjectOpenedJob extends CompositePreviewJob {

    /**
     * The project just opened (never <code>null</code>).
     */
    private final IProject project;

    /**
     * @param project the project just opened (never <code>null</code>)
     * @param context the preview context (never <code>null</code>)
     */
    public ModelProjectOpenedJob( IProject project,
                                  PreviewContext context ) throws Exception {
        super(Messages.bind(Messages.ModelProjectOpenedJob, project), context, null, true);

        assert (project != null);
        assert (ModelerCore.hasModelNature(project));

        this.project = project;
        
        // create Preview VDB for project
        CreatePreviewVdbJob job = new CreatePreviewVdbJob(project, context);
        add(job);

        process(this.project);
    }

    /**
     * @return the project that was opened (never <code>null</code>
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * Constructs a {@link CreatePreviewVdbJob} for each previewable model.
     * 
     * @param container the project or folder whose models are being processed
     * @throws Exception if there is a constructing the Preview VDBs
     */
    private void process( IContainer container ) throws Exception {
        PreviewContext context = getContext();

        for (IResource resource : container.members()) {
            if (resource instanceof IContainer) {
                process((IContainer)resource);
            } else if ((resource instanceof IFile) && PreviewManager.isPreviewable((IFile)resource)) {
                CreatePreviewVdbJob job = new CreatePreviewVdbJob((IFile)resource, context);
                add(job);
            }
        }
    }

}
