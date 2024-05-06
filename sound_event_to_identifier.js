/**
 * Fill customId identifier in musics.json from sound event of sounds.json
 */

const log = require('node:console').log;
const readFileSync = require('node:fs').readFileSync;
const writeFileSync = require('node:fs').writeFileSync;
const join = require('node:path').join;

const rootPath = './src/main/resources/assets/musicnotification/';

const MUSICS = readFile(join(rootPath, 'musics.json'));
const RANDOM = readFile(join(rootPath, 'random.json'));
const SOUNDS = readFile(join(rootPath, 'sounds.json'));
const OUTPUT = {};
const SOUND_EVENT = {};

function readFile(file_name) {
  return JSON.parse(readFileSync(file_name, 'utf8'));
}

function writeFile(file_name, data) {
  writeFileSync(file_name, JSON.stringify(data, null, 2));
}

function sortJsonObject(obj) {
  const sortedArray = Object.entries(obj).sort((a, b) =>
    a[0].localeCompare(b[0])
  );
  const sortedObject = {};
  for (let i = 0; i < sortedArray.length; i++) {
    sortedObject[sortedArray[i][0]] = sortedArray[i][1];
  }
  return sortedObject;
}

for (const key of Object.keys(SOUNDS)) {
  const sound = SOUNDS[key];
  SOUND_EVENT[sound.sounds[0].name] = key;
}

log('SOUND_EVENT:', SOUND_EVENT);

for (const key of Object.keys(MUSICS)) {
  const music = MUSICS[key];
  OUTPUT[key] = music;
  const cleanedKey = key.replace('minecraft:', '');
  if (SOUND_EVENT[cleanedKey] !== undefined)
    OUTPUT[key].customId = `musicnotification:${SOUND_EVENT[cleanedKey]}`;
  OUTPUT[key] = sortJsonObject(OUTPUT[key]);
}

log('OUTPUT:', OUTPUT);

writeFile(join(rootPath, 'musics-generated.json'), OUTPUT);
