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


</xsl:stylesheet>
