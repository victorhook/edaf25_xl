# Första redovisning, XL - Grupp 8

## 1. 
Vilka klasser bör finnas för att representera ett kalkylark?


## 2.
 En ruta i kalkylarket skall kunna innehålla en text eller ett uttryck. Hur modellerar man detta?
## 3.
 Hur kommer ni hantera fel i uttrycken i en eller flera celler?
## 4.
 Vilka klasser skall vara observatörer och vilka skall observeras?
## 5.
 Vilket paket och vilken klass skall hålla reda på vad som är ”Current slot”?
## 6.
 Vilken funktionalitet är redan färdig och hur fungerar den? Titta på klasserna i view-paketet och
## testkör.

## 7.
 Det kan inträffa ett antal olika fel när man försöker ändra innehållet i ett kalkylark. Då skall
## undantag 
kastas. Var skall dessa undantag fångas och hanteras?
## 8.
 Vilken klass används för att representera en adress i ett uttryck?
## 9.
 När ett uttryck som består av en adress skall beräknas används gränssnittet Environment. Vil-
## ken 
klass skall implementera gränssnittet? Varför använder man inte klassnamnet i stället för
## gränssnittet?

## 10.
 Om ett uttryck i kalkylarket refererar till sig själv, direkt eller indirekt, så kommer det att bli
## bekymmer 
vid beräkningen av uttryckets värde. Föreslå något sätt att upptäcka sådana cirkulära
## beroenden!
 Det finns en elegant lösning med hjälp av strategimönstret so