@echo off
REM Script to test RAG queries

echo Testing Camel Docling RAG - Query System
echo =========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: curl is not installed or not in PATH
    exit /b 1
)

REM Check if the application is running
echo Checking if application is running...
curl -s http://localhost:8080/api/health >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Application is not running
    echo Please start the application first with: gradlew run
    exit /b 1
)

echo Application is running!
echo.

REM Query 1: What is cloud computing?
echo Query 1: What is cloud computing?
echo ---------------------------------
curl -X POST http://localhost:8080/api/query ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"What is cloud computing?\"}"

echo.
echo.
echo.

REM Query 2: Service models
echo Query 2: What are the different cloud service models?
echo -----------------------------------------------------
curl -X POST http://localhost:8080/api/query ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"What are the different cloud service models?\"}"

echo.
echo.
echo.

REM Query 3: Benefits
echo Query 3: What are the benefits of cloud computing?
echo --------------------------------------------------
curl -X POST http://localhost:8080/api/query ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"What are the benefits of cloud computing?\"}"

echo.
echo.
echo All queries completed!
echo.
pause
