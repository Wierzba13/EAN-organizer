# EAN Organizer

Aplikacja mobilna na system Android służąca do skanowania, katalogowania oraz szybkiego wyszukiwania produktów na podstawie kodów kreskowych **EAN**. Pozwala na przypisanie do każdego kodu EAN nazwy produktu, ceny (w PLN) oraz dodatkowych uwag, przechowując bazę lokalnie w pamięci urządzenia.

---

## Główne funkcjonalności

* **Skanowanie kodów kreskowych aparatem** – szybkie odczytywanie kodów EAN za pomocą wbudowanego skanera (`ZXing`) z obsługą doświetlenia (latarki) przyciskiem głośności.
* **Ręczne wprowadzanie kodów EAN** – możliwość wpisania numeru EAN bezpośrednio z klawiatury numerycznej:
  * jeśli kod znajduje się już w bazie, automatycznie otwiera się okno podglądu/edycji produktu,
  * jeśli kodu jeszcze nie ma, otwiera się formularz dodawania nowego produktu z wstępnie wypełnionym numerem EAN.
* **Dodawanie i edycja produktów** – zapisywanie nazwy, kodu EAN, ceny (`zł`) oraz opcjonalnych uwag; walidacja duplikatów EAN oraz wymaganych pól.
* **Wyszukiwanie w czasie rzeczywistym** – natychmiastowe filtrowanie listy zapisanych produktów zarówno po **nazwie**, jak i po **fragmencie kodu EAN**.
* **Bezpieczne usuwanie** – okno dialogowe z potwierdzeniem przed usunięciem pozycji z listy.
* **Lokalna baza danych JSON (`UTF-8`)** – automatyczny zapis w wewnętrznym katalogu aplikacji (`codes.json`) z pełnym wsparciem dla polskich znaków.

---

## Technologie i wymagania

* **Język:** Java 17
* **System budowania:** Gradle `9.3.1` + Android Gradle Plugin `9.1.0` (kompatybilny z JDK / JBR 25)
* **Android SDK:**
  * `minSdk`: 24 (Android 7.0)
  * `targetSdk`: 36
  * `compileSdk`: 37
* **Biblioteki:**
  * AndroidX (`AppCompat 1.8.0`, `RecyclerView 1.4.0`, `ConstraintLayout 2.2.2`)
  * Material Components for Android (`1.14.0`)
  * ZXing Android Embedded (`4.3.0`)

---

## Uruchomienie projektu

1. Sklonuj repozytorium:
   ```bash
   git clone <adres-repozytorium>
   cd EAN-organizer
   ```
2. Otwórz projekt w **Android Studio**.
3. Poczekaj na zakończenie synchronizacji Gradle (**Gradle Sync**).
4. Uruchom aplikację na emulatorze lub fizycznym urządzeniu z systemem Android (`Shift + F10` lub przycisk **Run**).

### Budowanie z wiersza poleceń

Aby zbudować plik APK w wersji debug:

```bash
./gradlew assembleDebug
```

Aby od razu zainstalować aplikację na podłączonym urządzeniu lub emulatorze:

```bash
./gradlew installDebug
```

---

## Struktura projektu

```text
app/src/main/
├── java/com/example/ean_code_scanner/
│   ├── MainActivity.java       # Główny ekran aplikacji, obsługa skanera, wyszukiwania i okien dialogowych
│   ├── JsonController.java     # Obsługa zapisu/odczytu bazy produktów w pliku codes.json (UTF-8)
│   ├── RecyclerAdapter.java    # Adapter listy produktów z obsługą edycji i potwierdzenia usunięcia
│   ├── Product.java            # Model danych pojedynczego produktu
│   └── CaptureAct.java         # Aktywność skanera kodów kreskowych (ZXing)
└── res/
    ├── layout/
    │   ├── activity_main.xml   # Główny widok z kartą wyszukiwania/skanowania i listą RecyclerView
    │   ├── list_item.xml       # Karta pojedynczego produktu na liście
    │   └── add_product.xml     # Formularz dodawania / edycji produktu
    ├── drawable/               # Ikony wektorowe oraz tła elementów UI
    └── values/                 # Kolory, style i zasoby tekstowe
```
