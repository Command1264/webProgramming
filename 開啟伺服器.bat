@title webProgramming 0.10.2
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-0.10.2.jar
timeout 10
start .\%~nx0
exit