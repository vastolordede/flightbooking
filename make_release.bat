@echo off
setlocal

REM ====== CONFIG ======
set APP_JAR=dist\FlightBooking.jar
set MAIN_CLASS=flightbooking.FlightBooking

REM ====== CLEAN & CREATE RELEASE FOLDER ======
rmdir /s /q release 2>nul
mkdir release
mkdir release\dist
mkdir release\libs

REM ====== COPY FILES ======
copy "%APP_JAR%" release\dist\
xcopy /E /I /Y libs release\libs >nul

REM ====== CREATE RUN.BAT ======
(
echo @echo off
echo cd /d %%~dp0
echo echo Running FlightBooking...
echo java -cp "dist\FlightBooking.jar;libs\*" %MAIN_CLASS%
) > release\run.bat

echo ===================================
echo Release created at: release\
echo Double click release\run.bat
echo ===================================
endlocal
