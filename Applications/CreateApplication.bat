@echo off
set /p input= Name of Application 
mkdir %input%
cd %input%
copy /y NUL pom.xml
mkdir src
cd src
mkdir main
cd main
mkdir java
cd java
copy /y NUL %input%.java