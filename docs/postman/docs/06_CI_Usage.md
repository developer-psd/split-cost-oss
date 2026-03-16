# 06 CI Usage

## Minimal CI command
```bash
npm ci
npm run run:local
```

## Recommended pipeline behavior
- fail the job on any Newman assertion failure
- archive the JUnit XML and HTML report as pipeline artifacts
- publish the JUnit XML to the CI test-report view if supported

## Good places to use this pack
- pull request smoke validation
- nightly API regression
- pre-demo sanity check
- interview submission evidence pack
