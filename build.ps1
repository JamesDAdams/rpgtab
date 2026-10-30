Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression

$ErrorActionPreference = "Stop"
$rpgTabDir = $PSScriptRoot
if (-not $rpgTabDir) { $rpgTabDir = (Get-Location).Path }
$templateDir = "C:\Users\JamesAdams\AppData\Roaming\ModrinthApp\profiles\Template 1.21.1"
$metaLibs = "$env:APPDATA\ModrinthApp\meta\libraries"
$zuluJava = "$env:APPDATA\ModrinthApp\meta\java_versions\zulu21.52.203-ca-jre21.0.12.1-win_x64\bin\java.exe"
$ecjJar = Join-Path $templateDir "build_tools\ecj.jar"

$buildDir = Join-Path $rpgTabDir "build"
$classesDir = Join-Path $buildDir "classes"
$jarOut = Join-Path $buildDir "rpgtab-1.17.0-neoforge-1.21.1.jar"

if (Test-Path $buildDir) {
    Remove-Item $buildDir -Recurse -Force
}
New-Item -ItemType Directory -Path $classesDir -Force | Out-Null

$jars = Get-ChildItem -Path $metaLibs -Recurse -Filter '*.jar' | Where-Object {
    $_.FullName -notmatch '1\.20' -and
    $_.FullName -notmatch '26\.3' -and
    $_.FullName -notmatch '12\.0' -and
    $_.FullName -notmatch '21\.1\.233' -and
    $_.FullName -notmatch '21\.1\.247' -and
    $_.FullName -notmatch 'slim' -and
    $_.FullName -notmatch 'neoforge-.*-client\.jar' -and
    $_.FullName -notmatch 'loader-4\.0\.42' -and
    $_.FullName -notmatch 'loader-4\.0\.43' -and
    $_.FullName -notmatch 'authlib\\(10|4|7)\.'
} | ForEach-Object { $_.FullName }

$modJars = @(
    (Join-Path $templateDir "mods\ftb-quests-neoforge-2101.1.37.jar"),
    (Join-Path $templateDir "mods\ftb-library-neoforge-2101.1.39.jar"),
    (Join-Path $templateDir "mods\ftb-teams-neoforge-2101.1.11.jar"),
    (Join-Path $templateDir "mods\architectury-13.0.11-neoforge.jar")
)

$cp = ($jars + $modJars) -join ";"

$mainJavaFiles = Get-ChildItem -Path (Join-Path $rpgTabDir "src\main\java") -Recurse -Filter '*.java' | ForEach-Object { "`"$($_.FullName)`"" }
$testJavaFiles = Get-ChildItem -Path (Join-Path $rpgTabDir "src\test\java") -Recurse -Filter '*.java' | ForEach-Object { "`"$($_.FullName)`"" }
$javaFiles = $mainJavaFiles + $testJavaFiles
Write-Output "Compiling $($javaFiles.Count) Java files..."

$argFile = Join-Path $buildDir "ecj_args.txt"
$ecjArgs = @(
    "-21",
    "-nowarn",
    "-cp",
    "`"$cp`"",
    "-d",
    "`"$classesDir`""
) + $javaFiles

[System.IO.File]::WriteAllText($argFile, ($ecjArgs -join "`n"))

& $zuluJava -jar $ecjJar "@$argFile"
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed with code $LASTEXITCODE"
}
Write-Output "Compilation successful!"

# Run Unit Tests
Write-Output "Running Unit Tests..."
& $zuluJava -cp "$classesDir" com.rpgtab.test.RpgTabUnitTests
if ($LASTEXITCODE -ne 0) {
    throw "Unit tests failed with code $LASTEXITCODE"
}

$resDir = Join-Path $rpgTabDir "src\main\resources"
if (Test-Path $resDir) {
    Copy-Item -Path "$resDir\*" -Destination $classesDir -Recurse -Force
    Write-Output "Resources copied."
}

if (Test-Path $jarOut) { Remove-Item $jarOut -Force }

$zipStream = [System.IO.File]::Create($jarOut)
$archive = New-Object System.IO.Compression.ZipArchive($zipStream, [System.IO.Compression.ZipArchiveMode]::Create)

$allFiles = Get-ChildItem -Path $classesDir -Recurse -File
foreach ($file in $allFiles) {
    $relPath = $file.FullName.Substring($classesDir.Length).TrimStart("\", "/").Replace("\", "/")
    $entry = $archive.CreateEntry($relPath, [System.IO.Compression.CompressionLevel]::Optimal)
    $entryStream = $entry.Open()
    $fileStream = [System.IO.File]::OpenRead($file.FullName)
    $fileStream.CopyTo($entryStream)
    $fileStream.Close()
    $entryStream.Close()
}

$archive.Dispose()
$zipStream.Close()
Write-Output "Jar built: $jarOut ($((Get-Item $jarOut).Length) bytes)"

$deployedJar = Join-Path $templateDir "mods\rpgtab-1.17.0-neoforge-1.21.1.jar"
$oldJars = Get-ChildItem -Path (Join-Path $templateDir "mods") -Filter "rpgtab-*.jar"
foreach ($old in $oldJars) {
    Remove-Item $old.FullName -Force
}
Copy-Item -Path $jarOut -Destination $deployedJar -Force
Write-Output "Deployed to: $deployedJar"
