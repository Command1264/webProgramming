@title webProgramming 24w53a
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-24w53a.jar
timeout 10
start .\%~nx0
exit