# 🚀 ChaTTy Deployment Guide

This guide provides step-by-step instructions to deploy the **ChaTTy** full-stack application.

- **Frontend (React + Vite)** is deployed to **Vercel**.
- **Backend (Spring Boot + WebSockets)** is deployed to **Render** using a multi-stage Docker build.
- **Database (MySQL)** is deployed on a cloud MySQL provider (like **Aiven**, **Clever Cloud**, or **TiDB Cloud**).

---

## 🛠️ Step 1: Set Up a Cloud MySQL Database

Your backend needs a persistent MySQL database. Since Render's Free tier does not include a free persistent MySQL database directly anymore, we highly recommend using a free external cloud database provider like **Aiven** or **Clever Cloud**.

### Option A: Using Clever Cloud (Recommended & Free)
1. Go to [Clever Cloud](https://www.clever-cloud.com/) and create a free account.
2. Click **"Create..."** -> **"An add-on"** -> select **"MySQL"**.
3. Choose the **Free Plan (Shared)** and click **"Create"**.
4. Once created, go to the add-on page. Under **"Connection URIs"**, copy the following values:
   - **Host** (e.g., `bhxx...mysql.services.clever-cloud.com`)
   - **Database Name** (e.g., `bhxx...`)
   - **User** (e.g., `ujxx...`)
   - **Password** (e.g., `wRxx...`)
   - **Port** (usually `3306`)

### Option B: Using Aiven (Free Tier Available)
1. Go to [Aiven](https://aiven.io/) and sign up.
2. Create a **MySQL** service on the free tier.
3. Once active, copy the **Service URI**, **Host**, **Port**, **User**, and **Password**.

> [!IMPORTANT]
> Write down your database Host, Database Name, User, and Password. You will need them in **Step 2**.

---

## 📦 Step 2: Deploy the Backend on Render

We have created an optimized `backend/Dockerfile` that automatically handles the Maven build and creates the `.jar` execution package directly inside Render's cloud container. **You do not need to commit any `.jar` files to Git!**

1. Push your latest code changes (including the new `backend/Dockerfile`) to your **GitHub** repository.
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
| `MYSQL_URL` | `jdbc:mysql://<YOUR_DB_HOST>:<PORT>/<DATABASE_NAME>?useSSL=true` | The JDBC connection string to your cloud database |
| `MYSQL_USER` | `<YOUR_DATABASE_USER>` | Your cloud database username |
| `MYSQL_PASSWORD` | `<YOUR_DATABASE_PASSWORD>` | Your cloud database password |
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
