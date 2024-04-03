
.EXPORT_ALL_VARIABLES:
.PHONY: help docs build native install tests run update

COLOR_BLUE=\033[0;34m
COLOR_YELLOW=\033[0;33m
RESET_COLOR=\033[0m

MSG_INFO=[$(COLOR_BLUE)INFO$(RESET_COLOR)]

PROJECT_NAME=`xmllint --xpath "/project/artifactId/text()" pom.xml`

help:
	@echo "$(MSG_INFO) Available targets for $(PROJECT_NAME):"
	@echo "$(MSG_INFO)  - help\t\t\tThis help"
	@echo "$(MSG_INFO)  - docs\t\t\tGenerate project documentation and write to target/docs"
	@echo "$(MSG_INFO)  - build\t\t\tCompiles and packages the code into an executable jar"
	@echo "$(MSG_INFO)  - native\t\t\tBuilds a native image using Graal"
	@echo "$(MSG_INFO)  - install\t\t\tCopies the native image to /usr/local/bin"
	@echo "$(MSG_INFO)  - tests\t\t\tRuns a mvn clean test"
	@echo "$(MSG_INFO)  - run\t\t\tBuilds the code and runs it with sleeps enabled"
	@echo "$(MSG_INFO)  - update\t\tChecks for outdated libraries and known vulnerabilities"

# Install on debian with:
#     apt-get install pandoc
#     apt-get install texlive-latex-base
#     apt-get install weasyprint
docs:
	@mkdir -p target/docs
	@pandoc --pdf-engine=weasyprint -o target/docs/README.pdf README.md

# Builds the code
build:
	@echo "$(MSG_INFO) [$$(date '+%H:%M:%S')] Building"
	@mvn clean package -DskipTests -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugin.surefire.SurefirePlugin=warn

native: build
	@native-image --no-fallback -jar target/logfilter.jar -o target/logfilter
	@chmod +x target/logfilter

install: native
	@sudo mv target/logfilter /usr/local/bin

# Runs the tests
tests:
	@echo "$(MSG_INFO) [$$(date '+%H:%M:%S')] Running tests"
	@mvn clean test

# Builds first then runs the app in normal mode
run: build
	@echo "$(MSG_INFO) [$$(date '+%H:%M:%S')] Running"
	@cat src/data/stream.txt | java -jar target/logfilter.jar -c src/data/reorder.yml

# Checks for newer versions of dependencies or plugins
# Checks for known vulnerabilities in any dependencies
# Checks for forbidden licenses in any dependencies
update:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates
	-@mvn validate -P vulnerabilities
	-@mvn validate -P licenses
