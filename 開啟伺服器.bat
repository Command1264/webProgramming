@title webProgramming 25w01a
@echo off

"C:/Program Files/Java/jdk-17/bin/java.exe" -jar target/webProgramming-25w01a.jar
timeout 10
start .\%~nx0
exit