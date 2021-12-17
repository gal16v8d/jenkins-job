pipeline {
   agent any
   
   parameters {
        // choices are a string of newline separated values
		// https://issues.jenkins-ci.org/browse/JENKINS-41180
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Build',
			name: 'BUILD')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si se lanzan los test unitarios o no',
			name: 'TEST')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Scan',
			name: 'SCAN')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si el artefacto generado sube a Artifactory',
			name: 'PUBLISH')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Run',
			name: 'RUN')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Run Docker o Run Jar (depende de la bandera RUN)',
			name: 'RUN_DOCKER')
    }
    
    environment {
        wsDir = pwd()
        dockerImage = "test/demo:latest"
        dockerContainer = "demo"
        DOCKER_HOME = tool name: 'DockerTool', type: 'org.jenkinsci.plugins.docker.commons.tools.DockerTool'
    }
    
    options {
        timeout(time: 5, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr:'3'))
    }
    
    tools {
        gradle 'Gradle-7'
    }

    stages {
	   stage('Checkout') {
	       when {
			   expression { params.BUILD == 'true' }
		   }
	       steps {
	           cleanWs()
    	       bat(/(robocopy C:\Portable_Program\WS_STS\demo\ . -e -xd .gradle .settings bin build -xf .classpath .project)^& IF %ERRORLEVEL% LSS 8 exit 0 /)
	       }
	   }
	   
	   stage('Build') {
	      when {
			   expression { params.BUILD == 'true' }
		  }
		  failFast true
		  parallel {
			    stage('Build & Test') {
			        when {
			            expression { params.TEST == 'true' }
			        }
			        steps {
			            bat(/gradle clean/)
			             bat(/gradle build/)
			        }
			    }
			    stage('Build Only') {
			        when {
			            expression { params.TEST == 'false' }
			        }
			        steps {
			            bat(/gradle clean/)
			         bat(/gradle build -x test/)
			        }
			    }
		   }
	   }
	   
	   stage('Scan') {
	       when {
	           expression { params.SCAN == 'true' }
	       }
	       steps {
	           script {
    	           def scannerHome = tool 'scanLocal'
    	           withSonarQubeEnv('Sonar-Local') {
    	            bat(/${scannerHome}\bin\sonar-scanner -Dsonar.projectKey=demo -Dsonar.projectName=demo -Dsonar.projectVersion=1.0 -Dsonar.sourceEncoding=UTF-8 -Dsonar.language=java -Dsonar.java.source=1.8 -Dsonar.sources=.\src\main\java -Dsonar.java.binaries=.\build\classes\java\main -Dsonar.tests=.\src\test\java -Dsonar.java.test.binaries=.\build\classes\java\test -Dsonar.junit.reportPaths=.\build\test-results\test -Dsonar.jacoco.reportPaths=.\build\jacoco\test.exec /)
    	           }
	           }
	       }
	   }
	   
	   stage('Publish') {
	        when {
	           expression { params.BUILD == 'true' && params.PUBLISH == 'true' }
	       }
	       steps {
              script { 
                 def server = Artifactory.server 'LocalArt'
                 def uploadSpec = """{
                    "files": [{
                       "pattern": "./build/libs/demo.jar",
                       "target": "local-repo/"
                    }]
                 }"""
                 server.upload(uploadSpec) 
               }
            }
	   }
	   
	   stage('Run') {
		    when {
				expression { params.RUN == 'true' }
			}
			failFast true
			parallel {
			        stage('Run Container') {
			            when {
			                expression { params.RUN_DOCKER == 'true' }
			            }
			            steps {
			                script {
    			                //bat(/"${DOCKER_HOME}\docker" stop ${dockerContainer}/)
    			                //bat(/"${DOCKER_HOME}\docker" rm ${dockerContainer}/)
    			                //bat(/"${DOCKER_HOME}\docker" rmi ${dockerImage} -f/)
    			                //bat(/gradle startDockerContainer/)
    			                bat(/gradle buildDockerImage/)
    			                bat(/kubectl get nodes/)
    			                bat(/kubectl run demo --image=${dockerImage} --port=8090 imagePullPolicy=Never/)
    			                bat(/kubectl expose deployment hello-minikube --type=NodePort/)
			                }
			            }
			        }
			        stage('Run Jar') {
			            when {
			                expression { params.RUN_DOCKER == 'false' }
			            }
			            steps {
			                bat(/start java -jar "${wsDir}\build\libs\demo-0.0.1-SNAPSHOT.jar"/)
			            }
			         }
			}
	   }
   }
}