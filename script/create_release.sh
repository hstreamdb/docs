#!/bin/bash

echo "Create a new release from current main branch."
read -p "Tag(e.g. v1.0.0): " tag
BRANCH="release_$tag"

git checkout -b $BRANCH main
tag=$tag make upgrade-docker-image
git add docs/ assets/
git commit -m "Release $tag"
git tag $tag
git checkout main
git --no-pager diff main $BRANCH

echo ""
echo "=================================="
echo ""
echo "Next:"
echo "- To push the release: git push upstream $BRANCH --tags"
echo "- To discard the release: git branch -D $BRANCH && git tag $tag --delete"
