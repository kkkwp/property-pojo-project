FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN find src -name "*.java" > sources.txt
RUN javac -d bin -cp "lib/*" @sources.txt
# docker run -e DB_USERNAME=your_user -e DB_PASSWORD=your_password <image_name>
CMD ["java", "-cp", "bin:resources:lib/*", "Main"]
