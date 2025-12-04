@echo off
REM Script to test document ingestion via HTTP API

echo Testing Camel Docling RAG - Document Ingestion
echo ===============================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: curl is not installed or not in PATH
    echo Please install curl to use this script
    exit /b 1
)

REM Check if the application is running
echo Checking if application is running...
curl -s http://localhost:8080/api/health >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Application is not running on port 8080
    echo Please start the application first with: gradlew run
    exit /b 1
)

echo Application is running!
echo.

REM Ingest the sample document
echo Ingesting sample document...
curl -X POST http://localhost:8080/api/ingest ^
  -H "Content-Type: text/markdown" ^
  -H "documentName: sample-document.md" ^
  --data-binary @data/sample-document.md

echo.
echo.
echo Document ingestion request sent!
echo Check the application logs for processing status.
echo.
pause
