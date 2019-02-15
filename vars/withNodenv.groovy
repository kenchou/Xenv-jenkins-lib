#!/usr/bin/env groovy
import groovy.transform.Field

@Field metaRunner = 'nodenv'
@Field metaRunnerRoot = env.NODENV_ROOT ?: env.HOME ? "${HOME}/.${metaRunner}" : "${JENKINS_HOME}/.${metaRunner}"


def call(String version='10.15.1', method=null, cl) {
  def utils = new info.pedrocesar.utils()

  print "Setting up NodeJS version ${version}!"

  if (0 != sh (returnStatus: true, script: "which ${metaRunner}") && !fileExists("${metaRunnerRoot}/bin/${metaRunner}")) {
    installNodenv(metaRunner)
    sh "git clone https://github.com/${metaRunner}/node-build.git ${metaRunnerRoot}/plugins/node-build"
  }

  if (!fileExists("${metaRunnerRoot}/versions/${version}/")) {
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH", "NODENV_ROOT=${metaRunnerRoot}"]) {
      utils.installVersion(metaRunner, version)
    }
  }

  withEnv(["PATH=${metaRunnerRoot}/shims:${metaRunnerRoot}/bin/:$PATH", "NODENV_ROOT=${metaRunnerRoot}", "NODENV_SHELL=sh"]) {
    sh "${metaRunner} rehash && ${metaRunner} local ${version}"
    cl()
  }

  if (method == 'clean') {
    print "Removing NodeJS ${version}!!!"
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH", "NODENV_ROOT=${metaRunnerRoot}"]) {
      utils.deleteVersion(metaRunner, version)
    }
  } 
}

def installNodenv(String metaRunner, String metaRunnerRoot) {
  print "Installing ${metaRunner}"
  new info.pedrocesar.utils().installMetaRunner(metaRunner, metaRunnerRoot)
}

def purgeAll() {
  print "Removing all versions of ${metaRunner}"
  new info.pedrocesar.utils().purgeAllVersions(metaRunnerRoot)
}
