# Bank Helper

A RuneLite plugin that colors your bank items based on their value. Each item gets an overlay on a blue → yellow → red gradient using whichever is higher — GE price or High Alch value. Colors are calculated on a log scale so cheap and expensive items both look distinct, not everything crammed into one end of the range.

<img width="468" height="549" alt="image" src="https://github.com/user-attachments/assets/420fb7ec-0602-42bc-b915-3f4a6e5960aa" />
<img width="268" height="666" alt="image" src="https://github.com/user-attachments/assets/39368dbb-8edf-442f-bb14-74e1d6a6e4c6" />

## Features

- Color gradient from blue (cheap) to red (valuable) based on GE/HA price
- Items below a threshold get labeled **JUNK**
- Untradeable items (quest items, uniques, etc.) get their own color and an asterisk so they don't blend in with the gradient
- All colors, opacity, and price thresholds are configurable

## Configuration

| Setting | Default | Description |
|---|---|---|
| Enabled | true | Toggle the overlay |
| Fill Opacity | 100 | How transparent the color overlay is (0–255) |
| Show Quest Marker | true | Show an asterisk on untradeable items |
| Low Color | Blue | Color at the bottom of the gradient |
| Mid Color | Yellow | Color at the middle of the gradient |
| High Color | Red | Color at the top of the gradient |
| Untradeable Color | Purple | Color for non-tradeable items |
| Junk Label Color | White | Color of the "JUNK" text |
| Quest Marker Color | White | Color of the asterisk marker |
| Junk Threshold | 100 | Stack value below which an item gets labeled JUNK |
| Low Price | 0 | Price that maps to the low color |
| Mid Price | 100k | Price that maps to the mid color |
| High Price | 10m | Price that maps to the high color |

Price fields accept shorthand like `100k`, `1m`, `1.5b`.

## Installation

### Sideloading

1. Build the JAR:
   ```bash
   ./gradlew build
   ```
2. Deploy it:
   ```bash
   ./gradlew deployPlugin
   ```
   This drops the JAR into `~/.runelite/sideloaded-plugins/`.
3. Enable developer mode in RuneLite and load the plugin.

### Plugin Hub

Search for **Bank Helper** in the RuneLite Plugin Hub and install from there.

## Building

Requires Java 11. Gradle wrapper is included.

```bash
./gradlew build
```

## License

[BSD 2-Clause](LICENSE) — Copyright 2026, nativvstudios
