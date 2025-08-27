# 부동산 매물 플랫폼

## 💡 실행방법

### 1. git clone

```shell
git clone https://github.com/stigma-property/java-project.git
cd 프로젝트 경로
```

### 2. PowerShell (관리자로 실행)

```shell
# unicode 지원 활성화
chcp 65001
# 컴파일에 필요한 모든 java 파일 경로 저장
Get-ChildItem -Path src -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } > sources.txt
# 경로에 있는 내용 바탕으로 컴파일
javac -encoding UTF-8 -d bin (Get-Content sources.txt)
# 실행
java -cp bin .\src\Main.java
```
