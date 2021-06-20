@ECHO OFF
:server_start
echo.
echo =========================
echo Starting server. OS x64.
echo You need JAVA 8
echo =========================
echo.
java -Xmx2G -XX:MaxPermSize=256M -Dfile.encoding=UTF-8 -jar JustBot.jar nogui
echo.
echo =========================
echo PRESS CTRL+C TO STOP SERVER
echo =========================
echo.
timeout 5
goto server_start