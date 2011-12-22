<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- JavaCPC 6.7 configuration template (properties file) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"0", "1":"1", "2":"2", "3":"3"}>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Floppy drive macro -->
<#macro floppyDisk item>
<#if item.type == "AMS3_180">
file.drive${floppyDriveLetter[item.num]}=${item.digobj}
loaddrive${floppyDriveLetter[item.num]}=${item.inserted}
df${floppyDriveLetter[item.num]}_head=false
</#if>
<#if item.type == "AMSTAPE">
file.tape=${item.digobj}
loadtape=${item.inserted}
</#if>
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
<#-- Start of body -->
<@separator section="body"/>
#[Settings]
#Emulation Framework generated configuration
system=CPC6128
lowperformance=false
hacker_kay_output=true
ay_effect=true
on_top=false
upper_F=none
upper_E=none
upper_D=none
upper_C=none
upper_B=none
upper_A=none
upper_9=none
upper_8=none
upper_7=none
upper_6=none
upper_5=none
upper_4=none
upper_3=none
upper_2=none
upper_1=none
upper_0=BASIC1-0.zip
frame_xpos=200
frame_ypos=200
show_about=false
<#list floppyDisks as floppy>
 <@floppyDisk item=floppy/>
</#list>
lower=OS464.zip
firstrun=false
<@separator section="postscript"/>
<#-- Start of postscript -->