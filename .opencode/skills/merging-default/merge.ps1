$ErrorActionPreference = "Stop"

$currentBranch = git rev-parse --abbrev-ref HEAD
Write-Output "CURRENT_BRANCH:$currentBranch"

$status = git status --porcelain
if ($status) {
    Write-Output "UNCOMMITTED_CHANGES:true"
} else {
    Write-Output "UNCOMMITTED_CHANGES:false"
}

git fetch origin

$mainRef = git ls-remote --heads origin main
if ($mainRef -match "refs/heads/main") {
    $defaultBranch = "main"
} else {
    $defaultBranch = "master"
}
Write-Output "DEFAULT_BRANCH:$defaultBranch"

if ($currentBranch -eq $defaultBranch) {
    Write-Output "Already on the default branch. Nothing to merge."
    exit 0
}

git checkout $defaultBranch
git pull origin $defaultBranch
git merge --no-ff $currentBranch -m "chore: merge branch $currentBranch into $defaultBranch"
git push origin $defaultBranch
