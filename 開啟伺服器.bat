@title webProgramming 0.9.0
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-0.9.0.jar
timeout 10
start .\%~nx0
exit