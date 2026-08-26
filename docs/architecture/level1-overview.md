# Level 1 Architecture Overview

The Level 1 dependency flow is:

`Shroud Core -> persistent logical field -> canonical ShroudQuery -> terrain materialization + player exposure + entity corruption`

`Flame state -> Flame Altar -> sanctuary/passsage query -> ShroudQuery/exposure/mutation suppression`

`Story state -> ManifestationDirector -> selected Lich provider -> encounter defeat -> Enshrouded Lich Skull -> Flame Altar ritual -> Level 1 checkpoint`

Key separation: world visuals, external bosses and client effects consume authoritative Enshrouded state; none of them are the source of truth.
