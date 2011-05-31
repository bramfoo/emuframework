<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- WinUAE 1.6 / LinUAE 0.76 configuration template (properties file) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"0", "1":"1", "2":"2", "3":"3"}>

<#-- Drive type definition -->
<#assign driveTypes = {"AMG3_5_880":"0", "AMG3_5_1760":"1"}>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Floppy drive macro -->
<#macro floppyDisk item>
floppy${floppyDriveLetter[item.num]}=./${item.digobj}
floppy${floppyDriveLetter[item.num]}type=${driveTypes[item.type]}
floppy${floppyDriveLetter[item.num]}sound=0
</#macro>

<#-- Fixed drive macro -->
<#macro fixedDisk item>
kickstart_rom_file=./${item.swImg}
kickstart_ext_rom_file=
flash_file=
cart_file=
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
<#-- Start of body -->
<@separator section="body"/>
config_description=Emulation Framework generated configuration
config_hardware=false
config_host=true
use_gui=no
gfx_display=0
gfx_framerate=1
gfx_width_windowed=720
gfx_height_windowed=568
gfx_width_fullscreen=800
gfx_height_fullscreen=600
gfx_refreshrate=0
gfx_vsync=false
gfx_lores=false
gfx_linemode=double
gfx_correct_aspect=false
gfx_fullscreen_amiga=false
gfx_fullscreen_picasso=false
gfx_center_horizontal=none
gfx_center_vertical=none
gfx_colour_mode=32bit
gfx_filter=no
gfx_filter_vert_zoom=0
gfx_filter_horiz_zoom=0
gfx_filter_vert_offset=0
gfx_filter_horiz_offset=0
gfx_filter_scanlines=0
gfx_filter_scanlinelevel=0
gfx_filter_scanlineratio=17
use_debugger=false
<#list fixedDisks as fixed>
 <@fixedDisk item=fixed/>
</#list>
kickshifter=false
<#-- Floppy drives defined here don't seem to work, moved to CLI -->
<@separator section="postscript"/>
<#-- Start of postscript -->