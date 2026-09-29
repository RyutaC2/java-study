@echo off
cd /d "%~dp0"
if not exist "target\library-system-dx-1.0.0.jar" (
    echo Build first: mvn package
    exit /b 1
)
if exist "runtime\bin\java.exe" (
    "runtime\bin\java.exe" -jar "target\library-system-dx-1.0.0.jar"
) else (
    java -jar "target\library-system-dx-1.0.0.jar"
)
