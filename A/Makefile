KOTLINC = kotlinc
KOTLIN = kotlin
SOURCES = $(shell find . -type f -name "*.kt")
NAME = A

all: out build

build:
	$(KOTLINC) $(SOURCES) -include-runtime -d out/app.jar

run:
	java -jar out/app.jar

pack:
	zip $(NAME).zip -r Makefile $(SOURCES) 

out:
	mkdir -p out

clean:
	rm -rf out
