pipeline {
   agent any
   
   parameters {
        // choices are a string of newline separated values
		// https://issues.jenkins-ci.org/browse/JENKINS-41180
		string(name: 'PROJECT_ROOT_DIR', defaultValue: "D://Programacion//repos//Bitbucket//demojooby")
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Build',
			name: 'BUILD')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si se lanzan los test o no',
			name: 'TEST')
		choice(
			choices: 'true\nfalse',
			description: 'Indica si ejecuta el stage Scan',
			name: 'SCAN')
		/*
		choice(
			choices: 'true\nfalse',
			description: 'Indica si el artefacto generado sube a Artifactory',
			name: 'PUBLISH')
			*/
    }

    options {
        timeout(time: 5, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr:'5'))
    }
    
    tools {
        jdk 'Jdk8'
        gradle 'Gradle-6.5'
    }

    stages {
       
	   stage('Checkout') {
	       when {
			   expression { params.BUILD == 'true' }
		   }
	       steps {
	           cleanWs()
    	       bat(/(robocopy ${params.PROJECT_ROOT_DIR} . -e -xd .git .gradle .settings .scannerwork target -xf .classpath .project)^& IF %ERRORLEVEL% LSS 8 exit 0 /)
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
			        
			        //post {
                       // success {
                          //  junit '*target/surefire-reports/**/*.xml' 
                        //}
                    //}
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
    	           def scannerHome = tool 'SonarScanner'
    	           bat(/${scannerHome}\bin\sonar-scanner/)
	           }
	       }
	   }
	   
	   /*
	   stage('Publish') {
	        when {
	           expression { params.BUILD == 'true' &amp;&amp; params.PUBLISH == 'true' }
	       }
	       steps {
              script { 
                 def server = Artifactory.server 'Artifactory'
                 def uploadSpec = """{
                    "files": [{
                       "pattern": "./target/*.jar",
                       "target": "local-repo/"
                    }]
                 }"""
                 server.upload(uploadSpec) 
               }
            }
	   }
	   */
   }
}