# Azure App Service — Deployment Guide

This guide covers deploying the Spring Boot WebSocket Chat application to
Azure App Service so it is publicly reachable at a URL like
`https://chat-app-<suffix>.azurewebsites.net`.

---

## Prerequisites

- [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) installed and logged in (`az login`)
- Java 25 + Maven available locally (or use the Maven Wrapper `./mvnw`)
- An active Azure subscription

---

## Step 1 — Build the JAR

```bash
./mvnw clean package -DskipTests
```

The artifact is created at `target/th-0.0.1-SNAPSHOT.jar`.

---

## Step 2 — Create the Azure resource group and App Service plan

```bash
# Create a resource group in the region of your choice
az group create \
    --name chat-rg \
    --location eastus

# Create an App Service plan (Free tier F1 does NOT support WebSockets;
# use at least Basic B1 for WebSocket support)
az appservice plan create \
    --name chat-plan \
    --resource-group chat-rg \
    --sku B1 \
    --is-linux

# Create the Web App (Java 21 is the highest currently listed runtime on
# Azure; Java 25 JARs compiled with --release 21 compatibility also work)
az webapp create \
    --name chat-app-<unique-suffix> \
    --resource-group chat-rg \
    --plan chat-plan \
    --runtime "JAVA:21:Java SE:21"
```

> Replace `<unique-suffix>` with a globally unique string (e.g. your initials + date).

---

## Step 3 — Deploy the JAR

```bash
az webapp deploy \
    --resource-group chat-rg \
    --name chat-app-<unique-suffix> \
    --src-path target/th-0.0.1-SNAPSHOT.jar \
    --type jar
```

The deployment triggers a restart automatically.

---

## Step 4 — Set the Spring profile to `prod`

```bash
az webapp config appsettings set \
    --resource-group chat-rg \
    --name chat-app-<unique-suffix> \
    --settings SPRING_PROFILES_ACTIVE=prod
```

This activates the `prod` profile in `application.yml`, which:
- Disables the H2 console (security hardening)
- Sets the server port from the `PORT` env variable injected by Azure
- Reduces log verbosity

---

## Step 5 — Enable WebSockets in App Service

**This step is mandatory.** Azure App Service disables WebSockets by default.

Via Azure CLI:
```bash
az webapp config set \
    --resource-group chat-rg \
    --name chat-app-<unique-suffix> \
    --web-sockets-enabled true
```

Or via the Azure Portal:
1. Open the App Service → **Configuration** → **General settings**
2. Set **Web sockets** to **On**
3. Click **Save** and restart the app

---

## Step 6 — Verify the deployment

Open `https://chat-app-<unique-suffix>.azurewebsites.net` in a browser.
You should see the login page. Open a second browser tab or a different
device and verify that messages appear in real time in both windows.

---

## Limitations of the current setup

| Limitation | Impact | Solution |
|------------|--------|----------|
| **H2 in-memory database** | All messages are lost when the app restarts or Azure recycles the instance | Migrate to Azure SQL Database (see `schema-notes.md`) |
| **Single instance** | The simple STOMP broker lives in process memory; messages cannot be shared across multiple App Service instances | Use Azure Service Bus or a Redis-backed STOMP broker for scale-out |
| **No authentication** | Any user who knows the URL can join | Add Spring Security with Azure AD B2C or OAuth2 |
| **No TLS termination config** | Azure handles HTTPS termination at the load balancer; the app only needs to listen on HTTP | No action needed for typical deployments |

---

## Useful commands

```bash
# Stream live application logs
az webapp log tail \
    --resource-group chat-rg \
    --name chat-app-<unique-suffix>

# Restart the app
az webapp restart \
    --resource-group chat-rg \
    --name chat-app-<unique-suffix>

# Delete all resources when done
az group delete --name chat-rg --yes --no-wait
```
