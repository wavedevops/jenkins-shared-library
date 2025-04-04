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

            post {
                always {
                    echo "Cleaning up workspace..."
                    deleteDir()
                    sh """
                    sh "echo Cleaning up ${env.WORKSPACE}"
                    sh "rm -rf ${env.WORKSPACE}"
                    """
                }
            }
        }
    }
}

//post {
//    always {
//        echo "Cleaning up workspace..."
//        deleteDir()
//        sh '''
//        cd /home/ec2-user/workspace/
//        rm -rf _app-ci-pipeline_${component}_${env.TAG_NAME}
//        '''
//    }
//}
//cd /home/ec2-user/workspace/
//        rm -rf _app-ci-pipeline_frontend_v1.0.0
