#!/usr/bin/env groovy
import groovy.transform.Field

@Field static final metaRunner = 'pyenv'
@Field metaRunnerRoot = env.PYENV_ROOT ?: env.HOME ? "${HOME}/.${metaRunner}" : "${JENKINS_HOME}/.${metaRunner}"


def call(String version='3.7.0', method=null, cl) {
  print "Setting up Python version ${version}!"
  def utils = new info.pedrocesar.utils()

  if (0 != sh (returnStatus: true, script: "which ${metaRunner}") && !fileExists("${metaRunnerRoot}/bin/${metaRunner}")) {
    installPyenv()
  }

  if (!fileExists("${metaRunnerRoot}/versions/${version}/")) {
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH", "PYENV_ROOT=${metaRunnerRoot}"]) {
      installVersion(metaRunner, version)
    }
  }

  withEnv(["PATH=${metaRunnerRoot}/shims:${metaRunnerRoot}/bin/:$PATH", "PYENV_ROOT=${metaRunnerRoot}", "PYENV_SHELL=sh"]) {
    sh "${metaRunner} rehash && ${metaRunner} local ${version}"
    cl()
  }

  if (method == 'clean') {
    print "Removing Python ${version}!!!"
    withEnv(["PATH=${metaRunnerRoot}/bin/:$PATH", "PYENV_ROOT=${metaRunnerRoot}"]) {
      utils.deleteVersion(metaRunnerRoot, version)
    }
  }
}


def installPyenv() {
  print "Installing ${metaRunner}"
  installMetaRunner(metaRunner, metaRunnerRoot)
}


def installMetaRunner(String metaRunner, String metaRunnerRoot){
  sh """
  git clone https://github.com/${metaRunner}/${metaRunner}.git ${metaRunnerRoot}
  cd "${metaRunnerRoot}"
  src/configure --without-ssl && make -C src
  """
}


def installVersion(metaRunner, version) {
  sh "${metaRunner} install ${version}"
}
