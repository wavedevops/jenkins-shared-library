def call() {
    pipeline {
        agent { label 'workstation' }

        //environment {
            //component = "cart"
        //}

        stages {

            stage('Code Quality') {
                when {
                    allOf {
                        expression { env.TAG_NAME != env.GIT_BRANCH }
                    }
                }
                steps {
                    echo 'OK'
                }
            }

            stage('Unit Tests') {
                when {
                    allOf {
                        expression { env.TAG_NAME != env.GIT_BRANCH }
                        branch 'main'
                    }
                }
                steps {
                    echo 'CI'
                }
            }

            stage('Release') {
                when {
                    expression { env.TAG_NAME ==~ ".*" }
                }
                steps {
                    sh 'zip -r ${component}-${TAG_NAME}.zip *'
                    sh """
                curl -sSf -u admin:@123Chaitu -X PUT -T ${component}-${TAG_NAME}.zip "https://jfrog.chaitu.net/artifactory/${component}/${component}-${TAG_NAME}.zip"
                """
                }
            }
        }

        post {
            always {
                echo "Cleaning up workspace..."
                deleteDir()
            }

            success {
                script {
                    if (env.TAG_NAME ==~ ".*") {
                        sh 'rm -rf /home/ec2-user/*'
                    }
                }
            }
        }
    }
}