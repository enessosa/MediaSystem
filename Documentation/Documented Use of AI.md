Every file written or manipulated by AI will be tagged, either in the file itself or in the commit message that adds/changes it.

- **Markdown files** contain ``(AI)`` or similar in the first line (for rendered files like the README as an HTML comment, e.g. ``<!-- (AI) -->``), or the commit message states that the file was written/changed with AI.
- **Code** is marked appropriately depending on the language structure (e.g. a comment in the file), or the commit message states that it was written/changed with AI.

Commit messages name the AI tool and what it did, e.g. `modified the frontend with claude to support password change`.
