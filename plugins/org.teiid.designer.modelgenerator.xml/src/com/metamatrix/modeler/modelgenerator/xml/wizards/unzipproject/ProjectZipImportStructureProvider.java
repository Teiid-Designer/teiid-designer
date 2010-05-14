/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards.unzipproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;


/**
 * This class provides information regarding the context structure and content
 * of specified zip file entry objects.
 * 
 * @since 3.1
 */
public class ProjectZipImportStructureProvider implements IImportStructureProvider
{

	private ZipEntry zipEntryRoot = new ZipEntry("/");//$NON-NLS-1$
	
	private ZipFile zipFile;

	private Map<ZipEntry, List> children;

	private Map<IPath, ZipEntry> directoryEntryCache = new HashMap<IPath, ZipEntry>();

	private int stripLevel;

	/**
	 * Creates a <code>ZipFileStructureProvider</code>, which will operate on
	 * the passed zip file.
	 * 
	 * @param sourceFile
	 *            The source file to create the ZipLeveledStructureProvider
	 *            around
	 */
	public ProjectZipImportStructureProvider(ZipFile sourceFile) {
		super();
		zipFile = sourceFile;
		stripLevel = 0;
	}

	/**
	 * Adds the specified child to the internal collection of the parent's
	 * children.
	 */
	@SuppressWarnings("unchecked")
	protected void addToChildren(ZipEntry parent, ZipEntry child) {
		List<ZipEntry> childList = children.get(parent);
		if (childList == null) {
			childList = new ArrayList<ZipEntry>();
			children.put(parent, childList);
		}

		childList.add(child);
	}

	/**
	 * Creates a new container zip entry with the specified name, if it has not
	 * already been created.
	 * 
	 * @param pathname The path representing the container
	 */
	protected void createContainer(IPath pathname) {
		if (directoryEntryCache.containsKey(pathname))
			return;

		ZipEntry parent;
		if (pathname.segmentCount() == 1)
			parent = zipEntryRoot;
		else
			parent = directoryEntryCache.get(pathname.removeLastSegments(1));

		ZipEntry newEntry = new ZipEntry(pathname.toString());
		directoryEntryCache.put(pathname, newEntry);
		addToChildren(parent, newEntry);
	}

	/**
	 * Creates a new file zip entry with the specified name.
	 * 
	 * @param entry The new zip file entry
	 */
	protected void createFile(ZipEntry entry) {
		IPath pathname = new Path(entry.getName());
		ZipEntry parent;
		if (pathname.segmentCount() == 1)
			parent = zipEntryRoot;
		else
			parent = directoryEntryCache.get(pathname.removeLastSegments(1));

		addToChildren(parent, entry);
	}

	/*
	 * (non-Javadoc) Method declared on IImportStructureProvider
	 */
	public List getChildren(Object element) {
		if (children == null)
			initialize();

		return children.get(element);
	}

	/*
	 * (non-Javadoc) Method declared on IImportStructureProvider
	 */
	public InputStream getContents(Object element) {
		try {
			return zipFile.getInputStream((ZipEntry) element);
		} catch (IOException e) {
			return null;
		}
	}

	/*
	 * Strip the leading directories from the path
	 */
	private String stripPath(String path) {
		String pathOrig = new String(path);
		
		for (int i = 0; i < stripLevel; i++) {
			int firstSep = path.indexOf('/');
			
			// If the first character was a separator we must strip to the next
			// separator as well
			if (firstSep == 0) {
				path = path.substring(1);
				firstSep = path.indexOf('/');
			}
			// No separator was present so we're in a higher directory right
			// now
			if (firstSep == -1) {
				return pathOrig;
			}
			
			path = path.substring(firstSep);
		}
		return path;
	}

	/*
	 * (non-Javadoc) Method declared on IImportStructureProvider
	 */
	public String getFullPath(Object element) {
		return stripPath(((ZipEntry) element).getName());
	}

	/*
	 * (non-Javadoc) Method declared on IImportStructureProvider
	 */
	public String getLabel(Object element) {
		if (element.equals(zipEntryRoot))
			return ((ZipEntry) element).getName();

		return stripPath(new Path(((ZipEntry) element).getName()).lastSegment());
	}

	/**
	 * Returns the importer's root entry.
	 * 
	 * @return java.util.zip.ZipEntry
	 */
	public Object getRoot() {
		return zipEntryRoot;
	}

	/**
	 * Returns the zip file that this provider provides structure for.
	 * 
	 * @return The zip file
	 */
	public ZipFile getZipFile() {
		return zipFile;
	}

	/**
	 * Initializes this object's children table based on the contents of the
	 * specified source file.
	 */
	protected void initialize() {
		children = new HashMap<ZipEntry, List>(1000);

		Enumeration entries = zipFile.entries();
		
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			
			if (!entry.isDirectory()) {
				
				IPath path = new Path(entry.getName()).addTrailingSeparator();
				int pathSegmentCount = path.segmentCount();

				for (int i = 1; i < pathSegmentCount; i++) {
					createContainer(path.uptoSegment(i));
				}
				
				createFile(entry);
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on IImportStructureProvider
	 */
	public boolean isFolder(Object element) {
		return ((ZipEntry) element).isDirectory();
	}

	public void setStripLevel(int level) {
		stripLevel = level;
	}

	public int getStripLevel() {
		return stripLevel;
	}
}
