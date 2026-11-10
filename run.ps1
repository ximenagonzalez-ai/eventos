# Script para arrancar el proyecto con Java 21
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
Write-Host "Usando JAVA_HOME: $env:JAVA_HOME"
.\mvnw.cmd spring-boot:run
