# Beskrivning XL - Grupp 8

Vi har valt att hålla det så enkelt som möjligt med väldigt få klasser.
Anledningen till detta är för att vi började utan att göra en massa klasser,
kom fram till lösningarna fungerade, och såg därefter inte
någon anledning att skriva om koden bara för att göra fler klasser.

Dock bör det påpekas att om detta projektet skulle vidareutvecklats i framtiden
vore det bra att refaktorisera koden lite. I nuläget är det en del designprinciper
som bryts, såsom *SRP*.

Vi använder observatörsmönstret för att uppdatera vyn när modellen ändras.
Till detta använder vi gränssnitten `ModelObserver` och `ObservableModel`.

Det bör även påpekas att vi inte sparar beräknat värde någonstans, utan enbart
den råa strängen, vilket betyder att värdet behöver alltid räknas om på nytt
om någon ändring görs. Då prestanda inte var något krav, förbisåg vi detta, men
om prestanda skulle vara ett krav, så är detta en dåligt idé.
