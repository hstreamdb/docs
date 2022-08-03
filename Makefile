tag ?= v0.9.0

config:
	script/make-config

upgrade-docker-image:
	find docs/ -type f -exec sed -i -E \
		"s#hstreamdb/hstream\ #hstreamdb/hstream:$(tag)\ #g; \
		 s#hstreamdb/hstream\:[^\ ]+\ #hstreamdb/hstream:$(tag)\ #g \
		" {} +
