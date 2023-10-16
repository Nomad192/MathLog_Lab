KOTLINC = kotlinc
KOTLIN = kotlin
SOURCES_FOLDER = src
SOURCES = $(shell find $(SOURCES_FOLDER) -type f -name "*.kt")
MAINCLASS = Main
MAINCLASS_REAL_NAME = "$(MAINCLASS)Kt"
NAME = app
OUT = out

all: out build

build: build-kotlin

run: run-kotlin

pack:
	zip $(NAME).zip -r Makefile $(SOURCES) 

build-kotlin:
	${KOTLINC} -cp $(SOURCES_FOLDER) -d $(OUT) $(SOURCES)

run-kotlin:
	${KOTLIN} -cp $(OUT) ${MAINCLASS_REAL_NAME}

build-jar:
	$(KOTLINC) $(SOURCES) -include-runtime -d $(OUT)/$(NAME).jar

run-jar:
	$(KOTLIN) $(OUT)/$(NAME).jar

out:
	mkdir -p $(OUT)

clean:
	rm -rf $(OUT)
