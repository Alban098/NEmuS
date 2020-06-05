# NEmuS

> An experimental NES Emulator written in Java

<img src="img/smb.gif" width="50%" alt="Super Mario Bros"><img src="img/zelda.gif" width="50%" alt="Legend of Zelda">

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Libraries](#libraries)
- [Thanks](#thanks)
- [License](#license)


---

## Features
    /---------------- Game Commands ----------------/
        F1 : Load ROM 
        F2 : Reset
        F3 : Force Savegame
        
    /---------------- Debug Commands ---------------/
        F5 : Pause/Resume Emulation
        F6 : Step by Step (when emulation paused)
        F7 : RAM page -
        F8 : RAM page + 
        F9 : Frame by Frame
        F10 : Palette selection
        F12 : Help
* 6502 CPU Emulation with basic decompiler
* 2C02 PPU Emulation with Palette, Pattern Table and Nametable Viewer
* 2A03 APU Emulation with 2 Pulse Channels and Noise Channel
* Mappable controls by editing the ```input_mapping.xml``` file
* Gamepads and Joystick support
* A Debug window (Powered by OpenGL) with the following features :
  - Display the state of the CPU (Registers, Stack Pointer and Program Counter)
  - Display one page of RAM (as seen by the CPU) navigable
  - Display the disassembled code being executed
  - Display the Object Attribute Memory
  - Display Palettes, Pattern Tables and Nametables (with mirroring)
  - Enable you to pause the emulation
  - Enable you to step Instruction by Instruction or Frame by Frame (When emulation is paused)
* iNES Mappers :
  - [NROM](https://wiki.nesdev.com/w/index.php/INES_Mapper_000) (000) : Super Mario Bros, Donkey Kong, Duck Hunt
  - [MMC1](https://wiki.nesdev.com/w/index.php/INES_Mapper_001) (001) : Legend of Zelda, Zelda 2, Metroid
  - [UxROM](https://wiki.nesdev.com/w/index.php/INES_Mapper_002) (002) : Castlevania 
  - [CNROM](https://wiki.nesdev.com/w/index.php/INES_Mapper_003) (003) : Track & Field
  - [MMC3](https://wiki.nesdev.com/w/index.php/INES_Mapper_004) (004) : Super Mario Bros 2, Super Mario Bros 3
  - [GxROM](https://wiki.nesdev.com/w/index.php/INES_Mapper_066) (066) : Super Mario Bros + Duck Hunt
* Emulation of saves for games supporting it (every 30s)

## How to Use
- To launch the Emulator normally select ```NEmuS_Sound.java``` as the main class
- To launch the Emulator in Debug mode (Debug Window, and Sound disabled) select ```NEmuS_Debug.java``` as the main class

## Screenshots
<img src="img/debug.gif" width="100%" alt="Debug Window">

<img src="img/smb3.gif" width="50%" alt="Super Mario Bros 3"><img src="img/t&f.gif" width="50%" alt="Track & Field">

## Libraries
- **[LWJGL 3](https://www.lwjgl.org/)** Used to handle Rendering 
- **[Beads](http://www.beadsproject.net/)** Used to handle Audio

## Thanks
- **[OneLoneCoder](https://www.youtube.com/channel/UC-yuWVUplUJZvieEligKBkA)** [(Github)](https://github.com/OneLoneCoder) For his amazing video series about the NES and its inner workings
- **[NESDev Wiki](https://wiki.nesdev.com/w/index.php/Nesdev_Wiki)** For making available all of this information about the system in one place

## License

This project is licensed under the **[MIT license](http://opensource.org/licenses/mit-license.php)**
