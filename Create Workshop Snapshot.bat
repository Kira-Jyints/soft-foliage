@echo off
cd /d "%~dp0"

echo Starting Soft Foliage workshop snapshot...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0make-workshop-snapshot.ps1"

echo.
echo Done. You can close this window.
pause