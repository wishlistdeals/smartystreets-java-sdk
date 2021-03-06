#!/usr/bin/make -f

VERSION       := $(shell tagit -p --dry-run)
VERSION_FILE1 := pom.xml
VERSION_FILE2 := src/main/java/com/smartystreets/api/Version.java

clean:
	git checkout "$(VERSION_FILE1)" "$(VERSION_FILE2)"
	mvn clean

test:
	mvn test

compile:
	mvn compile

publish:
	sed -i -r "s/0\.0\.0/$(VERSION)/g" "$(VERSION_FILE1)" \
		&& sed -i -r "s/0\.0\.0/$(VERSION)/g" "$(VERSION_FILE2)" \
		&& GPG_TTY="$(shell tty)" mvn deploy \
		&& git checkout "$(VERSION_FILE1)" "$(VERSION_FILE2)"

##########################################################

workspace:
	docker-compose run sdk /bin/sh

release:
	docker-compose run sdk make publish \
		&& tagit -p \
		&& git push origin --tags \
		&& hub release create -m "v$(VERSION) Release" "$(VERSION)" \
			-a target/smartystreets-java-sdk-$(VERSION)-jar-with-dependencies.jar \
			-a target/smartystreets-java-sdk-$(VERSION)-javadoc.jar \
			-a target/smartystreets-java-sdk-$(VERSION).jar

.PHONY: clean test compile publish workspace release
