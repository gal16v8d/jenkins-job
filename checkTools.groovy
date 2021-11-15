node {
    def jdk8Home
    def jdk11Home
    def jdk17Home
    def mavenHome
    def gradleHome
    def gitHome
    def goHome
    def nodeJSHome
    def sonarScannerHome
    def dockerHome
    
    stage('Preparation') {
        jdk8Home = tool 'Jdk8'
        jdk11Home = tool 'Jdk11'
        jdk17Home = tool 'Jdk17'
        mavenHome = tool 'Mvn-3'
        gradleHome = tool 'Gradle-7'
        gitHome = tool 'Git'
        goHome = tool 'Go'
        nodeJSHome = tool 'NodeJs'
        sonarScannerHome = tool 'SonarScanner'
        dockerHome = tool 'Docker'
    }
    
    stage('Check Java') {
        if (isUnix()) {
            sh "'${jdk8Home}/bin/java' -version"
            sh "'${jdk11Home}/bin/java' -version"
            sh "'${jdk17Home}/bin/java' -version"
        } else {
            bat(/"${jdk8Home}\bin\java" -version/)
            bat(/"${jdk11Home}\bin\java" -version/)
            bat(/"${jdk17Home}\bin\java" -version/)
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
    
    stage('Check Go') {
        if (isUnix()) {
            sh "'${goHome}/bin/go' version"
        } else {
            bat(/"${goHome}\bin\go" version/)
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
    
    /*
    stage('Check Docker') {
      // Run the maven build
      if (isUnix()) {
         sh "'${dockerHome}/docker' info"
      } else {
         bat(/"${dockerHome}\docker" info/)
      }
   }*/
    
}