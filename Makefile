JUNIT = junit-platform-console-standalone-1.8.1.jar
MAIN = org.ioopm.calculator.Calculator
TXT = textfiles

.PHONY: all run clean tests systemtests compile

# Ändra här: Vi tar bort "clean" från slutet så att filerna finns kvar när den är klar
all: compile tests systemtests 

# Ett nytt steg som bara kompilerar huvudkoden
compile:
	mkdir -p classes
	javac -d classes $$(find src -name "*.java")

# Se till att run kompilerar först om det behövs, och sedan kör
run: compile
	java -ea -cp classes $(MAIN)

tests:
	javac -cp "$(JUNIT):src" -d tests StandardTests.java
	javac -cp "$(JUNIT):src" -d tests ParserTests.java
	java -jar $(JUNIT) -cp tests -c StandardTests
	java -jar $(JUNIT) -cp tests -c ParserTests

# Vi tar bort kompileringen härifrån eftersom "compile"-steget gör det nu
systemtests: compile
	java -cp classes $(MAIN) < $(TXT)/input1.txt > $(TXT)/actual1.txt 
	diff $(TXT)/output1.txt $(TXT)/actual1.txt
	java -cp classes $(MAIN) < $(TXT)/input2.txt > $(TXT)/actual2.txt 
	diff $(TXT)/output2.txt $(TXT)/actual2.txt
	@echo "Systemtester OK"

clean:
	rm -rf tests classes out build $(TXT)/actual1.txt $(TXT)/actual2.txt
	find . -name "*.class" -not -path "./.git/*" -delete
	mkdir -p tests