#!/usr/bin/groovy
package info.pedrocesar
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def installMetaRunner(String metaRunner, String metaRunnerRoot){
  sh """ 
  git clone https://github.com/${metaRunner}/${metaRunner}.git ${metaRunnerRoot}
  cd "${metaRunnerRoot}"
  src/configure --without-ssl && make -C src
  """
}

@NonCPS
def installVersion(String metaRunner, String version) {
    sh "${metaRunner} install ${version}"
}

@NonCPS
def deleteVersion(String metaRunnerRoot, String version) {
  File directory = new File("${metaRunnerRoot}/versions/${version}")
  
  directory.deleteDir()
}

@NonCPS
def purgeAllVersions(String metaRunnerRoot) {
  File directory = new File("${metaRunnerRoot}/versions/")

  directory.listFiles().each{
    it.deleteDir()
  }
}
