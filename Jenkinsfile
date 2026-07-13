// Pipeline CI/CD du backend MediShop.
//
// Declenche a chaque push sur la branche suivie :
//   1. BUILD  : construit l'image Docker
//   2. PUSH   : l'envoie sur Docker Hub
//   3. DEPLOY : se connecte en SSH a la VM Back et remplace le conteneur
//              (avec rollback automatique, voir deploy/deploy.sh)
//
// AUCUN SECRET N'EST ECRIT ICI. Le token Docker Hub, la cle SSH et le mot de
// passe de la base viennent des Credentials de Jenkins.

pipeline {
    agent any

    environment {
        IMAGE    = 'bayebara01012000/medishop_b'
        FRONT_IP = '20.199.183.9'   // bastion : seule machine joignable depuis Internet
        BACK_IP  = '10.0.2.10'      // VM Back : aucune IP publique
        VM_USER  = 'azureuser'
    }

    triggers {
        // Jenkins tourne en local : GitHub ne peut pas lui envoyer de webhook.
        // On interroge donc le depot toutes les 2 minutes (polling SCM).
        pollSCM('H/2 * * * *')
    }

    options {
        timestamps()
        disableConcurrentBuilds()   // jamais deux deploiements en meme temps
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {

        stage('Preparation') {
            steps {
                script {
                    // On tague l'image avec le hash du commit : chaque version est
                    // identifiable, et c'est ce qui rend le rollback possible.
                    env.TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
                echo "Version a deployer : ${env.TAG}"
            }
        }

        stage('Build') {
            steps {
                sh 'docker build -t $IMAGE:$TAG -t $IMAGE:latest .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS')]) {
                    // --password-stdin : le token ne passe PAS en argument de
                    // commande, il n'apparait donc pas dans la liste des processus.
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push $IMAGE:$TAG
                        docker push $IMAGE:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                    sshUserPrivateKey(credentialsId: 'azure-ssh', keyFileVariable: 'SSH_KEY'),
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD')
                ]) {
                    // La VM Back n'a pas d'IP publique : on rebondit par le Front (-J).
                    sh '''
                        OPTS="-i $SSH_KEY -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ConnectTimeout=20"
                        JUMP="-J $VM_USER@$FRONT_IP"

                        scp $OPTS $JUMP deploy/deploy.sh $VM_USER@$BACK_IP:/tmp/deploy.sh

                        ssh $OPTS $JUMP $VM_USER@$BACK_IP \
                            "chmod +x /tmp/deploy.sh && DB_PASSWORD='$DB_PASSWORD' /tmp/deploy.sh $IMAGE $TAG"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Backend deploye : $IMAGE:${env.TAG}"
        }
        failure {
            echo "Echec du pipeline. Si le deploiement a echoue, deploy.sh a deja"
            echo "tente un rollback vers la version precedente : verifier les logs ci-dessus."
        }
        always {
            sh 'docker logout 2>/dev/null || true'
        }
    }
}
