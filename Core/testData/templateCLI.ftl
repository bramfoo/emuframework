<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- QEMU 0.9.0 (Windows) and 0.12.5 (Linux) configuration template (CLI) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"fda", "1":"fdb"}>

<#-- Drive type definition -->
<#assign driveTypes = {"FAT3_5_720":"720K", "FAT3_5_1440":"1.44M", "C645_25_170":"unsupported drive type", "C645_25_340":"unsupported drive type"}>

<#-- Fixed drive letter definition -->
<#assign fixedDriveLetter = {"0":"hda", "1":"hdb", "2":"cdrom", "3":"hdd"}>

<#-- Floppy disk macro -->
<#macro floppyDisk item>
-${floppyDriveLetter[item.num]}
"${item.digobj}"
</#macro>

<#-- Fixed disk macro -->
<#macro fixedDisk item>
-${fixedDriveLetter[item.index]}
"${item.swImg}"
</#macro>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
<#-- Start of body -->
<@separator section="body"/>
-L
${configDir}
<#list floppyDisks as floppy>
  <@floppyDisk item=floppy/>
</#list>
<#list fixedDisks as fixed>
  <@fixedDisk item=fixed/>
</#list>
<@separator section="postscript"/>
<#-- Start of postscript -->