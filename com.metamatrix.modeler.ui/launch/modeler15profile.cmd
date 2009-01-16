@ECHO OFF
@REM Root of MetaMatrix Modeler installation
@REM  get the ROOT directory of this installation
@for /F "delims=" %%i IN ('cd') DO set MODELER_ROOT=%%i

@REM Adding the dlls folder to PATH environment variable 

@set MM_MIMB=%MODELER_ROOT%\MetaIntegration\bin
@set PATH=%MM_MIMB%;%PATH%

start eclipse\eclipse.exe -consoleLog -debug -data workspace -vmargs -agentlib:yjpagent=cpu=times,onexit=cpu,dir=d:\scott\snapshots -Xbootclasspath/a:license -Xms128M -Xmx512M -Djavax.net.ssl.trustStore=license\mmdemo.truststore -Dmetamatrix.config.none
