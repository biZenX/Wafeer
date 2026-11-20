# Security and Secret Protection Rules

## 1. Zero-Leakage Credential Policy
- **CRITICAL RESTRICTION**: Never output, hardcode, commit, or write any Personal Access Tokens (such as GitHub PAT `ghp_*`), API keys, keystore passwords, or private secrets into any repository file, markdown document, script, or commit.
- All operations requiring GitHub authentication MUST read the token securely from the environment variable `$env:GH_TOKEN` / `$env:GITHUB_TOKEN` or from the local private store at `~/.gemini/antigravity/user_secrets.json`.
- Never commit `.env`, `*secret*`, or token files to git, whether the repository is public or private.
