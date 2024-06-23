@title webProgramming 24w26a
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-24w26a.jar
timeout 10
start .\%~nx0
exit