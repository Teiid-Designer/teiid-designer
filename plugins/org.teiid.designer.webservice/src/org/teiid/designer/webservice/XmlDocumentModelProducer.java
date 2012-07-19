/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.compare.ModelProducer;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.compare.selector.TransientModelSelector;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;



/** 
 * @since 8.0
 */
public class XmlDocumentModelProducer implements ModelProducer {

    public static final int WARNING_NO_WEBSERVICE_OBJECTS          = 32301;

    private final IWebServiceModelBuilder builder;
    private final WebServiceModelProducer wsProducer;
    private final TransientModelSelector output;
    private final boolean shouldBuild;

    /** 
     * @param matcherFactories
     * @since 4.2
     */
    public XmlDocumentModelProducer(final IWebServiceModelBuilder builder, WebServiceModelProducer wsProducer ) {
        this.builder = builder;
        this.wsProducer = wsProducer;
        
        if ( this.builder.getXmlModel() != null ) {
            shouldBuild = true;
            // Create a temporary model selector with the same URI as the actual relational model
            // (the temporary will be placed in a separate "temporary" resource set, so same URI can be used)
            this.output = new TransientModelSelector(builder.getXmlModel().toString());
        } else {
            shouldBuild = false;
            this.output = new TransientModelSelector("WebServiceModelBuilder-XmlDocumentModel-Temporary"); //$NON-NLS-1$
        }
        
    }

    /** 
     * @see org.teiid.designer.compare.ModelProducer#execute(org.eclipse.core.runtime.IProgressMonitor, java.util.List)
     * @since 4.2
     */
    @Override
	public void execute(IProgressMonitor monitor, List problems) throws Exception {
        if ( !shouldBuild ) {
            return;
        }
        
        // Create the ModelAnnotation ...
        final ModelAnnotation modelAnnotation = this.output.getModelAnnotation();
        modelAnnotation.setPrimaryMetamodelUri(XmlDocumentPackage.eNS_URI);
        modelAnnotation.setModelType(ModelType.VIRTUAL_LITERAL);
        
        // Create the generator ...
        final IWebServiceXmlDocumentGenerator generator = WebServicePlugin.createXmlDocumentGenerator();
        generator.setXmlDocumentResource(this.output.getResource());
        
        // Go through the Web Service model and obtain all root-level components objects ...
        final ModelSelector wsModelSelector = this.wsProducer.getOutputSelector();
        generator.setWebServiceModelSelector(wsModelSelector);
        final Iterator iter = wsModelSelector.getRootObjects().iterator();
        while (iter.hasNext()) {
            final EObject root = (EObject)iter.next();
            if ( root instanceof Interface ) {
                generator.addWebServiceComponent((Interface)root);
            }
        }
        
        if ( generator.getWebServiceComponents().isEmpty() ) {
            // No objects were found ...
            final String msg = WebServicePlugin.Util.getString("XmlDocumentModelProducer.NoWebServiceComponentsFound");  //$NON-NLS-1$
            problems.add( new Status(IStatus.WARNING,WebServicePlugin.PLUGIN_ID,WARNING_NO_WEBSERVICE_OBJECTS,msg,null));
        } else {
            // At least some objects were found ...
            generator.generate(monitor, problems);
        }
    }

    /** 
     * @see org.teiid.designer.compare.ModelProducer#getOutputSelector()
     * @since 4.2
     */
    @Override
	public ModelSelector getOutputSelector() {
        return this.output;
    }


}
