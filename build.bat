cd ezyplatform-devtools & ^
mvn clean install -Dmaven.test.skip=true & ^
cd .. & ^
cd ezyplatform-parent & ^
mvn clean install -Dmaven.test.skip=true & ^
cd .. & ^
cd ezyplatform-sdk & ^
mvn clean install -Dmaven.test.skip=true & ^
cd ..
