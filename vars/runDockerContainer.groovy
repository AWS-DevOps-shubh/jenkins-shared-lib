// vars/runDockerContainer.groovy
def call(Map config = [:]) {
    def image = config.image ?: error("Image name not provided")
    def ports = config.ports ?: "3000:80"

    sh "docker run -d -p ${ports} ${image}"
}
