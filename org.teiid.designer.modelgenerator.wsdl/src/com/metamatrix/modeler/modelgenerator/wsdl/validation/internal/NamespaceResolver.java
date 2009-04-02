package com.metamatrix.modeler.modelgenerator.wsdl.validation.internal;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.wsdl.validation.internal.resolver.IExtensibleURIResolver;
import org.eclipse.wst.wsdl.validation.internal.resolver.IURIResolutionResult;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;

import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.modelgenerator.xml.wizards.XsdAsRelationalImportWizard;

public class NamespaceResolver implements IExtensibleURIResolver {

	private Map<String,String> namespaceToXsd;

	public NamespaceResolver() throws Exception {
		IPreferenceStore prefs = XmlImporterUiPlugin.getDefault().getPreferenceStore();
		String directory = prefs.getString(XmlImporterUiPlugin.xsdLibrary);
		File xsdDirectory = new File(directory);
		if(!xsdDirectory.exists()) {
			throw new Exception(ModelGeneratorWsdlPlugin.Util.getString("NamespaceResolver.BadDirectory"));
		}
		
		List<String> xsdPaths = new ArrayList<String>();
		if(xsdDirectory.canRead()) {
			String[] allContents = xsdDirectory.list();
			for(int i = 0; i < allContents.length; i++) {
				String path = allContents[i];
				if(path.endsWith(".xsd")) {
					xsdPaths.add(directory + File.separatorChar + path);
				}
			}
		}

		namespaceToXsd = new HashMap<String,String>();
		for(Iterator<String> iter = xsdPaths.iterator(); iter.hasNext(); ) {
			XSDParser parser = new XSDParser(null);
			String path = iter.next();
			parser.parse(new FileInputStream(path));
			XSDSchema schema = parser.getSchema();
			String namespace = schema.getTargetNamespace();
			namespaceToXsd.put(namespace, "file:" + path);
		}
	}
	
	@Override
	public void resolve(String baseLocation, String publicId, String systemId,
			IURIResolutionResult result) {
		if(namespaceToXsd.containsKey(publicId)) {
			result.setPhysicalLocation(namespaceToXsd.get(publicId));
			result.setLogicalLocation(namespaceToXsd.get(publicId));
		}
	}

}
