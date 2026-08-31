@echo off
rem ======================================================================
rem windows startup script
rem
rem author: 0neBean
rem date: 2018-12-2
rem ======================================================================

java -jar  -Denv=@profileActive@ ../boot/@project.build.finalName@.jar

pause
