# Release procedure

The pull-request CI is the authoritative implementation gate and performs one
complete compile/test/package/exact-input pass. Do not duplicate that full gate
between small local edits.

Before tagging:

1. Confirm the reviewed commit, clean repository, exact version, changelog,
   profile, and provenance identities.
2. Confirm PR CI passed on that commit and inspect its production JAR and
   publication metadata.
3. Run the one enabled staging gate in [STAGING.md](STAGING.md), including the
   save/restart persistence check, rendered 37-anchor census, browser sanity,
   and owner visual acceptance.
4. Merge any version change through a PR, then create the exact annotated tag
   `v<addon_version>` on the reviewed commit.

The tag workflow reacquires the exact third-party input, runs the same single
authoritative gate, creates SHA-256 checksums, and publishes a GitHub
prerelease plus the matching GitHub Packages Maven publication. The publish
task reuses already-built outputs and does not deploy to a server. The Pipez
JAR is temporary input and never a release asset.

If publication fails before release creation, fix through another PR and use a
new version/tag. Never move or overwrite a published tag or release asset.
