#def call(){
    #sh "trivy fs . -o results.json"
#}

def call() {
    sh 'docker run --rm -v $PWD:/app aquasec/trivy fs /app -o results.json'
}
