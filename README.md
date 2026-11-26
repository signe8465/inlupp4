# Inlupp 4

Denna inlupp bygger på Inlupp3 där vi bygger en kalkylator. Den är uppbyggd i två stora delar, Parser och AST som redan i Inlupp3 bygdes upp men i denna inlupp finns även möjligheten att använda sig av scopes, conditional och funktioner. 

## MAKEFILE

Vår Makefil är AI genererad, följande komandon gå att utföra:

make compile => för att kompilera
make run => för att köra kalkylator och kompilera alla filer 
make tests => för att köra alla tester och kompilera alla filer 
make clean => för att ta bort alla klasser och tester som ses som "skärp"

## Diskussion:

Nedan diskuerar vi de val vi gjort under inluppen. 

### Designval 

Under 3.4, 3.5, 3.6 så började vi skifta från de instruktioner som vi fick från inluppen. Vi valde att istället för göra check klasser, skriva in dem i Listor som vi skapade samt Evaluationvisitor. 

#### Scope

I Scope har vi implementerat Stacks som är gjord av Scopes, där varje Scope har en pekare, en variabellista samt ett eget environment. I den variabelslistan har vi inbyggd dubbelassignmentcheck vilket också innebär att i function kan man inte assigna till samma variabel flera gånger i samma Scope. Vi har satt Scope till en Unary i Parser och i SymbolicExpression-trädet. 

#### Conditional 

Vi skapade subklassen Operator till Conditonals som extendar Binary. Operator och Conditional har sina egna metoder i Parser. När vi tar reda på vilken operator vi använder så jämför vi tecken för tecken istället för Strängar då de är olika långa vilket gjorde det enklare för oss. När operator evalueras så retunerar de konstanter (1/0) istället för sant/falskt. 

#### Functions 

Vi har tre objekt, "FunctionDeclaration", "Function" och "FunctionStart" där Function är helt gömd för användaren och som en behållare för data i hashtabellen. FunktionDeclaration sätter parameterlistan och bygger under körningen upp själva funktionen med alla SymbolicExpressions som kommer efteråt. Den sätter också funktionen namn till key som lagras som en variabelnyckel i hashtabellen, så när man evaluerar en variabel och hämtar ut en funktion istllet för en konstant i hashtabellen så kommer denna att evalueras till en funktion och köras. Detta gör att vi undviker namnkonflikter och vi slipper separat logik i Parser för funktionsobjekt. 

FunktionStart anropas med funktionsnamnet som castas till en variabel och används som nyckel för att hämta ut funktionsdata och variabelnamn i hashtabellen. Variabelnamnen sätts nu mot konstanterna som funktionsdatan tar in och körs. Sen exekveras alla funktionsrader i en loop och returnerar sista returvärdet. 

## Kommentar:

Vi har implementerat Stack och Listor själva med egen logik 





