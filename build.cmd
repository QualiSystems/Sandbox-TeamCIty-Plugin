call mvn package
if %errorlevel% neq 0 exit /b %errorlevel%
copy target\qsSandbox.zip C:\TeamCity\webapps\ROOT\WEB-INF\plugins /Y
net stop TeamCity
net start TeamCity