node {
    def jdk8Home
    def jdk11Home
    def jdk13Home
    def mavenHome
    def gradleHome
    def gitHome
    def nodeJSHome
    def sonarScannerHome
    
    stage('Preparation') {
        jdk8Home = tool 'Jdk8'
        jdk11Home = tool 'Jdk11'
        jdk13Home = tool 'Jdk13'
        mavenHome = tool 'Mvn-3.6.3'
        gradleHome = tool 'Gradle-6.5'
        gitHome = tool 'Git'
        nodeJSHome = tool 'NodeJs'
        sonarScannerHome = tool 'SonarScanner'
    }
    
    stage('Check Java') {
        if (isUnix()) {
            sh "'${jdk8Home}/bin/java' -version"
            sh "'${jdk11Home}/bin/java' -version"
            sh "'${jdk13Home}/bin/java' -version"
        } else {
            bat(/"${jdk8Home}\bin\java" -version/)
            bat(/"${jdk11Home}\bin\java" -version/)
            bat(/"${jdk13Home}\bin\java" -version/)
        }
    }
    
    stage('Check Mvn') {
        if (isUnix()) {
            sh "'${mavenHome}/bin/mvn' -v"
        } else {
            bat(/"${mavenHome}\bin\mvn" -v/)
        }
    }
    
    stage('Check Gradle') {
        if (isUnix()) {
            sh "'${gradleHome}/bin/gradle' -v"
        } else {
            bat(/"${gradleHome}\bin\gradle" -v/)
        }
    }
    
    stage('Check Git') {
        if (isUnix()) {
            sh "'${gitHome}/bin/git' --version"
        } else {
            bat(/"${gitHome}\bin\git" --version/)
        }
    }

    stage('Check NodeJS') {
        if (isUnix()) {
            sh "'${nodeJSHome}/node' -v"
        } else {
            bat(/"${nodeJSHome}\node" -v/)
        }
    }
    
    stage('Check Sonar Scanner') {
        if (isUnix()) {
            sh "'${sonarScannerHome}/bin/sonar-scanner' -v"
        } else {
            bat(/"${sonarScannerHome}\bin\sonar-scanner" -v/)
        }
    }
    
}