config:
	python3 -c 'import json; import yaml; json.dump(yaml.safe_load(open("directory.yaml")), open("docs/directory.json", "w"), ensure_ascii=False)'
