JUNIT = junit-platform-console-standalone-1.8.1.jar
MAIN = org.ioopm.calculator.Calculator
TXT = textfiles

.PHONY: all run clean tests systemtests

all: tests systemtests clean

run:
	java -jar $(JUNIT) -cp tests -c StandardTests

tests:
	javac -cp "$(JUNIT):src" -d tests StandardTests.java
	javac -cp "$(JUNIT):src" -d tests ParserTests.java
	java -jar $(JUNIT) -cp tests -c StandardTests
	java -jar $(JUNIT) -cp tests -c ParserTests

systemtests:
	mkdir -p classes
	javac -d classes $$(find src -name "*.java")
	java -cp classes $(MAIN) < $(TXT)/input1.txt > $(TXT)/actual1.txt 
	diff $(TXT)/output1.txt $(TXT)/actual1.txt
	java -cp classes $(MAIN) < $(TXT)/input2.txt > $(TXT)/actual2.txt 
	diff $(TXT)/output2.txt $(TXT)/actual2.txt
	@echo "Systemtester OK"

clean:
	rm -rf tests classes out build $(TXT)/actual1.txt $(TXT)/actual2.txt
	find . -name "*.class" -not -path "./.git/*" -delete
	mkdir -p tests