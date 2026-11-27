UCoffee

A coffee ordering platform made of three apps and one shared backend API.
Built as a student project (XADAD7112) around the needs of a non-profit's coffee brand, where every cup helps fund a homelessness programme.

Overview
App	Platform	Purpose
Customer app	Android (Kotlin, Jetpack Compose)	Browse the menu, choose a size, pick pickup or delivery, pay with PayFast, track orders
Admin app	Windows (C# WinForms)	Manage products, orders, ingredient stock and sales reports; super admin approves staff
Driver app	Android (Kotlin, Jetpack Compose)	Log in, see orders that are out for delivery, mark them delivered
API	ASP.NET Core Web API	Authentication, business rules and data access for all three apps

All apps talk only to the API and never to the database directly.

Architecture
Customer app ─┐
Admin app ────┼──►  ASP.NET Core Web API  ──►  Entity Framework Core  ──►  Azure SQL
Driver app ───┘            │
                           └──►  PayFast (payment + server notification)
Features

Customer

Register and log in
Browse the menu and product details
Sizes: 250 / 350 / 500 ml
Cart and checkout, pickup (free) or delivery (R20)
PayFast online payment (sandbox)
Order confirmation, history and live status

Admin

Secure login with Super and Staff levels (new staff need approval by a super admin)
View and update orders
Product management (CRUD)
Ingredient and stock tracking
Sales reports (how much was sold, and which drinks)

Driver

Secure driver login
List of orders that are out for delivery (refreshes every 30 seconds)
Order detail with items, customer phone and map navigation
Mark an order as delivered (shows in the admin app)
Order flow

Pending → Paid → Ready → Shipped → Delivered (or Cancelled)

Payment moves an order from Pending to Paid. This is confirmed by PayFast's server notification, never by the app.
The barista sets Ready, then Shipped when the order is handed to the driver.
The driver can only change a Shipped delivery order to Delivered.
Unpaid orders expire after 30 minutes and their stock is restored.
Tech stack
Backend: ASP.NET Core Web API, Entity Framework Core, Azure SQL Database
Android apps: Kotlin, Jetpack Compose, Navigation Compose, Retrofit + OkHttp, DataStore, Coil
Admin app: C# WinForms, Newtonsoft.Json
Payments: PayFast (sandbox)
Hosting: Azure App Service
Security
JWT authentication with role-based access (Customer, Admin, Driver)
BCrypt password hashing
Android tokens stored encrypted with the Android Keystore (AES-GCM)
Entity Framework Core parameterised queries (SQL injection protection)
Rate limiting on the API (stricter on login and register)
Server-side payment confirmation and amount checks
Security headers and HTTPS
