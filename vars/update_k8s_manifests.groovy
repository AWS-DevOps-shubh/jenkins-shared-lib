#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'git-hub-cred'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'jenkins@example.com'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    
    withCredentials([usernamePassword(
        credentialsId: 	git-hub-cred,
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
            # Update main application deployment - note the correct image name is devshubh2204/easyshop-hack_app
            sed -i "s|image: devshubh2204/easyshop-hack_app:.*|image: devshubh2204/easyshop-hack_app:${imageTag}|g" ${manifestsPath}/easyshop-hack-deployment.yml
            
            # Update migration job if it exists
            if [ -f "${manifestsPath}/easy-shop-migration-job.yml" ]; then
                sed -i "s|image: devshubh2204/easyshop-hack_migration:.*|image: devshubh2204/easyshop-hack_migration:${imageTag}|g" ${manifestsPath}/easy-shop-migration-job.yml
            fi
            
            # Ensure ingress is using the correct domain
            if [ -f "${manifestsPath}/ingress.yaml" ]; then
                sed -i "s|host: .*|host: easyshop.letsdeployit.com|g" ${manifestsPath}/ingress.yaml
            fi
            
            # Check for changes
            if git diff --quiet; then
                echo "No changes to commit"
            else
                # Commit and push changes
                git add ${manifestsPath}/*.yaml
                git commit -m "Update image tags to ${imageTag} and ensure correct domain [ci skip]"
                
                # Set up credentials for push
                git remote set-url origin https://\${GIT_USERNAME}:\${GIT_PASSWORD}@github.com/AWS-DevOps-shubh/easyshop-hack.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}
