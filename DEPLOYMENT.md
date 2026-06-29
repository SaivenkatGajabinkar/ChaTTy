# 🚀 ChaTTy Deployment Guide

This guide provides step-by-step instructions to deploy the **ChaTTy** full-stack application.

- **Frontend (React + Vite)** is deployed to **Vercel**.
- **Backend (Spring Boot + WebSockets)** is deployed to **Render** using a multi-stage Docker build.
- **Database (PostgreSQL)** is deployed on a cloud provider (like **Neon**).

---

## 🛠️ Step 1: Set Up a Cloud PostgreSQL Database

Your backend needs a persistent PostgreSQL database. Since Render's Free tier does not include a free persistent database directly anymore, we highly recommend using a free cloud database provider like **Neon**.

### Using Neon (Free & Managed)
1. Go to [Neon](https://neon.tech/) and create a free account.
2. Create a new project named `chatty-db` and select the **PostgreSQL** version.
3. Once created, you will see your **Connection Details** on the dashboard. Copy the following:
   - **Host** (e.g., `ep-round-wind-adzvb2hz-pooler.c-2.us-east-1.aws.neon.tech`)
   - **Database Name** (usually `neondb`)
   - **Username** (usually `neondb_owner`)
   - **Password** (e.g., `npg_KeRO...`)

> [!IMPORTANT]
> The database connection URL for Java Spring Boot must use the **`jdbc:postgresql://`** prefix.
> Write down your database Host, Database Name, Username, and Password. You will need them in **Step 2**.

---

## 📦 Step 2: Deploy the Backend on Render

We have created an optimized `backend/Dockerfile` that automatically handles the Maven build and creates the `.jar` execution package directly inside Render's cloud container. **You do not need to commit any `.jar` files to Git!**

1. Push your latest code changes (including the new `backend/Dockerfile` and `application.properties`) to your **GitHub** repository.
2. Log in to [Render](https://render.com/).
3. Click the **"New +"** button in the top right and select **"Web Service"**.
4. Connect your GitHub repository.
5. In the creation form, configure the following:
   - **Name**: `chatty-backend`
   - **Root Directory**: `backend`
   - **Runtime**: Select **Docker** (Render will automatically detect the `Dockerfile` inside the `backend` folder)
   - **Instance Type**: **Free**
6. Scroll down and click **"Advanced"** to add your **Environment Variables**:

| Key | Value / Example | Description |
|---|---|---|
| `DB_USER` | `neondb_owner` | Your Neon database username |
| `DB_PASSWORD` | `npg_KeROBkAg15CI` | Your Neon database password |
| `CORS_ORIGIN` | `https://your-frontend.vercel.app` *(Leave as `*` temporarily, update in Step 4)* | The URL of your deployed frontend |

7. Click **"Create Web Service"**.
8. Wait a few minutes for Render to pull the repository, build the Java application via Maven, and deploy the service. Once successfully deployed, copy the **Render URL** (e.g., `https://chatty-backend.onrender.com`).

---

## 🎨 Step 3: Deploy the Frontend on Vercel

1. Go to [Vercel](https://vercel.com/) and sign up or log in.
2. Click **"Add New"** -> **"Project"** and import your GitHub repository.
3. Under **Configure Project**:
   - **Root Directory**: Click "Edit" and select **`frontend`**.
   - **Framework Preset**: Vercel will automatically detect **Vite**.
4. Open the **"Environment Variables"** dropdown and add the following variable:

| Key | Value | Description |
|---|---|---|
| `VITE_API_URL` | `https://chatty-backend.onrender.com` *(Use your actual Render URL from Step 2)* | Points your React frontend to the deployed backend |

5. Click **"Deploy"**.
6. Vercel will build and deploy your React app in less than a minute. Once completed, Vercel will provide you with a production URL (e.g., `https://chatty-frontend-abc.vercel.app`). Copy this URL.

---

## 🔄 Step 4: Secure CORS Configuration (Crucial for WebSockets)

For security and correct WebSocket functionality, your backend needs to allow requests from your frontend.

1. Go back to your [Render Dashboard](https://dashboard.render.com/).
2. Select your `chatty-backend` Web Service.
3. Click on the **"Environment"** tab in the left sidebar.
4. Locate the `CORS_ORIGIN` environment variable.
5. Change its value from `*` or a placeholder to your actual **Vercel frontend production URL** (e.g., `https://chatty-frontend-abc.vercel.app`).
6. Click **"Save Changes"**.
7. Render will automatically redeploy the backend with the secure CORS configuration.

---

## 🎉 Verification

Once the redeployment is complete:
1. Open your Vercel frontend URL in the browser.
2. Register a new user account.
3. Check your Cloud MySQL database to verify that the tables (e.g. `users`, `messages`, `friends`, `conversations`) were automatically created by Hibernate!
4. Open the application in two different browsers, register two accounts, add each other as friends, and test real-time chat with auto-translation!
