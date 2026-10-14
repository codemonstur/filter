
.EXPORT_ALL_VARIABLES:
.PHONY: help clean docs build test native install tests run validate release-notes

NAME=`xmllint --xpath "/project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

# Blue [INFO] prefix before every message, NO_COLOR=1 prints it plain
ifneq ($(NO_COLOR),)
INFO = printf '[INFO] %s\n'
else
INFO = printf '[\033[1;34mINFO\033[m] %s\n'
endif

help:
	@$(INFO) "Available targets for $(NAME):"
	@$(INFO) "  - help		This help"
	@$(INFO) "  - clean		Delete everything in ./target"
	@$(INFO) "  - docs		Generate project documentation and write to target/docs"
	@$(INFO) "  - build	Compiles and packages the code into an executable jar"
	@$(INFO) "  - test	Runs the tests"
	@$(INFO) "  - native	Builds a native image using Graal"
	@$(INFO) "  - install	Copies the native image to /usr/local/bin"
	@$(INFO) "  - tests	Runs a mvn clean test"
	@$(INFO) "  - run		Builds the code and runs it with sleeps enabled"
	@$(INFO) "  - validate	Check dependency versions, cves, and licenses"
	@$(INFO) "  - release-notes	Create release notes for the latest version"

clean:
	@$(INFO) "Cleaning"
	@mvn clean

# Install on debian with:
#     apt-get install pandoc
#     apt-get install texlive-latex-base
#     apt-get install weasyprint
docs:
	@mkdir -p target/docs
	@pandoc --pdf-engine=weasyprint -o target/docs/README.pdf README.md

# Builds the code
build:
	@$(INFO) "[$$(date '+%H:%M:%S')] Building"
	@mvn -DskipTests clean package

test:
	@mvn test

native: build
	@native-image --no-fallback -jar target/filter.jar -o target/filter
	@chmod +x target/filter

install: native
	@sudo mv target/filter /usr/local/bin

# Runs the tests
tests:
	@mvn clean test

# Builds first then runs the app in normal mode
run: build
	@$(INFO) "[$$(date '+%H:%M:%S')] Running"
	@cat src/data/stream.txt | java -jar target/filter.jar -c src/data/reorder.yml

# Checks for newer versions of dependencies or plugins
# Checks for known vulnerabilities in any dependencies
# Checks for forbidden licenses in any dependencies
validate:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates
	@mvn validate -P vulnerabilities
	@mvn validate -P licenses

release-notes:
	@$(INFO) "Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@mkdir -p src/docs/releases
	@echo "$(VERSION)" >> src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt
