@echo off
chcp 65001 >nul
cd src
javac -cp . *.java
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp . Main
pause

