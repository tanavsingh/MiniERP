$ErrorActionPreference = 'Stop'

Write-Host '========================================'
Write-Host 'MiniERP Desktop App Launcher (No Maven)'
Write-Host '========================================'

# Create directories
$libDir = 'lib'
$classesDir = 'target/classes'

if (!(Test-Path $libDir)) { New-Item -ItemType Directory -Force -Path $libDir | Out-Null }
if (!(Test-Path (Split-Path $classesDir -Parent))) { New-Item -ItemType Directory -Force -Path (Split-Path $classesDir -Parent) | Out-Null }
if (!(Test-Path $classesDir)) { New-Item -ItemType Directory -Force -Path $classesDir | Out-Null }

# Dependencies
$deps = @(
    @{url='https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar'; name='sqlite-jdbc-3.45.1.0.jar'},
    @{url='https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar'; name='flatlaf-3.4.jar'},
    @{url='https://repo1.maven.org/maven2/com/formdev/flatlaf-extras/3.4/flatlaf-extras-3.4.jar'; name='flatlaf-extras-3.4.jar'},
    @{url='https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar'; name='itextpdf-5.5.13.3.jar'},
    @{url='https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar'; name='commons-lang3-3.14.0.jar'}
)

Write-Host 'Downloading dependencies...' -ForegroundColor Green
foreach ($dep in $deps) {
    $path = Join-Path $libDir $dep.name
    try {
        Invoke-WebRequest -Uri $dep.url -OutFile $path -UseBasicParsing
        Write-Host "  OK: $($dep.name)" -ForegroundColor Green
    } catch {
        Write-Error "Failed to download $($dep.name): $($_.Exception.Message)"
        exit 1
    }
}

# Compile
Write-Host "`nCompiling Java sources..." -ForegroundColor Green
$libJars = (Get-ChildItem $libDir -Filter '*.jar' | ForEach-Object { $_.FullName }) -join ';'
$javaFiles = Get-ChildItem -Path 'src/main/java' -Recurse -Filter '*.java' | ForEach-Object { "`"$($_.FullName)`"" }

& javac -encoding UTF-8 -cp "$libJars" -d $classesDir $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Error 'Compilation failed!'
    exit 1
}

Write-Host 'Compilation successful!' -ForegroundColor Green

# Copy resources
Write-Host 'Copying resources...' -ForegroundColor Green
robocopy 'src\main\resources' $classesDir /MIR /NFL /NDL /NJH /NJS /nc /ns /nfl 2>$null
if ($LASTEXITCODE -gt 7) {
    Write-Warning 'Some resources copy failed (non-critical)'
}

# Classpath for Java
$cp = $classesDir + ';' + $libJars

Write-Host "`nLaunching MiniERP..." -ForegroundColor Green
Write-Host 'Login: faculty1 / faculty123' -ForegroundColor Yellow

# Run the app
& java -cp $cp -Dfile.encoding=UTF-8 com.minierp.main.MainApp

Write-Host "`nApp exited. Cleanup not performed." -ForegroundColor Cyan
