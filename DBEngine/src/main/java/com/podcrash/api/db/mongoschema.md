hello! this is the schema (I am proposing to use)

```bash
players: <uuid>
```
for other stuff like permissions, currency, kit unlocks, settings, etc.
we can either do embedding or document reference.

for example, for champions, I decided to embed documents
```bash
players: <uuid>, championskits: {????, ?????, ????}
```

maybe for stuff like permissions, we may need doc ref, and kit unlocks probably another embed doc
and etc.

as of right now, all the database does is add the players to its collections when they join
