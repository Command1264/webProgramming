@title webProgramming 24w26d
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-24w26d.jar
timeout 10
start .\%~nx0
exit