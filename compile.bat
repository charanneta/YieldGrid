@echo off
echo ==================================================
echo Compiling YieldGrid Java Project...
echo ==================================================

if not exist "bin" mkdir bin

javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.2.0.jar" -d bin src\yieldgrid\model\*.java src\yieldgrid\interfaces\*.java src\yieldgrid\exception\*.java src\yieldgrid\security\*.java src\yieldgrid\database\*.java src\yieldgrid\dao\*.java src\yieldgrid\service\*.java src\yieldgrid\ui\*.java src\yieldgrid\main\*.java src\yieldgrid\test\*.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation completed successfully into bin/ directory!
    exit /b 0
) else (
    echo [ERROR] Compilation failed. Please check errors above.
    exit /b 1
)
