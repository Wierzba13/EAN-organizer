# EAN Organizer

Aplikacja mobilna na system Android służąca do skanowania, katalogowania oraz szybkiego wyszukiwania produktów na podstawie kodów kreskowych **EAN**. Pozwala na przypisanie do każdego kodu EAN nazwy produktu, ceny (w PLN) oraz dodatkowych uwag, przechowując bazę lokalnie w pamięci urządzenia.

> **Brak weryfikacji kodów w zewnętrznych bazach (użytek lokalny):**
> Aplikacja nie waliduje wprowadzanych kodów EAN w żadnej bazie, a jako kod można podać dowolny ciąg znaków, dzięki czemu jest odpowiednia również na użytek lokalny.

---

## Główne funkcjonalności

* **Skanowanie kodów kreskowych aparatem** – szybkie odczytywanie kodów EAN za pomocą wbudowanego skanera (`ZXing`) z obsługą doświetlenia (latarki) przyciskiem głośności.
* **Ręczne wprowadzanie kodów EAN** – możliwość wpisania numeru EAN lub dowolnego własnego ciągu znaków bezpośrednio z klawiatury:
  * jeśli kod znajduje się już w bazie, automatycznie otwiera się okno podglądu/edycji produktu,
  * jeśli kodu jeszcze nie ma, otwiera się formularz dodawania nowego produktu z wstępnie wypełnionym kodem.
* **Dodawanie i edycja produktów** – zapisywanie nazwy, kodu EAN, ceny (`zł`) oraz opcjonalnych uwag; walidacja duplikatów EAN oraz wymaganych pól.
* **Wyszukiwanie w czasie rzeczywistym** – natychmiastowe filtrowanie listy zapisanych produktów zarówno po **nazwie**, jak i po **fragmencie kodu EAN**.
* **Dane przechowywane lokalnie** – automatyczny zapis w wewnętrznym katalogu aplikacji z pełnym wsparciem dla polskich znaków. Bez problemu działa `offline` oraz nie wysyła twoich zapisów na serwery.
