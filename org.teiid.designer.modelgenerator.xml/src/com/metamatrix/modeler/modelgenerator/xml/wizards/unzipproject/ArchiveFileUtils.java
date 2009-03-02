/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards.unzipproject;

import java.io.IOException;
import java.util.zip.ZipFile;


/**
 * Provide archive (zip) utility methods.
 */
public class ArchiveFileUtils
{
	private static ProjectZipImportStructureProvider zipProviderCache;

	/**
	 * Determines whether the input file is a valid .zip or .jar file.
	 * 
	 * @param theFileName
	 *            file to test
	 * @return true if the file is in tar format
	 */
	public static boolean isZipFile(String theFileName) {
		if (theFileName.length() == 0) {
			return false;
		}

		try {
			new ZipFile(theFileName);
		} catch (IOException ioException) {
			return false;
		}

		return true;
	}

	/**
	 * Clears the structure provider after closing it.
	 * 
	 * @throws IOException 
	 */
	public static void clearProviderCache() throws IOException {
		if (zipProviderCache != null) {
			closeZipFile(zipProviderCache.getZipFile());
			zipProviderCache = null;
		}
	}

	/**
	 * Returns a new instance of the zip structure provider.
	 * 
	 * @param targetZip
	 * @return the structure provider
	 * @throws IOException 
	 */
	public static ProjectZipImportStructureProvider getZipStructureProvider(
			ZipFile targetZip) throws IOException {
		if (zipProviderCache == null) {
			zipProviderCache = new ProjectZipImportStructureProvider(targetZip);
		} else if (!zipProviderCache.getZipFile().getName().equals(
				targetZip.getName())) {
			clearProviderCache();
			// i.e.- new value, so finalize&remove old value
			zipProviderCache = new ProjectZipImportStructureProvider(targetZip);
		} else if (!zipProviderCache.getZipFile().equals(targetZip)) {
			closeZipFile(targetZip); // i.e.- duplicate handle to same
			// .zip
		}

		return zipProviderCache;
	}

	/**
	 * Attempts to close the passed zip file.
	 * 
	 * @param file
	 *            The zip file to attempt to close
	 * @return Returns true if the operation was successful
	 * @throws IOException 
	 */
	public static boolean closeZipFile(ZipFile file) throws IOException {
		file.close();
		return true;
	}
}
