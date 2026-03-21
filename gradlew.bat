@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%DIRNAME%") do set APP_HOME=%%~fi
@rem Add default JVM options here.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
