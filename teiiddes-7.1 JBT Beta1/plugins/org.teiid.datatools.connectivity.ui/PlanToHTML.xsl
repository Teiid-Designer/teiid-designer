<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" />

	<xsl:template match="/">
		<html>
			<body>
				<xsl:apply-templates select="node" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="node">
		<xsl:apply-templates select="property" />
	</xsl:template>

	<xsl:template match="property">
		<h3>
			<font color="blue">
				<xsl:value-of select="@name" />
			</font>
		</h3>
		<xsl:for-each select="value">
			<br />
			<para>
				<font size="-2">
					<xsl:value-of select="." />
				</font>
			</para>
		</xsl:for-each>
		<br />
		<hr width="75%" size="3" />
	</xsl:template>
</xsl:stylesheet>