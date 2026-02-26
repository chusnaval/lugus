pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    skipDefaultCheckout(true)
  }

  parameters {
    string(name: 'GIT_URL', defaultValue: 'https://github.com/chusnaval/lugus', description: 'URL del repositorio Git (https://... o ssh://...)')
    string(name: 'GIT_BRANCH', defaultValue: 'master', description: 'Rama a descargar')
    string(name: 'GIT_CREDENTIALS_ID', defaultValue: '', description: 'Credentials ID para Git (opcional si repo público)')
    string(name: 'TOMCAT_BASE_URL', defaultValue: '', description: 'Base URL de Tomcat. Ejemplo: http://servidor:8080')
    string(name: 'TOMCAT_CONTEXT_PATH', defaultValue: '/lugus', description: 'Context path del despliegue. Ejemplo: /lugus')
  }

  environment {
    POM_FILE = 'lugus\\pom.xml'
    WAR_PATH = 'lugus\\lugus-bootstrap\\target\\lugus.war'
    TOMCAT_MANAGER_CREDENTIALS_ID = 'tomcat-manager-creds'
    PROD_APP_PROPS_CREDENTIALS_ID = 'lugus-prod-properties'
  }

  stages {
    stage('Checkout') {
      steps {
        script {
          if (params.GIT_URL?.trim() == '') {
            error('Debes informar el parámetro GIT_URL en Build with Parameters')
          }
        }
        deleteDir()
        script {
          if (params.GIT_CREDENTIALS_ID?.trim()) {
            git branch: params.GIT_BRANCH, credentialsId: params.GIT_CREDENTIALS_ID, url: params.GIT_URL
          } else {
            git branch: params.GIT_BRANCH, url: params.GIT_URL
          }
        }
      }
    }

    stage('Check Tools') {
      steps {
        bat "if not exist \"${env.WORKSPACE}\\${env.POM_FILE}\" (echo No existe pom en ${env.WORKSPACE} & exit /b 1)"
        bat 'java -version'
        bat 'mvn -version'
      }
    }

    stage('Build') {
      steps {
        bat "mvn -B -ntp -f \"${env.WORKSPACE}\\${env.POM_FILE}\" clean install -DskipTests -Denv=prod -Pprod"
      }
    }

    stage('Deploy to Tomcat') {
      steps {
        withCredentials([
          usernamePassword(
            credentialsId: "${env.TOMCAT_MANAGER_CREDENTIALS_ID}",
            usernameVariable: 'TOMCAT_USER',
            passwordVariable: 'TOMCAT_PASS'
          ),
          file(
            credentialsId: "${env.PROD_APP_PROPS_CREDENTIALS_ID}",
            variable: 'PROD_PROPS_FILE'
          )
        ]) {
          powershell '''
              $warPath = Join-Path $env:WORKSPACE "$($env:WAR_PATH)"
              if (-not (Test-Path $warPath)) {
                Write-Error "WAR no encontrado en $warPath"
                exit 1
              }

              if (-not (Test-Path $env:PROD_PROPS_FILE)) {
                Write-Error "No se encontró el Secret file de prod en Jenkins"
                exit 1
              }

              if ([string]::IsNullOrWhiteSpace($env:TOMCAT_BASE_URL)) {
                Write-Error "TOMCAT_BASE_URL no está definido en el job/entorno Jenkins"
                exit 1
              }

              if ([string]::IsNullOrWhiteSpace($env:TOMCAT_CONTEXT_PATH)) {
                Write-Error "TOMCAT_CONTEXT_PATH no está definido en el job/entorno Jenkins"
                exit 1
              }

              $prodPropsText = Get-Content -Path $env:PROD_PROPS_FILE -Raw
              $hasDbPassword = (
                ($prodPropsText -match '(?m)^\\s*spring\\.datasource\\.password\\s*=\\s*.+$') -or
                ($prodPropsText -match '(?m)^\\s*spring\\.datasource\\.hikari\\.password\\s*=\\s*.+$')
              )
              if (-not $hasDbPassword) {
                Write-Error "El Secret file de prod debe contener spring.datasource.password o spring.datasource.hikari.password con un valor no vacío"
                exit 1
              }

              $deployUrl = "$($env:TOMCAT_BASE_URL)/manager/text/deploy?path=$($env:TOMCAT_CONTEXT_PATH)&update=true"
              $tomcatUser = ($env:TOMCAT_USER | ForEach-Object { $_.Trim() })
              $tomcatPass = ($env:TOMCAT_PASS | ForEach-Object { $_.Trim() })
              if ([string]::IsNullOrWhiteSpace($tomcatUser) -or [string]::IsNullOrWhiteSpace($tomcatPass)) {
                Write-Error "Credenciales Tomcat vacías o inválidas en Jenkins Credentials"
                exit 1
              }

              try {
                $jarExe = (Get-Command jar.exe -ErrorAction SilentlyContinue).Source
                if (-not $jarExe) {
                  Write-Error "jar.exe no está disponible en el agente Jenkins"
                  exit 1
                }

                $tmpPatchRoot = Join-Path $env:TEMP ("lugus-war-patch-" + [guid]::NewGuid().ToString("N"))
                $tmpWebInfClasses = Join-Path $tmpPatchRoot "WEB-INF\\classes"
                New-Item -ItemType Directory -Path $tmpWebInfClasses -Force | Out-Null

                $targetProdProps = Join-Path $tmpWebInfClasses "application-prod.properties"
                Copy-Item -Path $env:PROD_PROPS_FILE -Destination $targetProdProps -Force

                Push-Location $tmpPatchRoot
                & $jarExe uf "$warPath" "WEB-INF/classes/application-prod.properties"
                $jarExitCode = $LASTEXITCODE
                Pop-Location

                if ($jarExitCode -ne 0) {
                  Write-Error "No se pudo actualizar application-prod.properties dentro del WAR. ExitCode $jarExitCode"
                  exit $jarExitCode
                }

                if (Test-Path $tmpPatchRoot) { Remove-Item $tmpPatchRoot -Recurse -Force }

                $curlExe = (Get-Command curl.exe -ErrorAction SilentlyContinue).Source
                if (-not $curlExe) {
                  Write-Error "curl.exe no está disponible en el agente Jenkins"
                  exit 1
                }

                $tmpOut = Join-Path $env:TEMP "tomcat-deploy-response.txt"
                if (Test-Path $tmpOut) { Remove-Item $tmpOut -Force }

                & $curlExe --silent --show-error --fail-with-body --user "${tomcatUser}:${tomcatPass}" --upload-file "$warPath" "$deployUrl" --output "$tmpOut"
                if ($LASTEXITCODE -ne 0) {
                  $body = if (Test-Path $tmpOut) { Get-Content $tmpOut -Raw } else { '' }
                  Write-Error "Error desplegando en Tomcat con curl. ExitCode $LASTEXITCODE"
                  if (-not [string]::IsNullOrWhiteSpace($body)) {
                    Write-Error "Respuesta Tomcat: $body"
                  }
                  exit $LASTEXITCODE
                }

                Write-Host "Deploy OK en $($env:TOMCAT_BASE_URL)$($env:TOMCAT_CONTEXT_PATH)"
              } catch {
                Write-Error "Error desplegando en Tomcat: $($_.Exception.Message)"
                throw
              }
            '''
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'lugus/**/target/surefire-reports/*.xml, lugus/**/target/failsafe-reports/*.xml'
      archiveArtifacts allowEmptyArchive: true, artifacts: 'lugus/**/target/*.jar, lugus/**/target/*.war'
    }
  }
}
