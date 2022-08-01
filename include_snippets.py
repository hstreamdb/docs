#!/usr/bin/env python3

import os

FILE_TYPES = {".py": "python", "cpp": "cpp"}


def run_snippet_cmd(snippets_root, snippet_file, *labels):
    lines = [[] for _ in labels]
    with open(os.path.join(snippets_root, snippet_file), "r") as f:
        idx = None
        for line in f:
            for i, label in enumerate(labels):
                # first match
                if idx is None and f"[{label}]" in line.split():
                    idx = i
                    break
                # secod match
                elif idx is not None and f"[{label}]" in line.split():
                    idx = None
                    break
            else:
                if idx is not None:
                    lines[idx].append(line)
    # TODO: strip indent
    extension = os.path.splitext(snippet_file)[1]
    ft = FILE_TYPES[extension]
    blocks = "\n\n\n".join("".join(xs).strip() for xs in lines)
    return f"```{ft}\n{blocks}\n```\n"


support_commands = [("@snippet", run_snippet_cmd)]


def parse_commands(lines):
    for linenum, line in enumerate(lines):
        for cmd, _ in support_commands:
            if line.startswith(cmd):
                yield (linenum, line.strip().split())


def run_commands(snippets_root, filepath):
    with open(filepath, "r") as f:
        # for small doc files, this is OK
        lines = f.readlines()
        commands = parse_commands(lines)
        for cmd_linenum, cmds in commands:
            for support_cmd, fun in support_commands:
                if cmds[0] == support_cmd:
                    new_lines = fun(snippets_root, *cmds[1:])
                    lines[cmd_linenum] = new_lines

    with open(filepath, "w") as f:
        f.write("".join(lines))


if __name__ == "__main__":
    import argparse
    import glob

    parser = argparse.ArgumentParser(description="Including Your Snippets")
    parser.add_argument(
        "--snippets-root", type=str, help="your snippets root dir", default=""
    )
    parser.add_argument(
        "--file-pattern", type=str, help="dest file pattern", required=True
    )
    args = parser.parse_args()

    for file in glob.iglob(args.file_pattern):
        run_commands(args.snippets_root, file)
