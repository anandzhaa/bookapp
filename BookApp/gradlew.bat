@echo off
setlocal
set DIR=%~dp0
set APP_HOME=%DIR:~0,-1%
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
"%JAVA_HOME%\bin\java" %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
