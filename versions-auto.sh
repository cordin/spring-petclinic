#/bin/sh

BRANCH="versions-autoupdate"

echo "Creating branch ${BRANCH}"

git pull
git checkout -b ${BRANCH} master
git push -u origin ${BRANCH}

echo "Updating versions"

./mvnw versions:update-parent versions:update-properties 

echo "Commiting and MR"

git add pom.xml
git ci -m "Update dependencies versions"
git push

