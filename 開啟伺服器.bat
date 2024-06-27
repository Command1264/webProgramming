@title webProgramming 24w26e
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-24w26e.jar
timeout 10
start .\%~nx0
exit