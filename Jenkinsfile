node {

  agent {
    docker { image 'maven:3.8.5-openjdk-17-slim' }
  }

  def dockerImageTag = "schoolmate-api-${env.BUILD_NUMBER}"

  try {

    notifyBuild('STARTED')

    stage('Clone repository') {
      git url: 'git@github.com:MouadFiali/SchoolMate.git',
        credentialsId: 'personal-cloning-key',
        branch: 'main'
    }

    stage('Run integration & unit tests') {
      sh "mvn clean test"
    }

    stage('Build Docker image') {
      sh "docker build -t schoolmate-api:${env.BUILD_NUMBER} ."
    }

    stage('Deploy Docker image') {
            echo "Docker Image Tag Name: ${dockerImageTag}"
            sh "docker stop schoolmate-api || true && docker rm schoolmate-api || true"
            sh "docker run --name schoolmate-api -d -p 8081:8080 schoolmate-api:${env.BUILD_NUMBER}"
    }

  } catch(e) {
      currentBuild.result = "FAILED"
      throw e
  } finally {
      notifyBuild(currentBuild.result)
  }
}


def notifyBuild(String buildStatus = 'STARTED'){
  
  buildStatus =  buildStatus ?: 'SUCCESSFUL'
  
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def now = new Date()
  
  def subject = "${buildStatus}, Job: ${env.JOB_NAME} - Deployment Sequence: [${env.BUILD_NUMBER}] "
  def summary = "${subject} - Check On: (${env.BUILD_URL}) - Time: ${now}"
  def subject_email = "SchoolMate API Deployment on Docker Hub"
  def details = """<p>${buildStatus} JOB </p>
    <p>Job: ${env.JOB_NAME} - Deployment Sequence: [${env.BUILD_NUMBER}] - Time: ${now}</p>
    <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME}</a>"</p>"""
  
  emailext (
     to: "mehdi.essalehi@gmail.com",
     subject: subject_email,
     body: details
  )
    
}