#!/usr/bin/env node
// Usage: node tools/check-permission-codes.mjs   (from frontend/)
//
// Holds the frontend's permission codes to the backend's, the way
// `backend/tools/generate_permission_seed.py --check` holds the seed to the annotations. Together
// the two make one chain: @RequiresPermission -> 04-permissions.csv -> configs/permissions.js.
//
// What it cannot check is the other direction - that a page which ought to be gated is gated at
// all. Nothing here notices a route that simply forgot `meta.permission`.

import { readFileSync, readdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

import { RESOURCE } from '../src/configs/permissions.js';

const here = dirname(fileURLToPath(import.meta.url));
const SEED = join(here, '../../backend/src/main/resources/db/changelog/csv/04-permissions.csv');
const TABLE_CONFIGS = join(here, '../src/configs/tables');

const ACTIONS = ['read', 'create', 'update', 'delete'];

const errors = [];
const notes = [];

/** The seed is semicolon-separated with single-quoted values; only two columns matter here. */
function seededResources() {
  const [header, ...rows] = readFileSync(SEED, 'utf8').trim().split('\n');
  const columns = header.split(';');
  const resourceAt = columns.indexOf('resource');
  const actionAt = columns.indexOf('action');
  const unquote = (value) => value.trim().replace(/^'|'$/g, '');

  const byResource = new Map();
  for (const row of rows) {
    const cells = row.split(';');
    const resource = unquote(cells[resourceAt]);
    if (!byResource.has(resource)) {
      byResource.set(resource, new Set());
    }
    byResource.get(resource).add(unquote(cells[actionAt]));
  }
  return byResource;
}

const seeded = seededResources();

// 1. Every code the frontend can build is a code the backend knows.
for (const [key, resource] of Object.entries(RESOURCE)) {
  const actions = seeded.get(resource);
  if (!actions) {
    errors.push(`RESOURCE.${key} = '${resource}' is in no permission the backend seeds`);
    continue;
  }
  const missing = ACTIONS.filter((action) => !actions.has(action));
  if (missing.length > 0) {
    errors.push(`RESOURCE.${key} = '${resource}' has no ${missing.join(', ')} permission seeded`);
  }
}

// 2. Resources the frontend does not name. Not an error: the UI has no page for every one of them.
const named = new Set(Object.values(RESOURCE));
for (const resource of seeded.keys()) {
  if (!named.has(resource)) {
    notes.push(`no page gates '${resource}' - the backend seeds it, the frontend never asks`);
  }
}

// 3. Every table declares its resource, and declares its own rather than a neighbour's. A config
//    that copied the line from the file next to it is the mistake nothing else would show.
for (const file of readdirSync(TABLE_CONFIGS).filter((name) => name.endsWith('.config.js'))) {
  const tableKey = file.replace('.config.js', '');
  const source = readFileSync(join(TABLE_CONFIGS, file), 'utf8');
  const declared = source.match(/permissionResource:\s*RESOURCE\.(\w+)/);
  if (!declared) {
    errors.push(`${file} declares no permissionResource, so its row actions are ungated`);
  } else if (declared[1] !== tableKey) {
    errors.push(`${file} declares RESOURCE.${declared[1]}, which belongs to another table`);
  } else if (!RESOURCE[tableKey]) {
    errors.push(`${file} declares RESOURCE.${tableKey}, which does not exist`);
  }
}

for (const note of notes) {
  console.log(`note: ${note}`);
}
for (const error of errors) {
  console.error(`error: ${error}`);
}
console.log(
  `${Object.keys(RESOURCE).length} resources named, ${seeded.size} seeded, ${errors.length} errors`
);
process.exit(errors.length === 0 ? 0 : 1);
