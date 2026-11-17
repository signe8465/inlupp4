JUNIT = junit-platform-console-standalone-1.8.1.jar

.PHONY: all run clean tests

all: tests clean

run:
	java -jar $(JUNIT) -cp tests -c StandardTests

tests:
	javac -cp $(JUNIT) -d tests StandardTests.java
	java -jar $(JUNIT) -cp tests -c StandardTests

clean:
	rm -rf classes tests/*.class