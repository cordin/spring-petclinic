#/bin/sh

TODAY=$(date +"%Y-%m-%d")
BRANCH="versions-autoupdate-$(TODAY)"

echo "Configuration required by github"

git config pull.rebase false
git config user.email "cordin@gmail.com"
git config user.name "cordin"

echo "Creating branch ${BRANCH}"

git pull -X theirs
git checkout -b ${BRANCH} master
#git merge --strategy-option theirs
#git add .
#git ci -m "Merge from master"
git push -u origin ${BRANCH}

echo "Updating versions"

./mvnw versions:update-parent versions:update-properties 

echo "Commiting and MR"

git add pom.xml
git commit -m "Update dependencies versions"
git push

