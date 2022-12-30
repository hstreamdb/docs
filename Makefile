tag ?= latest

ifeq ($(tag), latest)
branch ?= main
else
branch ?= $(tag)
endif

config:
	script/make-config

upgrade-docker-image:
	find docs/ -type f -exec \
		sed -i -E \
			"s#hstreamdb/hstream*\ #hstreamdb/hstream:$(tag)\ #g; \
			s#hstreamdb/hstream:latest\ #hstreamdb/hstream:$(tag)\ #g; \
			s#raw.githubusercontent.com/hstreamdb/docs/main/#raw.githubusercontent.com/hstreamdb/docs/$(branch)/#g ; \
			s#hstreamdb/hstream\:v[0-9]+(\.[0-9]+)*#hstreamdb/hstream:$(tag)#g \
			" {} +
	find assets/ -type f -exec \
		sed -i -E \
			"s#hstreamdb/hstream:latest#hstreamdb/hstream:$(tag)#g; \
			" {} +
