@title webProgramming 24w25g
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-24w25g.jar
timeout 10
start .\%~nx0
exit