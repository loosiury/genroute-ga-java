$ProjectRoot = Split-Path -Parent $PSScriptRoot
$JdkHome = Join-Path $ProjectRoot ".tools\jdk-21"
$MavenHome = Join-Path $ProjectRoot ".tools\apache-maven-3.9.11"
$LocalMavenRepo = Join-Path $ProjectRoot ".m2\repository"

if (-not (Test-Path (Join-Path $JdkHome "bin\java.exe"))) {
    throw "JDK portatil nao encontrado em $JdkHome"
}

if (-not (Test-Path (Join-Path $MavenHome "bin\mvn.cmd"))) {
    throw "Maven portatil nao encontrado em $MavenHome"
}

$env:JAVA_HOME = $JdkHome
$env:MAVEN_HOME = $MavenHome
$env:GENROUTE_PROJECT_ROOT = $ProjectRoot
$env:GENROUTE_MAVEN_REPO = $LocalMavenRepo
$env:Path = "$JdkHome\bin;$MavenHome\bin;$env:Path"

Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "MAVEN_HOME=$env:MAVEN_HOME"
Write-Host "MAVEN_REPO=$env:GENROUTE_MAVEN_REPO"
