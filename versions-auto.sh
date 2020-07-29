#/bin/sh

BRANCH="versions-autoupdate"

echo "Configuration required by github"

git config pull.rebase false
git config user.email "cordin@gmail.com"
git config user.name "cordin"

echo "Creating or updating branch ${BRANCH}"

git pull -X theirs
git checkout -b ${BRANCH} master
git merge --strategy-option theirs
git push -u origin ${BRANCH}

echo "Updating versions"

./mvnw versions:update-parent versions:update-properties 

echo "Commiting and MR"

git add pom.xml
git commit -m "Update dependencies versions"
git push

