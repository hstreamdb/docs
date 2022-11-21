tag ?= latest

config:
	script/make-config

upgrade-docker-image:
	find docs/ assets/ -type f -exec \
		sed -i -E \
			"s#hstreamdb/hstream\ #hstreamdb/hstream:$(tag)\ #g; \
			s#hstreamdb/hstream\:v[0-9]+(\.[0-9]+)*#hstreamdb/hstream:$(tag)#g \
			" {} +
