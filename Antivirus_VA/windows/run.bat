@echo off
rem Ejecuta el Antivirus Java en Windows.
rem Requiere tener el JRE/JDK instalado y "java" disponible en el PATH.
 
cd /d "%~dp0.."
 
where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: no se encontro "java". Instala el JDK/JRE y agregalo al PATH.
    echo Descarga: https://adoptium.net
    pause
    exit /b 1
)
 
if not exist out (
    echo No se encontro la carpeta "out". Ejecuta primero windows\compile.bat
    pause
    exit /b 1
)
 
java -cp out Main
if errorlevel 1 (
    echo.
    echo El programa termino con un error.
    pause
)
