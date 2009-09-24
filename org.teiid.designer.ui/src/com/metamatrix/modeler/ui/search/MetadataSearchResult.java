/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * The <code>NewMetadataSearchResult</code> class is the result object for a metadata search. A {@link MetadataSearchQuery} is
 * used to construct a result instance. Then {@link MetadataMatch}es are added later.
 * 
 * @since 6.0.0
 */
public class MetadataSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter, UiConstants {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private final Match[] NO_MATCHES = new Match[0];

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The query used to construct the results.
     * 
     * @since 6.0.0
     */
    private final MetadataSearchQuery query;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a result for the specified query. Metadata matches must be added to the result using {@link #setMatches(List)}.
     * 
     * @param the query used to construct the result
     * @since 6.0.0
     */
    public MetadataSearchResult( MetadataSearchQuery query ) {
        this.query = query;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult,
     *      org.eclipse.ui.IEditorPart)
     */
    @Override
    public Match[] computeContainedMatches( AbstractTextSearchResult result,
                                            IEditorPart editor ) {
        IEditorInput editorInput = editor.getEditorInput();

        if (editorInput instanceof IFileEditorInput) {
            return computeContainedMatches(result, ((IFileEditorInput)editorInput).getFile());
        }

        return NO_MATCHES;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.IFileMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult,
     *      org.eclipse.core.resources.IFile)
     */
    @Override
    public Match[] computeContainedMatches( AbstractTextSearchResult result,
                                            IFile file ) {
        Object info = new MetadataMatchInfo(file.getFullPath().toOSString(), (MetadataSearchResult)result);
        return getMatches(info);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
     */
    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.IFileMatchAdapter#getFile(java.lang.Object)
     */
    @Override
    public IFile getFile( Object element ) {
        if (element instanceof MetadataMatchInfo) {
            return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(((MetadataMatchInfo)element).getResourcePath()));
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
     */
    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchResult#getLabel()
     */
    @Override
    public String getLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(MetadataSearchResult.class) + "result.msg", //$NON-NLS-1$ 
                              new Object[] {((MetadataSearchQuery)getQuery()).getSearchCriteria(), getMatchCount()});
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchResult#getQuery()
     */
    @Override
    public ISearchQuery getQuery() {
        return this.query;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchResult#getTooltip()
     */
    @Override
    public String getTooltip() {
        return getLabel();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.IEditorMatchAdapter#isShownInEditor(org.eclipse.search.ui.text.Match,
     *      org.eclipse.ui.IEditorPart)
     */
    @Override
    public boolean isShownInEditor( Match match,
                                    IEditorPart editor ) {
        if (match instanceof IModelObjectMatch) {
            String matchResourcePath = ((IModelObjectMatch)match).getResourcePath();
            String editorResourcePath = ((ModelEditor)editor).getModelFile().getFullPath().toOSString();

            return matchResourcePath.equals(editorResourcePath);
        }

        return false;
    }
}
