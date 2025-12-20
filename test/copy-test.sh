#!/usr/bin/env bash

set -euo pipefail

for platform in fabric neoforge; do
	for verdir in "$platform"/versions/*; do
		mkdir "-p" "$verdir/run"
		rundir="$verdir/run"
		if [ -d "$rundir" ]; then
			printf 'Processing %s\n' "$rundir"

			# copy wd_PlaygroundVoid
			mkdir -p "$rundir/saves"
			printf 'Copying test/wd_PlaygroundVoid -> %s/saves/\n' "$rundir"
			cp -a test/wd_PlaygroundVoid "$rundir/saves/"

			# copy dp_*
			mkdir -p "$rundir/saves/wd_PlaygroundVoid/datapacks"
			for dp in test/dp_*; do
				printf 'Copying %s -> %s/saves/wd_PlaygroundVoid/datapacks\n' "$dp" "$rundir"
				cp -a "$dp" "$rundir/saves/wd_PlaygroundVoid/datapacks"
			done

			# copy rp_*
			mkdir -p "$rundir/resourcepacks"
			for dp in test/rp_*; do
				printf 'Copying %s -> %s/resourcepacks\n' "$dp" "$rundir"
		  		cp -a "$dp" "$rundir/resourcepacks"
			done

			# copy options.txt
			if [ -f "test/options.txt" ]; then
            	printf 'Copying test/options.txt -> %s/options.txt\n' "$rundir"
            	cp -a "test/options.txt" "$rundir/options.txt"
            fi
		fi
	done
done
