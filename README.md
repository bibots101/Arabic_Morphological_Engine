# Arabic Morphological Search Engine

Moteur de Recherche Morphologique et Générateur de Dérivation Arabe Avec Interface Pédagogique

An educational tool for learning Arabic morphology through interactive generation and validation of word derivations from trilateral and quadrilateral roots.

## Overview

This project implements a morphological engine for Arabic language learning, supporting both trilateral (3-letter) and quadrilateral (4-letter) root systems. The system combines algorithmic efficiency with pedagogical design using optimized data structures (AVL Tree + HashMap).

### Supported Root Types

| Type | Letters | Example Root | Example Pattern | Example Word |
|------|---------|--------------|-----------------|--------------|
| Trilateral | 3 | كتب | فَاعِل | كَاتِب |
| Quadrilateral | 4 | دحرج | فَعْلَلَ | دَحْرَجَ |

## Features

**Teacher Mode:**
- Add/remove trilateral and quadrilateral roots
- Manage morphological patterns with automatic type detection
- Configure difficulty levels (beginner, intermediate, advanced)
- View student statistics

**Student Mode:**
- Progressive learning across three difficulty levels
- Interactive exercises (generation and validation)
- Configurable quizzes (5-20 questions)
- Session statistics and progress tracking

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- UTF-8 encoding support

### Setup

1. Clone or download the repository
2. Verify project structure:
   ```
   src/main/
   ├── data/Pro/
   │   ├── patterns.txt
   │   └── roots.txt
   └── java/org/morpho/ui/
       ├── Main.java              # Console version
       ├── MainWithGUI.java       # GUI version
       └── ...
   ```

3. Compile the project:
   ```bash
   javac -d bin -encoding UTF-8 src/main/java/org/morpho/ui/*.java
   ```

## Usage

### Console Mode
```bash
cd bin
java -Dfile.encoding=UTF-8 org.morpho.ui.Main
```

### GUI Mode (Recommended)
```bash
cd bin
java -Dfile.encoding=UTF-8 org.morpho.ui.MainWithGUI
```

## Key Algorithms

**Automatic Root Type Detection:**
Analyzes pattern template to determine if trilateral (3-letter) or quadrilateral (4-letter) root is required by counting occurrences of the ل symbol.

**Morphological Generation:**
Maps root radicals (ف-ع-ل) to pattern template positions, handling both 3-letter and 4-letter roots with a unified algorithm.

**Validation:**
Reverses generation by extracting radicals from derived word and comparing with source root across all possible patterns.

## Development Team

Institut Supérieur d'Informatique, Université de Tunis El Manar  
Academic Year 2025-2026

- Wassim LOURIMI
- Arij BELMABROUK
- Yasmine HSAIRI

## License

Academic project developed at Institut Supérieur d'Informatique. Freely available for educational and research purposes.

## Troubleshooting

**Characters display incorrectly:**
Ensure UTF-8 encoding: `java -Dfile.encoding=UTF-8 org.morpho.ui.MainWithGUI`

**Data files not found:**
Verify files are in `src/main/data/Pro/` directory

**Pattern generation fails:**
Check that pattern templates use correct Arabic symbols (ف, ع, ل)