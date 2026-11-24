@echo off
rem Compila el Antivirus Java en Windows.
rem Requiere tener el JDK instalado y "javac" disponible en el PATH.
 
cd /d "%~dp0.."
 
where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: no se encontro "javac". Instala el JDK y agregalo al PATH.
    echo Descarga: https://adoptium.net
    pause
    exit /b 1
)
 
if not exist out mkdir out
 
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
set BUILD_RESULT=%errorlevel%
del sources.txt
 
if %BUILD_RESULT% neq 0 (
    echo.
    echo ERROR al compilar.
    pause
    exit /b %BUILD_RESULT%
)
 
echo.
echo Compilado correctamente en out\
pause
