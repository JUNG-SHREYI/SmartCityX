@echo off
setlocal EnableDelayedExpansion
title SmartCityX - Web Analytics Platform

set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
    set "JAVA_CMD=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
)

where npm >nul 2>nul
if errorlevel 1 (
    echo Node.js and npm are required to build the React interface.
    echo Install Node.js from https://nodejs.org/ and run start.bat again.
    pause
    exit /b 1
)

if not exist "web\node_modules" (
    echo Installing React application dependencies...
    pushd web
    call npm install
    if errorlevel 1 (
        popd
        echo Failed to install React dependencies.
        pause
        exit /b 1
    )
    popd
)

echo Building SmartCityX React interface...
pushd web
call npm run build
if errorlevel 1 (
    popd
    echo Failed to build the React interface.
    pause
    exit /b 1
)
popd

if not exist bin mkdir bin
set "JAVA_SOURCES="
for /r src %%F in (*.java) do set "JAVA_SOURCES=!JAVA_SOURCES! "%%F""
"%JAVAC_CMD%" -encoding UTF-8 -d bin -sourcepath src !JAVA_SOURCES!
if errorlevel 1 (
    echo Failed to compile SmartCityX Java sources.
    pause
    exit /b 1
)

set "SERVER_PORT="
for /l %%P in (8080,1,8090) do (
    powershell -NoProfile -Command "$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, %%P); try { $listener.Start(); $listener.Stop(); exit 0 } catch { exit 1 }" >nul 2>nul
    if not errorlevel 1 (
        set "SERVER_PORT=%%P"
        goto port_selected
    )
)

echo No available local port found between 8080 and 8090.
pause
exit /b 1

:port_selected
echo Opening SmartCityX in your browser at http://127.0.0.1:%SERVER_PORT%
start "" http://127.0.0.1:%SERVER_PORT%
"%JAVA_CMD%" -cp bin smartcityx.web.SmartCityXWebServer %SERVER_PORT%
