net stop TeamCity
powershell -c "Get-Process java* | Where-Object {$_.Path -match 'TeamCity'} | Stop-Process"
del C:\TeamCity\webapps\ROOT\WEB-INF\plugins\qsSandbox.zip
del C:\TeamCity\buildAgent\plugins\qs*
call C:\TeamCity\bin\teamcity-server.bat start
if %errorlevel% neq 0 exit /b %errorlevel%
call C:\TeamCity\buildAgent\bin\agent.bat start
if %errorlevel% neq 0 exit /b %errorlevel%