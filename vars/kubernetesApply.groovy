#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'git-hub-cred'
    def gitUserName = config.gitUserName ?: 'AWS-DevOps-shubh'
    def gitUserEmail = config.gitUserEmail ?: 'devshubh2204@gmail.com'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: 	'git-hub-cred',
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        // Configure Git
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """
        
        // Update deployment manifests with new image tags - using proper Linux sed syntax
        sh """
            # Update main application deployment - note the correct image name is devshubh2204/geminiclonenip
            sed -i "s|image: devshubh2204/geminiclonenip:.*|image: devshubh2204/geminiclonenip:${imageTag}|g" ${manifestsPath}/gemini-deployment.yml

            
            # Ensure ingress is using the correct domain
            if [ -f "${manifestsPath}/gemini-ingress.yml" ]; then
                sed -i "s|host: .*|host: geminishubh.34.251.105.234.nip.io|g" ${manifestsPath}/gemini-ingress.yml
            fi
            
            # Check for changes
            if git diff --quiet; then
                echo "No changes to commit"
            else
                # Commit and push changes
                git add ${manifestsPath}/*.yml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                
                # Set up credentials for push
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/AWS-DevOps-shubh/dev-gemini-clone.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}
