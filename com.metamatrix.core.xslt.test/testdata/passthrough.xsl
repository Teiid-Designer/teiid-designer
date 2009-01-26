<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xslns="http://www.w3.org/1999/XMLNS" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<!--
  ************************************************************************
  ** Primary template; pass everything through
  ************************************************************************ -->
<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>

<!--
  ************************************************************************
  ** Override the built-in template for text nodes so that
  ** the normalized text is used.
  ** XML parsers normalize CR+LF to LF, except in attribute values containing
  ** a CR written as a numeric character reference like &#13;. So in XSLT you
  ** get only LFs (&#10;) 99.999% of the time.
  ************************************************************************ -->
<xsl:template match="text()">
  <xsl:value-of select="normalize-space(.)"/>
</xsl:template>


</xsl:stylesheet>
