def version, mvnCmd = "mvn"
      pipeline
      {
       agent any
        tools
        {
            maven 'M3'
        }

        stages
        {
          stage('Build de CARRENT')
          {
            steps
             {
              git branch: 'master', url: 'https://github.com/act-sarl/carrent.git'
              script {
                  def pom = readMavenPom file: 'pom.xml'
                  version = pom.version
              }
              sh "${mvnCmd} clean install -DskipTests=true"
            }
          }
        /*
          stage('Test')
          {
            steps
            {
              sh "${mvnCmd} test -Dspring.profiles.active=test"
            }
          }  */
          stage('Package')
          {
            steps
            {
              echo "-=- packaging du project -=-"
              sh "${mvnCmd} clean package"
            }
          }
          stage('Exécution')
          {
            steps
            {
              echo "-=- Exécution de CARRENT en mode preProd -=-"
              sh "java -jar target/carrent.war --spring.profiles.active=preProd"
            }
          }
          /*
          stage('Code Analysis')
          {
            steps
             {
              script
              {
                      sh "mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000"
              }
            }
          }*/
          /*
          stage('Archive App') {
            steps {
              sh "${mvnCmd} deploy -DskipTests=true -P nexus3"
            }
          }*/


        }
      }