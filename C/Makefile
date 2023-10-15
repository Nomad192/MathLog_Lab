KOTLINC = kotlinc
KOTLIN = kotlin
SOURCES = $(shell find src -type f -name "*.kt")
NAME = app
OUT = out

all: out build

build:
	$(KOTLINC) $(SOURCES) -include-runtime -d $(OUT)/$(NAME).jar

run:
	java -jar $(OUT)/$(NAME).jar

pack:
	zip $(NAME).zip -r Makefile $(SOURCES) 

out:
	mkdir -p $(OUT)

clean:
	rm -rf $(OUT)
