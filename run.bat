@echo off
chcp 65001
dir /s /b src\*.java > sources.txt
javac -d bin -encoding UTF-8 @sources.txt
java -cp bin .\src\Main.java
pause
