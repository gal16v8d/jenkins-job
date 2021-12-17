pipeline {
   agent any
   
   parameters {
        // choices are a string of newline separated values
		// https://issues.jenkins-ci.org/browse/JENKINS-41180
		
		// Unix
	    string(name: 'PROJECT_ROOT_DIR', defaultValue: "/home/alex/Documents/ws-sts/Bitbucket/biblioteca/")
		
		// Windows
		//string(name: 'PROJECT_ROOT_DIR', defaultValue: "D://Programacion//repos//Bitbucket//biblioteca")
		
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
        buildDiscarder(logRotator(numToKeepStr:'3'))
    }
    
    tools {
        jdk 'Jdk8'
        gradle 'Gradle-7'
    }

    stages {
       
	   stage('Checkout') {
	       steps {
	           script {
	               cleanWs()
    	           if (isUnix()) {
    	               sh "rsync -av --progress '${params.PROJECT_ROOT_DIR}' '${WORKSPACE}' --exclude .git --exclude .settings --exclude target --exclude node_modules"
    	           } else {
    	               bat(/(robocopy ${params.PROJECT_ROOT_DIR} . -e -xd .git .gradle .settings .scannerwork target node_modules -xf .classpath .project)^& IF %ERRORLEVEL% LSS 8 exit 0 /)
    	           }
	           }
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
			            script {
			                if (isUnix()) {
    			                sh "gradle clean"
    			                sh "gradle build"
    			            } else {
    			                bat(/gradle clean/)
			                    bat(/gradle build/)
    			            }
			            }
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
			            script {
			                if (isUnix()) {
    			                sh "gradle clean"
    			                sh "grad;e build -x test"
    			            } else {
    			                bat(/gradle clean/)
			                    bat(/gradle build -x test/)
    			            }
			            }
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
    	           if (isUnix()) {
    	               sh "'${scannerHome}'/bin/sonar-scanner"   
    	           } else {
    	               bat(/${scannerHome}\bin\sonar-scanner/)
    	           }
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