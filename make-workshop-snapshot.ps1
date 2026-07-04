$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$SnapshotName = "SoftFoliage-WorkshopSnapshot-Latest.zip"
$OutputPath = Join-Path $ProjectRoot $SnapshotName
$TempPath = Join-Path $ProjectRoot "_snapshot_temp"

$ExcludeDirs = @(
    ".git",
    ".gradle",
    ".idea",
    "build",
    "run",
    "_snapshot_temp"
)

$ExcludeFiles = @(
    $SnapshotName
)

Write-Host ""
Write-Host "Soft Foliage Workshop Snapshot" -ForegroundColor Green
Write-Host "--------------------------------"
Write-Host "Project root: $ProjectRoot"
Write-Host ""

if (Test-Path $TempPath) {
    Remove-Item $TempPath -Recurse -Force
}

if (Test-Path $OutputPath) {
    Remove-Item $OutputPath -Force
}

New-Item -ItemType Directory -Path $TempPath | Out-Null

$IncludedFileCount = 0

Get-ChildItem $ProjectRoot -Force | ForEach-Object {
    $Name = $_.Name

    if ($_.PSIsContainer) {
        if ($ExcludeDirs -notcontains $Name) {
            Copy-Item $_.FullName -Destination (Join-Path $TempPath $Name) -Recurse -Force
        }
    } else {
        if ($ExcludeFiles -notcontains $Name) {
            Copy-Item $_.FullName -Destination (Join-Path $TempPath $Name) -Force
        }
    }
}

$IncludedFileCount = (Get-ChildItem $TempPath -Recurse -File | Measure-Object).Count

Compress-Archive -Path (Join-Path $TempPath "*") -DestinationPath $OutputPath -Force

Remove-Item $TempPath -Recurse -Force

$SizeBytes = (Get-Item $OutputPath).Length
$SizeKB = [Math]::Round($SizeBytes / 1KB, 2)
$SizeMB = [Math]::Round($SizeBytes / 1MB, 2)

Write-Host ""
Write-Host "Snapshot created successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Filename: $SnapshotName"
Write-Host "Files included: $IncludedFileCount"
Write-Host "Size: $SizeKB KB / $SizeMB MB"
Write-Host ""
Write-Host "Ready to upload to the workshop sources." -ForegroundColor Cyan
Write-Host ""

explorer.exe /select,"$OutputPath"