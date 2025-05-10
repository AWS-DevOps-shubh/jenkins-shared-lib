// vars/dockerImage.groovy
def call(String image) {
    // Setting the DOCKER_IMAGE environment variable
    env.DOCKER_IMAGE = image
    echo "Docker Image set to: ${env.DOCKER_IMAGE}"
}
