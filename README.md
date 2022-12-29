# HStreamDB Docs

Documentation for HStreamDB.

Push to the `main` branch will automatically deploy the `latest` document
through GitHub action. To create a new release, you need to run
`./script/create_release.sh`

## Preview with docker

```sh
make
docker run --rm -p 8080:8080 -it --name hstream-doc-preview \
        -v $PWD/docs/directory.json:/app/docs/.vuepress/config/directory.json \
        -v $PWD/docs/en:/app/docs/en/latest \
        -v $PWD/docs/zh:/app/docs/zh/latest \
        -e DOCS_TYPE=hstreamdb \
        -e VERSION=latest \
    ghcr.io/emqx/emqx-io-docs-frontend:latest
```

Now, open
<http://localhost:8080/docs/en/latest/start/quickstart-with-docker.html>
