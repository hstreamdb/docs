tag ?= v0.6.1

config:
	python3 -c 'import json; import yaml; json.dump(yaml.safe_load(open("directory.yaml")), open("docs/directory.json", "w"), ensure_ascii=False)'

upgrade-docker-image:
	find docs/ -type f -exec sed -i -E \
		"s#hstreamdb/hstream\ #hstreamdb/hstream:$(tag)\ #g; \
		 s#hstreamdb/hstream\:[^\ ]+\ #hstreamdb/hstream:$(tag)\ #g \
		" {} +
