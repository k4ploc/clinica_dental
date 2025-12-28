# 🚀 Guía de Despliegue en Diferentes Plataformas

## 📋 Tabla de Contenidos

1. [Docker Local](#docker-local)
2. [Docker Swarm](#docker-swarm)
3. [Kubernetes](#kubernetes)
4. [Azure Container Instances](#azure-container-instances)
5. [AWS ECS](#aws-ecs)
6. [Heroku](#heroku)

---

## 🐳 Docker Local

### Prerrequisitos
```bash
# Windows
winget install Docker.DockerDesktop

# macOS
brew install docker docker-compose

# Linux
sudo apt-get install docker.io docker-compose
```

### Desplegar
```bash
# Desarrollo
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener
docker-compose down
```

### Verificar
```bash
curl http://localhost:8080/actuator/health
```

---

## 🔗 Docker Swarm

### Inicializar Swarm
```bash
docker swarm init
```

### Crear Stack
```bash
docker stack deploy -c docker-compose.prod.yml clinica
```

### Monitorear
```bash
docker stack services clinica
docker stack ps clinica
docker service logs clinica_app
```

### Actualizar
```bash
docker service update --image clinica:v1.1 clinica_app
```

### Remover
```bash
docker stack rm clinica
```

---

## ☸️ Kubernetes

### Prerrequisitos
```bash
# Instalar kubectl
kubectl version

# Acceso a cluster
kubectl config current-context
```

### 1. Crear Namespace
```bash
kubectl create namespace clinica
```

### 2. Crear Secretos
```bash
kubectl create secret generic db-credentials \
  --from-literal=username=postgres \
  --from-literal=password=StrongPassword123! \
  -n clinica
```

### 3. Crear ConfigMap
```bash
kubectl create configmap app-config \
  --from-literal=SPRING_PROFILES_ACTIVE=prod \
  --from-literal=SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db \
  -n clinica
```

### 4. Desplegar Aplicación
```yaml
# k8s-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: clinica-app
  namespace: clinica
spec:
  replicas: 2
  selector:
    matchLabels:
      app: clinica
  template:
    metadata:
      labels:
        app: clinica
    spec:
      containers:
      - name: app
        image: myregistry.azurecr.io/clinica:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_DATASOURCE_URL
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 40
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: clinica-service
  namespace: clinica
spec:
  selector:
    app: clinica
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: clinica
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: clinica_db
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
  - metadata:
      name: postgres-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

### Desplegar en Kubernetes
```bash
kubectl apply -f k8s-deployment.yml

# Verificar
kubectl get all -n clinica
kubectl logs -f deployment/clinica-app -n clinica

# Acceder
kubectl port-forward svc/clinica-service 8080:80 -n clinica
```

---

## ☁️ Azure Container Instances

### Prerrequisitos
```bash
az login
az group create --name clinica-rg --location eastus
```

### Crear Container Registry
```bash
az acr create --resource-group clinica-rg --name clinicaacr --sku Basic
az acr login --name clinicaacr
```

### Push Image
```bash
docker tag clinica:latest clinicaacr.azurecr.io/clinica:latest
docker push clinicaacr.azurecr.io/clinica:latest
```

### Desplegar
```bash
az container create \
  --resource-group clinica-rg \
  --name clinica-app \
  --image clinicaacr.azurecr.io/clinica:latest \
  --cpu 1 --memory 1 \
  --registry-login-server clinicaacr.azurecr.io \
  --registry-username <username> \
  --registry-password <password> \
  --ip-address Public \
  --ports 8080 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATASOURCE_URL="jdbc:postgresql://db.postgres.database.azure.com:5432/clinica_db" \
    SPRING_DATASOURCE_USERNAME="dbuser@dbserver" \
    SPRING_DATASOURCE_PASSWORD="StrongPassword123!"
```

### Verificar
```bash
az container show \
  --resource-group clinica-rg \
  --name clinica-app \
  --query "{FQDN:ipAddress.fqdn,ProvisioningState:provisioningState}" \
  --out table
```

---

## 🔶 AWS ECS

### Crear ECR Repository
```bash
aws ecr create-repository --repository-name clinica --region us-east-1
```

### Login a ECR
```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com
```

### Push Image
```bash
docker tag clinica:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/clinica:latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/clinica:latest
```

### Crear ECS Cluster
```bash
aws ecs create-cluster --cluster-name clinica-cluster
```

### Crear Task Definition
```json
{
  "family": "clinica-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "clinica-app",
      "image": "<ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/clinica:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "hostPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://clinica-db.xxxxx.rds.amazonaws.com:5432/clinica_db"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:<ACCOUNT_ID>:secret:clinica/db-user"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:<ACCOUNT_ID>:secret:clinica/db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/clinica-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 40
      }
    }
  ]
}
```

### Registrar Task Definition
```bash
aws ecs register-task-definition --cli-input-json file://task-definition.json
```

### Crear Service
```bash
aws ecs create-service \
  --cluster clinica-cluster \
  --service-name clinica-service \
  --task-definition clinica-task:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxx,subnet-yyyyy],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:us-east-1:<ACCOUNT_ID>:targetgroup/clinica/xxxxx,containerName=clinica-app,containerPort=8080"
```

---

## 💜 Heroku

### Prerrequisitos
```bash
npm install -g heroku
heroku login
```

### Crear App
```bash
heroku create clinica-api
```

### Configurar Database
```bash
heroku addons:create heroku-postgresql:hobby-dev --app clinica-api
```

### Push Image
```bash
heroku container:login
docker tag clinica:latest registry.heroku.com/clinica-api/web:latest
docker push registry.heroku.com/clinica-api/web:latest
heroku container:release web --app clinica-api
```

### Configurar Variables
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod --app clinica-api
heroku config:set SPRING_DATASOURCE_URL=$(heroku config:get DATABASE_URL --app clinica-api) --app clinica-api
```

### Verificar
```bash
heroku open --app clinica-api
heroku logs -f --app clinica-api
```

---

## 📊 Comparativa de Plataformas

| Plataforma | Complejidad | Costo | Escalabilidad | Recomendado para |
|---|---|---|---|---|
| **Docker Local** | Muy Baja | $0 | Manual | Desarrollo |
| **Docker Swarm** | Media | $0+ | Automática | Producción pequeña |
| **Kubernetes** | Alta | $0+ | Automática | Producción empresarial |
| **Azure ACI** | Media | $ | Manual | Pruebas/Demo |
| **AWS ECS** | Media | $$ | Automática | Producción AWS |
| **Heroku** | Baja | $$ | Automática | Startup/MVP |

---

## 🔐 Security Checklist

- [ ] Usar Docker Secrets o Secret Manager
- [ ] No hardcodear credenciales
- [ ] Usar HTTPS/SSL
- [ ] Scan vulnerabilidades con Trivy
- [ ] Usar imágenes firmadas
- [ ] Configurar Network Policies
- [ ] Implementar RBAC
- [ ] Usar Private Registry
- [ ] Logging centralizado
- [ ] Monitoring y alertas

---

**Última actualización**: Diciembre 21, 2025
