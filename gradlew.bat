@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@rem ---------------------------------------------------------------------------
@rem Brproject: atalhos (na raiz do projeto). Uso:
@rem   gradlew.bat br-menu             - abre menu interativo (brproject-menu.bat)
@rem   gradlew.bat br-compile          - compila Java+Kotlin (compileJava compileKotlin)
@rem   gradlew.bat br-compile-clean    - clean + compila
@rem   gradlew.bat br-ant-dist-test    - ant -f Mount.xml dist-test e inicia servidores
@rem ---------------------------------------------------------------------------
if /i "%~1"=="br-menu" (
call "%APP_HOME%\brproject-menu.bat"
goto end
)
if /i "%~1"=="br-compile" (
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain PrepararTeste jar compileJava compileKotlin
goto end
)
if /i "%~1"=="br-compile-clean" (
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain clean PrepararTeste jar compileJava compileKotlin
goto end
)
if /i "%~1"=="br-ant-dist-test" (
    "%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain PrepararTeste

    @rem Verifica se a task do Gradle falhou. Se falhar, nao tenta abrir os servidores.
    if errorlevel 1 (
        echo.
        echo [ERRO] Falha ao executar a task PrepararTeste no Gradle. Servidores nao serao iniciados.
        popd
        goto fail
    )
)

echo.
echo Preparar Teste.

@rem # STREAMING_CHUNK:Starting Login Server
echo Iniciando StartLogin_SemDashboard.bat...
if exist "Brproject_Distribution\StartLogin_SemDashboard.bat" (
start "Login Server" cmd /c "cd /d Brproject_Distribution && call StartLogin_SemDashboard.bat"
) else (
echo Aviso: Arquivo Brproject_Distribution\StartLogin_SemDashboard.bat nao encontrado!
)

@rem # STREAMING_CHUNK:Starting Game Server
echo Iniciando StartGame_SemDashboard.bat...
if exist "Brproject_Distribution\StartGame_SemDashboard.bat" (
start "Game Server" cmd /c "cd /d Brproject_Distribution && call StartGame_SemDashboard.bat"
) else (
echo Aviso: Arquivo Brproject_Distribution\StartGame_SemDashboard.bat nao encontrado!
)

popd
goto end
)

@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the script return code instead of
rem the cmd.exe /c return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega