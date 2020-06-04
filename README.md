# NEmuS

> An experimental NES Emulator written in Java

![Super Mario Bros](img/smb.gif)
![Legend of Zelda](img/zelda.gif)

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Libraries](#libraries)
- [Thanks](#thanks)
- [License](#license)


---

## Features
    F1 : Load ROM 
    F2 : Reset
    F3 : Force Savegame
    
    F5 : Pause/Resume Emulation
    F6 : Step by Step (when emulation paused)
    F7 : RAM page -
    F8 : RAM page +
     
    F9 : Frame by Frame
    F10 : Palette selection
    F12 : Help
* 6502 CPU Emulation with basic decompiler
* The ability to pause and advance Instruction by Instruction or Frame by Frame
* 2C02 PPU Emulation with Palette, Pattern Table and Nametable Viewer
* A Debug window (Powered by OpenGL) displaying :
  - The state of the CPU (Registers, Stack Pointer and Program Counter)
  - One page of RAM (as seen by the CPU) navigable
  - Disassembled code being executed
  - Object Attribute Memory
  - Palette, Pattern Tables and Nametables (with mirroring)
* iNES Mappers :
  - [NROM (000)](https://wiki.nesdev.com/w/index.php/INES_Mapper_000) : Super Mario Bros, Donkey Kong, Duck Hunt
  - [MMC1 (001)](https://wiki.nesdev.com/w/index.php/INES_Mapper_001) : Legend of Zelda, Zelda 2, Metroid
  - [UxROM (002)](https://wiki.nesdev.com/w/index.php/INES_Mapper_002) : Castlevania 
  - [CNROM (003)](https://wiki.nesdev.com/w/index.php/INES_Mapper_003) : Track & Field
  - [MMC3 (004)](https://wiki.nesdev.com/w/index.php/INES_Mapper_004) : Super Mario Bros 2, Super Mario Bros 3
  - [GxROM (066)](https://wiki.nesdev.com/w/index.php/INES_Mapper_066) : Super Mario Bros + Duck Hunt
* Emulation of saves for games supporting it (Every 30s)

## Screenshots
![Debug Window](img/debug.gif)
![Super Mario Bros 3](img/smb3.gif)
![Track & Field](img/t&f.gif)

## Libraries
- **[LWJGL 3](https://www.lwjgl.org/)**

## Thanks
- **[OneLoneCoder](https://www.youtube.com/channel/UC-yuWVUplUJZvieEligKBkA)** [(Github)](https://github.com/OneLoneCoder) For his amazing video series about the NES and its inner workings
- **[NESDev Wiki](https://wiki.nesdev.com/w/index.php/Nesdev_Wiki)** For making available all of this information in one place

## License

This project is licensed under the **[MIT license](http://opensource.org/licenses/mit-license.php)**
