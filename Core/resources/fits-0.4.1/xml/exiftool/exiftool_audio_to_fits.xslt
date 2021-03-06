<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exiftool_common_to_fits.xslt"/>
<xsl:template match="/">

    <fits xmlns="http://hul.harvard.edu/ois/xml/ns/fits/fits_output">
		<xsl:apply-imports/>
		
		<metadata>
		<audio>
			<duration>
				<xsl:value-of select="exiftool/Duration"/>
			</duration>
			<xsl:choose>
				<xsl:when test="exiftool/BitsPerSample">
					<bitDepth>
						<xsl:value-of select="exiftool/BitsPerSample"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/SampleSize">
					<bitDepth>
						<xsl:value-of select="exiftool/SampleSize"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/AudioBitrate">
					<bitDepth>
						<xsl:value-of select="exiftool/AudioBitrate"/>
					</bitDepth>
				</xsl:when>
				<xsl:when test="exiftool/AvgBitRate">
					<bitDepth>
						<xsl:value-of select="exiftool/AvgBitRate"/>
					</bitDepth>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="exiftool/SampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/SampleRate"/>
					</sampleRate>
				</xsl:when>
				<xsl:when test="exiftool/AudioSampleRate">
					<sampleRate>
						<xsl:value-of select="exiftool/AudioSampleRate"/>
					</sampleRate>
				</xsl:when>
			</xsl:choose>

			<xsl:choose>
				<!-- WAV/FLAC -->
				<xsl:when test="exiftool/NumChannels">
					<channels>
						<xsl:value-of select="exiftool/NumChannels"/>
					</channels>
				</xsl:when>
				<!-- OGG -->
				<xsl:when test="exiftool/AudioChannels">
					<channels>
						<xsl:value-of select="exiftool/AudioChannels"/>
					</channels>
				</xsl:when>
				<!-- MP3 -->
				<xsl:when test="exiftool/ChannelMode">
					<channels>
						<xsl:value-of select="exiftool/ChannelMode"/>
					</channels>
				</xsl:when>
				<!-- FLAC -->
				<xsl:when test="exiftool/Channels">
					<channels>
						<xsl:value-of select="exiftool/Channels"/>
					</channels>
				</xsl:when>
			</xsl:choose>	
			<dataFormatType>
				<xsl:value-of select="exiftool/Encoding"/>
			</dataFormatType>	
			<blockSizeMin>
				<xsl:value-of select="exiftool/BlockSizeMin"/>
			</blockSizeMin>	
			<blockSizeMax>
				<xsl:value-of select="exiftool/BlockSizeMax"/>
			</blockSizeMax>	
		</audio>			
		</metadata>
	</fits>	

</xsl:template>
</xsl:stylesheet>