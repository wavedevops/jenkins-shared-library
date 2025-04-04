def call() {
    //def component = "cart"
    pipeline {
        agent { label 'workstation' }

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
                    sh "zip -r ${component}-${env.TAG_NAME}.zip *"
                    sh """
                    curl -sSf -u admin:@123Chaitu -X PUT -T ${component}-${env.TAG_NAME}.zip "https://jfrog.chaitu.net/artifactory/${component}/${component}-${env.TAG_NAME}.zip"
                    """
                }
            }
        }

        post {
            always {
                echo "Cleaning up workspace..."
                deleteDir()
            }
        }
    }
}
