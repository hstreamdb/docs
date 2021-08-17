# HStreamDB Docs

Documentation for HStreamDB.

Push to the `main` branch will automatically deploy the `latest` document
through GitHub action. Tagging will deploy a specfic versioned document which is
just the tag's name.

## Preview with docker

Under project root and run:

```sh
docker run -it --rm -p 8000:8000 -v ${PWD}:/docs squidfunk/mkdocs-material serve -a 0.0.0.0:8000
```

Now open <http://127.0.0.1:8000> in your browser.
