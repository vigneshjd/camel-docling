@echo off
REM Setup Verification Script for Camel Docling RAG Project
REM This script checks if everything is properly set up

echo ========================================
echo Camel Docling RAG - Setup Verification
echo ========================================
echo.

REM Check 1: Java Installation
echo [1/5] Checking Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17 or higher
    goto :error
) else (
    echo [OK] Java is installed
    java -version 2>&1 | findstr /C:"version"
)
echo.

REM Check 2: Project Structure
echo [2/5] Checking project structure...
if not exist "build.gradle" (
    echo [ERROR] build.gradle not found. Are you in the correct directory?
    goto :error
)
if not exist "src\main\java\com\example\docling\rag\DoclingRagApplication.java" (
    echo [ERROR] Main application file not found
    goto :error
)
echo [OK] Project structure is correct
echo.

REM Check 3: Data Directories
echo [3/5] Checking data directories...
if not exist "data" mkdir data
if not exist "data\input" mkdir data\input
if not exist "data\output" mkdir data\output
if not exist "data\failed" mkdir data\failed
echo [OK] Data directories created/verified
echo.

REM Check 4: Gradle Wrapper
echo [4/5] Checking Gradle wrapper...
if not exist "gradlew.bat" (
    echo [ERROR] Gradle wrapper not found
    goto :error
)
echo [OK] Gradle wrapper found
echo.

REM Check 5: Sample Document
echo [5/5] Checking sample document...
if not exist "data\sample-document.md" (
    echo [WARNING] Sample document not found
    echo You can still use the application, but testing will require your own documents
) else (
    echo [OK] Sample document available
)
echo.

REM Summary
echo ========================================
echo        VERIFICATION COMPLETE
echo ========================================
echo.
echo Your Camel Docling RAG project is ready!
echo.
echo Next steps:
echo   1. Build the project:    gradlew build
echo   2. Run the application:  gradlew run
echo   3. Test ingestion:       test-ingest.bat
echo   4. Test queries:         test-query.bat
echo.
echo Documentation:
echo   - Quick Start:  QUICKSTART.md
echo   - Full Docs:    README.md
echo   - API Docs:     API.md
echo   - Summary:      PROJECT_SUMMARY.md
echo.
echo Optional Configuration:
echo   - Set OPENAI_API_KEY environment variable for production AI responses
echo   - Edit application.properties for custom configuration
echo.
goto :end

:error
echo.
echo ========================================
echo      VERIFICATION FAILED
echo ========================================
echo.
echo Please fix the errors above and run this script again.
echo.
pause
exit /b 1

:end
echo Press any key to continue...
pause >nul
