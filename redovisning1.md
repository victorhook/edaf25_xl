# Första redovisning, XL - Grupp 8

## Två användningsfall
1. När man skriver in ett värde i en cell, skall värdet synas i cellen. Om man skriver något värde som är ogiltligt, skall det synas ett felmeddelande i cellen. Meddelandet skall tala om för användaren **vad** som är fel.
2. Man kan ta bort innehåller i en cell genom att trycka på  menyn "Edit" och alternativet "Clear". Cellen töms utan vidare konfirmation. Att tömma en tom cell har ingen effekt.

## 1. 
*Vilka klasser bör finnas för att representera ett kalkylark?*
- `XLModel` representerar ett kalkyl.

## 2.
*En ruta i kalkylarket skall kunna innehålla en text eller ett uttryck. Hur modellerar man detta?*
- Man använder gränssnittet `Expr`

## 3.
*Hur kommer ni hantera fel i uttrycken i en eller flera celler?*
- Vi använder `ErrorResult` och skriver felmeddelanden i cellerna som innehåller felen.

## 4.
*Vilka klasser skall vara observatörer och vilka skall observeras?*
- `XLModel` behöver *observeras*.
- `GridCell` observererar med hjälp av gränssnittet

## 5.
*Vilket paket och vilken klass skall hålla reda på vad som är ”Current slot”?*
-Paket `gui` -> Klassen `XL`

## 6.
*Vilken funktionalitet är redan färdig och hur fungerar den? Titta på klasserna i view-paketet och testkör.*
- All frontend är klar. Behöver bara implementera logiken.

## 7.
*Det kan inträffa ett antal olika fel när man försöker ändra innehållet i ett kalkylark. Då skall undantag kastas. Var skall dessa undantag fångas och hanteras?*
- `XLModel` fångar felen.

## 8.
*Vilken klass används för att representera en adress i ett uttryck?*
- `CellAddress`

## 9.
*När ett uttryck som består av en adress skall beräknas används gränssnittet Environment. Vilken klass skall implementera gränssnittet? Varför använder man inte klassnamnet i stället för gränssnittet?*
-

## 10.
 Om ett uttryck i kalkylarket refererar till sig själv, direkt eller indirekt, så kommer det att bli
## bekymmer 
vid beräkningen av uttryckets värde. Föreslå något sätt att upptäcka sådana cirkulära
## beroenden!
 Det finns en elegant lösning med hjälp av strategimönstret so
