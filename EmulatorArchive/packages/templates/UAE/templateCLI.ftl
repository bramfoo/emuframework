<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- WinUAE 1.6 / LinUAE 0.76 configuration template (CLI) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"0", "1":"1", "2":"2", "3":"3"}>

<#-- Floppy drive macro -->
<#macro floppyDisk item>
-${floppyDriveLetter[item.num]}
${item.digobj}
</#macro>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
<#-- Start of body -->
<@separator section="body"/>
-f
config.props
<#list floppyDisks as floppy>
 <@floppyDisk item=floppy/>
</#list>
<@separator section="postscript"/>
<#-- Start of postscript -->