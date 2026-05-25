@echo off
title Login Server - RusAcis
color 0B
cd /d "%~dp0"

REM --- Configurando para usar o Java 25 especificamente ---
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe"

REM --- Flags para VPS/servidor: evita crash de driver grafico (awt.dll) ---
REM Use Java 21 LTS em datacenter se ainda tiver problemas
REM -Dbrproject.safe.graphics=true = molduras e paineis com cores solidas (sem gradiente)
%JAVA_HOME% -Xms256m -Xmx512m -Dsun.java2d.opengl=false -Dsun.java2d.d3d=false -Dsun.java2d.pmoffscreen=false -Dbrproject.safe.graphics=true -cp "libs/*" ext.mods.security.LicenseInit
pause