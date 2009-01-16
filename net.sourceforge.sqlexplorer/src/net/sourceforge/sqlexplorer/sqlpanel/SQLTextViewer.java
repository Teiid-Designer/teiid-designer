/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.sqlpanel;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import net.sourceforge.sqlexplorer.sqleditor.ISQLColorConstants;
import net.sourceforge.sqlexplorer.sqleditor.SQLSourceViewerConfiguration;
import net.sourceforge.sqlexplorer.sqleditor.SQLTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SQLTextViewer extends SourceViewer {

    IPresentationReconciler fPresentationReconciler;
    private IDocumentPartitioner partitioner;
    SQLTextTools sqlTextTools;
    IPreferenceStore store;
    IContentAssistant contentAssistant;
    public Dictionary dictionary;
    SQLSourceViewerConfiguration configuration;
    TextViewerUndoManager undoManager = new TextViewerUndoManager(50);

    @Override
    public void setDocument( IDocument dc ) {
        // JFaceDbcPlugin.debug("setDocument:"+dc);
        IDocument previous = this.getDocument();
        if (previous != null) {
            // undoManager.disconnect();
            partitioner.disconnect();
        }
        super.setDocument(dc);
        if (dc != null) {

            partitioner.connect(dc);
            dc.setDocumentPartitioner(partitioner);
            undoManager.connect(this);
            this.setUndoManager(undoManager);
        }

    }

    public SQLTextViewer( Composite parent,
                          int style,
                          IPreferenceStore store,
                          final Dictionary dictionary,
                          IVerticalRuler ruler ) {
        super(parent, ruler, style);
        this.store = store;
        this.dictionary = dictionary;

        sqlTextTools = new SQLTextTools(store, dictionary);
        this.getControl().addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent event ) {
                sqlTextTools.dispose();
                fPresentationReconciler.uninstall();
            }
        });
        store.addPropertyChangeListener(fPreferenceListener);
        FontData[] fData = PreferenceConverter.getFontDataArray(store, IConstants.FONT); 

        if (fData.length > 0) {
            JFaceResources.getFontRegistry().put(fData[0].toString(), fData);
            this.getControl().setFont(JFaceResources.getFontRegistry().get(fData[0].toString()));
        }

        configuration = new SQLSourceViewerConfiguration(sqlTextTools);

        fPresentationReconciler = configuration.getPresentationReconciler(null);
        if (dictionary != null) {
            contentAssistant = configuration.getContentAssistant(null);
            if (contentAssistant != null) {
                contentAssistant.install(this);
            }

        }

        if (fPresentationReconciler != null) fPresentationReconciler.install(this);

        partitioner = configuration.getDocumentPartitioner();
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent event ) {
                if (dictionary != null) {
                    contentAssistant.uninstall();
                }
            }
        });
        fInformationPresenter = configuration.getInformationPresenter(this);

        if (fInformationPresenter != null) fInformationPresenter.install(this);

        this.setAnnotationHover(new IAnnotationHover() {

            public String getHoverInfo( ISourceViewer sourceViewer,
                                        int lineNumber ) {

                return "hover info";
            }
        });
        setHoverControlCreator(new IInformationControlCreator() {
            public IInformationControl createInformationControl( Shell parent ) {
                return new DefaultInformationControl(parent);
            }
        });

        String[] contentTypes = {IDocument.DEFAULT_CONTENT_TYPE, ISQLColorConstants.SQL_SINGLE_LINE_COMMENT,
            ISQLColorConstants.SQL_STRING, ISQLColorConstants.SQL_MULTILINE_COMMENT};
        for (int i = 0; i < contentTypes.length; i++) {

            super.setTextHover(new ITextHover() {

                public String getHoverInfo( ITextViewer textViewer,
                                            IRegion hoverRegion ) {
                    //
                    return "";
                }

                public IRegion getHoverRegion( ITextViewer textViewer,
                                               int offset ) {

                    return new Region(offset, 1);
                }

            }, contentTypes[i]);
        }
        super.activatePlugins();
    }

    public void setNewDictionary( Dictionary newDictionary ) {
        if (dictionary != null && contentAssistant != null) contentAssistant.uninstall();
        dictionary = newDictionary;
        // sqlTextTools.setNewDictionary(newDictionary);
        sqlTextTools = new SQLTextTools(store, dictionary);
        configuration = new SQLSourceViewerConfiguration(sqlTextTools);
        //
        fPresentationReconciler = configuration.getPresentationReconciler(null);

        if (fPresentationReconciler != null) fPresentationReconciler.install(this);
        contentAssistant = configuration.getContentAssistant(null);
        if (contentAssistant != null) {
            contentAssistant.install(this);
        }
    }

    public SQLTextViewer( Composite parent,
                          int style,
                          IPreferenceStore store,
                          final Dictionary dictionary ) {

        this(parent, style, store, dictionary, new VerticalRuler(0));

    }

    class PreferenceListener implements IPropertyChangeListener {
        public void propertyChange( PropertyChangeEvent event ) {
            adaptToPreferenceChange(event);
        }
    }

    void adaptToPreferenceChange( PropertyChangeEvent event ) {
        if (event.getProperty().equals(IConstants.FONT)) {
            FontData[] fData = PreferenceConverter.getFontDataArray(store, IConstants.FONT);
            String des = store.getString(IConstants.FONT);
            JFaceResources.getFontRegistry().put(des, fData);
            Control ctrl = this.getControl();
            if (ctrl != null) {
                ctrl.setFont(JFaceResources.getFontRegistry().get(des));
            }

        }
    }

    PreferenceListener fPreferenceListener = new PreferenceListener();

    public void showAssistance() {

        if (dictionary != null) contentAssistant.showPossibleCompletions();
    }

    /**
     * @see org.eclipse.jface.text.source.ISourceViewer#setDocument(org.eclipse.jface.text.IDocument,
     *      org.eclipse.jface.text.source.IAnnotationModel)
     */
    @Override
    public void setDocument( IDocument document,
                             IAnnotationModel annotationModel ) {
        setDocument(document);
        if (annotationModel != null && document != null) annotationModel.connect(document);
    }

    public void clearText() {
        getTextWidget().setText(""); //$NON-NLS-1$
    }
}
