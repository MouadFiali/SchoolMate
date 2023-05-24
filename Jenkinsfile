node {

  agent {
    docker { image 'maven:3.8.5-openjdk-17-slim' }
  }

  def dockerImageTag = "messalehi/schoolmate-api:${env.BUILD_NUMBER}"

  try {

    notifyBuild('STARTED')

    stage('Clone repository') {
      git url: 'git@github.com:MouadFiali/SchoolMate.git',
        credentialsId: 'personal-cloning-key',
        branch: 'main'
    }

    stage('Run integration & unit tests') {
      withCredentials([
        string(credentialsId: 'schoolmate-test-database-url', variable: 'TEST_DATABASE_URL'),
        string(credentialsId: 'schoolmate-test-database-username', variable: 'TEST_DATABASE_USERNAME'),
        string(credentialsId: 'schoolmate-test-database-password', variable: 'TEST_DATABASE_PASSWORD')
      ]) {        
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="DocumentsControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="ComplaintsControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="SchoolZoneControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="UserControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="AlertsControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="MappersTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="ComplaintMappersTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="PlaceSuggestionsControllerTest"')
        sh('./mvnw test -Dspring.profiles.active=prod -Dspring.datasource.url=$TEST_DATABASE_URL -Dspring.datasource.username=$TEST_DATABASE_USERNAME -Dspring.datasource.password=$TEST_DATABASE_PASSWORD -Dtest="PlaceSuggestionsMapperTest"')
      }
    }

    stage('Build Docker image') {
      sh "docker build -t ${dockerImageTag} ."
    }

    stage('Deploy Docker image') {
      echo "Docker Image Tag Name: ${dockerImageTag}"
      echo "Logging in to Docker Hub..."
      
      withCredentials([
        usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')
      ]) {        
        sh('docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD')
      }  
      
      echo "Pushing image..."
      sh "docker push ${dockerImageTag}"
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