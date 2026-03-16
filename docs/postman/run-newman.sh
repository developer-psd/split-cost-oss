#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
COLLECTION_SRC="Postman/Expense Splitter - Automated E2E.postman_collection.json"
ENV_SRC="Postman/Expense Splitter - Local.postman_environment.json"
REPORT_DIR="reports"
TMP_DIR=".tmp"
TMP_COLLECTION="$TMP_DIR/runtime.collection.json"
TMP_ENV="$TMP_DIR/runtime.environment.json"

if [[ ! -f "$COLLECTION_SRC" ]]; then
  echo "Collection not found: $COLLECTION_SRC"
  echo "Run this script from the postman pack root folder."
  exit 1
fi

mkdir -p "$REPORT_DIR/html" "$REPORT_DIR/junit" "$TMP_DIR"

if [[ ! -d node_modules ]]; then
  echo "Installing Newman dependencies..."
  npm install
fi

if [[ -x "./node_modules/.bin/newman" ]]; then
  NEWMAN_CMD=("./node_modules/.bin/newman")
else
  NEWMAN_CMD=("npx" "newman")
fi

export BASE_URL COLLECTION_SRC ENV_SRC TMP_COLLECTION TMP_ENV

python3 - <<'PY'
import json, os, re

base_url = os.environ['BASE_URL'].rstrip('/')
collection_src = os.environ['COLLECTION_SRC']
env_src = os.environ['ENV_SRC']
tmp_collection = os.environ['TMP_COLLECTION']
tmp_env = os.environ['TMP_ENV']

with open(collection_src, 'r', encoding='utf-8') as f:
    collection = json.load(f)

# Force collection-level baseUrl variable
variables = collection.setdefault('variable', [])
found = False
for var in variables:
    if var.get('key') == 'baseUrl':
        var['value'] = base_url
        found = True
        break
if not found:
    variables.insert(0, {'key': 'baseUrl', 'value': base_url})

def normalize_url(url):
    if isinstance(url, str):
        return url
    if isinstance(url, dict):
        raw = url.get('raw')
        if raw:
            return raw
        path = url.get('path') or []
        if isinstance(path, list):
            return '{{baseUrl}}/' + '/'.join(str(p).strip('/') for p in path)
    return '{{baseUrl}}'


def patch_events(events):
    for ev in events or []:
        script = ev.get('script', {})
        exec_lines = script.get('exec') or []
        patched = []
        for line in exec_lines:
            line = re.sub(r'^\s*const\s+data\s*=\s*pm\.response\.json\(\);', 'var data = pm.response.json();', line)
            patched.append(line)
        script['exec'] = patched
        ev['script'] = script


def patch_items(items):
    patched_items = []
    for item in items or []:
        name = item.get('name', '')
        # Skip known broken/stubbed requests from automated runs
        if name.startswith('99 - Known Current API Gaps'):
            continue
        if 'item' in item:
            item['event'] = item.get('event', [])
            patch_events(item.get('event'))
            item['item'] = patch_items(item.get('item'))
            patched_items.append(item)
            continue
        req = item.get('request', {})
        req['url'] = normalize_url(req.get('url'))
        item['request'] = req
        patch_events(item.get('event'))
        patched_items.append(item)
    return patched_items

collection['item'] = patch_items(collection.get('item'))

with open(tmp_collection, 'w', encoding='utf-8') as f:
    json.dump(collection, f, indent=2)

# Build a fresh runtime environment each run to avoid stale values
runtime_env = {
    'id': 'runtime-env',
    'name': 'Expense Splitter - Runtime',
    'values': [
        {'key': 'baseUrl', 'value': base_url, 'enabled': True},
        {'key': 'run_id', 'value': '', 'enabled': True},
        {'key': 'trip_id', 'value': '', 'enabled': True},
        {'key': 'participant_flow_trip_id', 'value': '', 'enabled': True},
        {'key': 'negative_trip_id', 'value': '', 'enabled': True},
        {'key': 'transaction_1_id', 'value': '', 'enabled': True},
        {'key': 'basic_settlement_count', 'value': '0', 'enabled': True},
        {'key': 'participant_Alice', 'value': '', 'enabled': True},
        {'key': 'participant_Bob', 'value': '', 'enabled': True},
        {'key': 'participant_Smith', 'value': '', 'enabled': True},
        {'key': 'participant_Wonder', 'value': '', 'enabled': True},
        {'key': 'participant_Mat', 'value': '', 'enabled': True},
        {'key': 'participant_Philips', 'value': '', 'enabled': True},
        {'key': 'participant_Aleena', 'value': '', 'enabled': True},
        {'key': 'participant_Kimberly', 'value': '', 'enabled': True},
        {'key': 'participant_Kiran', 'value': '', 'enabled': True},
        {'key': 'transactionsBody', 'value': '', 'enabled': True},
    ],
    '_postman_variable_scope': 'environment'
}
# Try to preserve non-baseUrl vars from existing environment if available
try:
    with open(env_src, 'r', encoding='utf-8') as f:
        existing_env = json.load(f)
    for entry in existing_env.get('values', []):
        key = entry.get('key')
        if key and key != 'baseUrl' and not any(v['key'] == key for v in runtime_env['values']):
            runtime_env['values'].append({'key': key, 'value': entry.get('value', ''), 'enabled': bool(entry.get('enabled', True))})
except Exception:
    pass

with open(tmp_env, 'w', encoding='utf-8') as f:
    json.dump(runtime_env, f, indent=2)
PY

echo "Running Newman against: $BASE_URL"
echo "Collection: $TMP_COLLECTION"
echo "Environment: $TMP_ENV"

REPORTERS="cli,junit"
ARGS=(
  run "$TMP_COLLECTION"
  -e "$TMP_ENV"
  --env-var "baseUrl=$BASE_URL"
  --global-var "baseUrl=$BASE_URL"
  --bail
  --reporters "$REPORTERS"
  --reporter-junit-export "$REPORT_DIR/junit/results.xml"
)

if [[ -d "./node_modules/newman-reporter-htmlextra" ]]; then
  REPORTERS="cli,junit,htmlextra"
  ARGS=(
    run "$TMP_COLLECTION"
    -e "$TMP_ENV"
    --env-var "baseUrl=$BASE_URL"
    --global-var "baseUrl=$BASE_URL"
    --bail
    --reporters "$REPORTERS"
    --reporter-junit-export "$REPORT_DIR/junit/results.xml"
    --reporter-htmlextra-export "$REPORT_DIR/html/report.html"
  )
fi

"${NEWMAN_CMD[@]}" "${ARGS[@]}"
