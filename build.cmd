net stop TeamCity
del C:\TeamCity\webapps\ROOT\WEB-INF\plugins\teamcity-cloudshell-plugin.zip
powershell -c "Get-Process java* | Where-Object {$_.Path -match 'TeamCity'} | Stop-Process"
if %errorlevel% neq 0 exit /b %errorlevel%
call mvn clean
if %errorlevel% neq 0 exit /b %errorlevel%
call mvn package
if %errorlevel% neq 0 exit /b %errorlevel%
copy target\teamcity-cloudshell-plugin.zip C:\TeamCity\webapps\ROOT\WEB-INF\plugins /Y
call C:\TeamCity\bin\teamcity-server.bat start
if %errorlevel% neq 0 exit /b %errorlevel%
powershell -c "Start-Sleep -s 10"
C:\TeamCity\buildAgent\bin\agent.bat start
if %errorlevel% neq 0 exit /b %errorlevel%