# HStreamDB Docs

Documentation for HStreamDB.

Push to the `main` branch will automatically deploy the `latest` document
through GitHub action. Tagging will deploy a specfic versioned document which is
just the tag's name.

## Preview with docker


```sh
docker build . -t hstreamdb/docs-dev

docker run -it --rm -p 8080:8080 -v $(pwd)/docs:/app/docs hstreamdb/docs-dev
```

Now, open <http://localhost:8080/docs/en>
