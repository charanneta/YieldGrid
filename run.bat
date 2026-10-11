@echo off
echo ==================================================
echo Launching YieldGrid GUI Application...
echo ==================================================

java -cp "bin;lib/mysql-connector-j-9.2.0.jar" yieldgrid.main.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with an error. If classes are missing, run compile.bat first.
    pause
)
