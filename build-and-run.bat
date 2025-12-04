@echo off
REM Quick Build and Run Script for Camel Docling RAG

echo ========================================
echo  Camel Docling RAG - Build and Run
echo ========================================
echo.

REM Navigate to project directory
cd /d "%~dp0"

echo Step 1: Cleaning previous build...
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Clean failed
    pause
    exit /b 1
)
echo.

echo Step 2: Building project...
call gradlew build
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed
    echo Please check the error messages above
    pause
    exit /b 1
)
echo.

echo ========================================
echo       BUILD SUCCESSFUL
echo ========================================
echo.
echo Starting the application...
echo.
echo The application will be available at:
echo   - http://localhost:8080/api/health
echo   - http://localhost:8080/api/ingest
echo   - http://localhost:8080/api/query
echo.
echo Press Ctrl+C to stop the application
echo ========================================
echo.

REM Run the application
call gradlew run
