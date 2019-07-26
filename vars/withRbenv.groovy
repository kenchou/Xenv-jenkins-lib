#!/usr/bin/env groovy
import groovy.transform.Field

@Field metaRunner = 'rbenv'
@Field metaRunnerRoot = env.RBENV_ROOT ?: env.HOME ? "${HOME}/.${metaRunner}" : "${JENKINS_HOME}/.${metaRunner}"


def call(String version='2.5.1', method=null, cl) {
  def utils = new info.pedrocesar.utils()

  print "Setting up Ruby version ${version}!"

  if (0 != sh (returnStatus: true, script: "which ${metaRunner}") && !fileExists("${metaRunnerRoot}/bin/${metaRunner}")) {
    print "Installing ${metaRunner}"
    new info.pedrocesar.utils().installMetaRunner(metaRunner, metaRunnerRoot)
    sh "git clone https://github.com/${metaRunner}/ruby-build.git ${metaRunnerRoot}/plugins/ruby-build"
  }

  if (!fileExists("${metaRunnerRoot}/versions/${version}/")) {
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH"]) {
      utils.installVersion(metaRunner, version)
    }
  }

  withEnv(["PATH=${metaRunnerRoot}/shims:${metaRunnerRoot}/bin/:$PATH", "RBENV_SHELL=sh"]) {
    sh "${metaRunner} rehash && ${metaRunner} local ${version}"
    cl()
  }

  if (method == 'clean') {
    print "Removing Ruby ${version}!!!"
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH"]) {
      utils.deleteVersion(metaRunner, version)
    }
  }
}

def purgeAll() {
  print "Removing all versions of ${metaRunner}"
  new info.pedrocesar.utils().purgeAllVersions(metaRunnerRoot)
}
