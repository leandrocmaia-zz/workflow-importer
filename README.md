# workflow-importer

Application that imports workflow sources and prints a summary.

## Test

./gradlew clean test

## Run

./gradlew clean build; java -jar build/libs/workflow-importer-1.0.0.jar

## Thoughts

- Used spring boot to bootstrap so it is easier to transition to an webabb/microservice/etc.
- It's not clear the use cases for invalid input from sources. 
- Unreliable source isn't ideal in a real production application. Depending on the data sensivity, the validation step
should be done before any import is made, or the import should be denied at all.
- The design depended on the traffic. If very high, should be in parallel. If files are too big (1TB+), a reactive design
should be favored.