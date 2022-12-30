#!/bin/bash
set -e

echo "Create a new release from current main branch."

TAG="$1"
[ -z "$TAG" ] && read -p "Tag(e.g. v1.0.0): " TAG
BRANCH="release_$TAG"

if [ $(git rev-parse --verify $BRANCH 2>/dev/null) ]; then
    echo "Alreay has branch $BRANCH, abort!"
    exit 1
fi

if [ -n "$(git diff)" ]; then
    echo "Current workspace not clean, abort!"
    exit 1
fi

git checkout -b $BRANCH main
tag=$TAG make upgrade-docker-image
git add docs/ assets/
git commit -m "Release $TAG"
git tag $TAG
git checkout main
git --no-pager diff main $BRANCH

echo ""
echo "=================================="
echo ""
echo "Next:"
echo "- To push the release: git push upstream $BRANCH --tags"
echo "- To discard the release: git branch -D $BRANCH && git tag $TAG --delete"
