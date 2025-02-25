# Mii Database Editor

### A Java GUI application for editing Wii's Mii database

The Mii database ([RFL_DB.dat](https://wiibrew.org/wiki//shared2/menu/FaceLib/RFL_DB.dat)) can be found at `/shared2/menu/FaceLib/RFL_DB.dat`

**Capabilities:**
- Load/Save Mii database files.
- Automatically calculate valid checksums when saving, allowing files to be opened in the Mii Channel.
- Fix Mii databases with inncorrect checksums.
- View raw Mii data.
- Import/Export individual Miis.
- Change System IDs of Miis (Miis from other Wiis cannot be edited on the Mii Channel).
- Rearrange Mii data in the database (this does not affect functionality in the Mii Channel).

**What it does NOT do:**
- Edit Miis. Use these tools instead:
  - Mii Channel
  - [Mii Info Editor](https://kazuki-4ys.github.io/web_apps/MiiInfoEditorCTR/)
  - [My Avatar Editor](https://rc24.xyz/goodies/mii/?avatar "My Avatar Editor")
- Convert Miis to different consoles (Wii U, 3DS, Switch).

**Future Plans:**
- Mii Parade support (View/Edit/Save Mii Parade data)
- Modify Mii IDs (Miis with the same Mii ID can break certain games)
- Render Miis directly within the app.

**Contributions Welcome:**  
Have a suggestion or found an issue? Feel free to open an issue or submit a pull request!
