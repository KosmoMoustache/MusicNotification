#!/usr/bin/env bash

set -euo pipefail

for platform in fabric neoforge; do
	for verdir in "$platform"/versions/*; do
		mkdir "-p" "$verdir/run"
		rundir="$verdir/run"
		if [ -d "$rundir" ]; then
			printf 'Processing %s\n' "$rundir"

			# copy wd_void
			if [ -d "test/wd_void" ]; then
				mkdir -p "$rundir/saves"
				printf 'Copying test/wd_void -> %s/saves/\n' "$rundir"
				cp -a test/wd_void "$rundir/saves/"
			fi

			# copy dp_*
			if ls test/dp_* > /dev/null 2>&1; then
				mkdir -p "$rundir/saves/wd_void/datapacks"
				for dp in test/dp_*; do
					printf 'Copying %s -> %s/saves/wd_void/datapacks\n' "$dp" "$rundir"
					cp -a "$dp" "$rundir/saves/wd_void/datapacks"
				done
			fi

			# copy rp_*
			if ls test/rp_* > /dev/null 2>&1; then
				mkdir -p "$rundir/resourcepacks"
				for dp in test/rp_*; do
					printf 'Copying %s -> %s/resourcepacks\n' "$dp" "$rundir"
					cp -a "$dp" "$rundir/resourcepacks"
				done
			fi

			# copy options.txt
			if [ -f "test/options.txt" ]; then
            	printf 'Copying test/options.txt -> %s/options.txt\n' "$rundir"
            	cp -a "test/options.txt" "$rundir/options.txt"
            fi
		fi
	done
done
