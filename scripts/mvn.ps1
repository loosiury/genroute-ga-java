. "$PSScriptRoot\env.ps1"

$ClassworldsJar = Get-ChildItem -Path (Join-Path $env:MAVEN_HOME "boot") -Filter "plexus-classworlds-*.jar" |
    Select-Object -First 1

if (-not $ClassworldsJar) {
    throw "Launcher do Maven nao encontrado em $env:MAVEN_HOME\boot"
}

Push-Location $env:GENROUTE_PROJECT_ROOT
& "$env:JAVA_HOME\bin\java.exe" `
    "--enable-native-access=ALL-UNNAMED" `
    "-classpath" $ClassworldsJar.FullName `
    "-Dclassworlds.conf=$env:MAVEN_HOME\bin\m2.conf" `
    "-Dmaven.home=$env:MAVEN_HOME" `
    "-Dlibrary.jansi.path=$env:MAVEN_HOME\lib\jansi-native" `
    "-Dmaven.multiModuleProjectDirectory=$env:GENROUTE_PROJECT_ROOT" `
    "org.codehaus.plexus.classworlds.launcher.Launcher" `
    "-Dmaven.repo.local=$env:GENROUTE_MAVEN_REPO" `
    @args
$ExitCode = $LASTEXITCODE
Pop-Location
exit $ExitCode
