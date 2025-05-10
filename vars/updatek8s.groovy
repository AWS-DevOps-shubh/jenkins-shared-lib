#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'git-hub-cred'
    def gitUserName = config.gitUserName ?: 'AWS-DevOps-shubh'
    def gitUserEmail = config.gitUserEmail ?: 'devshubh2204@gmail.com'
    def gitBranch = config.gitBranch ?: 'dev-shubh'

    echo "Updating Kubernetes manifests with image tag: ${imageTag}"

    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"

            sed -i "s|image: devshubh2204/geminiclonenip:.*|image: devshubh2204/geminiclonenip:${imageTag}|g" ${manifestsPath}/gemini-deployment.yml

            if [ -f "${manifestsPath}/gemini-ingress.yml" ]; then
                sed -i "s|host: .*|host: geminishubh.34.251.105.234.nip.io|g" ${manifestsPath}/gemini-ingress.yml
            fi

            if git diff --quiet; then
                echo "No changes to commit"
            else
                git add ${manifestsPath}/*.yml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/AWS-DevOps-shubh/dev-gemini-clone.git
                git push origin HEAD:${gitBranch}
            fi
        """
    }
}
