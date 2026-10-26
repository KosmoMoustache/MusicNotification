#!/usr/bin/env node
// Compares every lang file against en_us.json.
//
// Usage: node scripts/check-lang-keys.mjs [path/to/en_us.json] [langDir]
//
// Reports, per file:
//   orphan:  key present in the file but absent from en_us.json
//   missing: key present in en_us.json but absent from the file
// Exit code 1 if at least one orphan key is found.

import { readFileSync, readdirSync } from "node:fs";
import { basename, join, resolve } from "node:path";

const DEFAULT_LANG_DIR = resolve("common/src/main/resources/assets/musicnotification/lang");

const enPath = resolve(process.argv[2] ?? join(DEFAULT_LANG_DIR, "en_us.json"));
const langDir = resolve(process.argv[3] ?? join(enPath, ".."));

function readLang(path) {
	const raw = readFileSync(path, "utf8");
	try {
		return JSON.parse(raw);
	} catch (error) {
		console.error(`Invalid JSON in ${path}: ${error.message}`);
		process.exit(2);
	}
}

const en = readLang(enPath);
const enKeys = new Set(Object.keys(en));
const enFile = basename(enPath);

const others = readdirSync(langDir)
	.filter((file) => file.endsWith(".json") && file !== enFile)
	.sort();

let orphanCount = 0;

for (const file of others) {
	const keys = new Set(Object.keys(readLang(join(langDir, file))));
	const orphans = [...keys].filter((key) => !enKeys.has(key)).sort();
	const missing = [...enKeys].filter((key) => !keys.has(key)).sort();
	orphanCount += orphans.length;

	console.log(`\n== ${file} ==`);
	console.log(`orphan (${orphans.length}):`);
	for (const key of orphans) console.log(`  - ${key}`);
	console.log(`missing (${missing.length}):`);
	for (const key of missing) console.log(`  - ${key}`);
}

console.log(`\nTotal orphan keys: ${orphanCount}`);
process.exit(orphanCount > 0 ? 1 : 0);
