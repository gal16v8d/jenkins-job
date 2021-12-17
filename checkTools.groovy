pipeline {
   agent any

   options {
        timeout(time: 5, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr:'3'))
    }

    stages {
    
        stage('Check Java') {
            steps {
                script {
                    def jdk8Home = tool 'Jdk8'
                    def jdk11Home = tool 'Jdk11'
                    def jdk17Home = tool 'Jdk17'
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
            }
        }
        
        stage('Check Mvn') {
            steps {
                script {
                    def mavenHome = tool 'Mvn-3'
                    if (isUnix()) {
                        sh "'${mavenHome}/bin/mvn' -v"
                    } else {
                        bat(/"${mavenHome}\bin\mvn" -v/)
                    }
                }
            }
        }
        
        stage('Check Gradle') {
            steps {
                script {
                    def gradleHome = tool 'Gradle-7'
                    if (isUnix()) {
                        sh "'${gradleHome}/bin/gradle' -v"
                    } else {
                        bat(/"${gradleHome}\bin\gradle" -v/)
                    }
                }
            }
        }
        
        stage('Check Git') {
            steps {
                script {
                    def gitHome = tool 'Git'
                    if (isUnix()) {
                        sh "'${gitHome}/bin/git' --version"
                    } else {
                        bat(/"${gitHome}\bin\git" --version/)
                    }
                }
            }
        }
        
        stage('Check Go') {
            steps {
                script {
                    def goHome = tool 'Go'
                    if (isUnix()) {
                        sh "'${goHome}/bin/go' version"
                    } else {
                        bat(/"${goHome}\bin\go" version/)
                    }
                }
            }
        }

        stage('Check NodeJS') {
            steps {
                script {
                    def nodeJSHome = tool 'NodeJs'
                    if (isUnix()) {
                        sh "'${nodeJSHome}/node' -v"
                    } else {
                        bat(/"${nodeJSHome}\node" -v/)
                    }
                }
            }
        }
        
        stage('Check Sonar Scanner') {
            steps {
                script {
                    def sonarScannerHome = tool 'SonarScanner'
                    if (isUnix()) {
                        sh "'${sonarScannerHome}/bin/sonar-scanner' -v"
                    } else {
                        bat(/"${sonarScannerHome}\bin\sonar-scanner" -v/)
                    }
                }
            }
        }
        
        /*
        stage('Check Docker') {
            steps {
                script {
                    def dockerHome = tool 'Docker'
                    if (isUnix()) {
                        sh "'${dockerHome}/docker' info"
                    } else {
                        bat(/"${dockerHome}\docker" info/)
                    }
                }
            }
        }*/
    }
}