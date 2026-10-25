# FarmConnect — Phase 1

A Spring Boot + Thymeleaf + MySQL marketplace homepage prototype.

## Requirements
- Java 21 (or Java 17 if you update `java.version` in `pom.xml`)
- Maven 3.9+ installed, OR use the Maven wrapper if you add one
- MySQL Server running

## 1. Create the database
Open MySQL Workbench or a MySQL terminal and run:

```sql
CREATE DATABASE farmconnect;
```

## 2. Configure database credentials
Edit `src/main/resources/application.properties`. The default is user `root` and a blank password.
If your MySQL root password is not blank, set environment variables before running.

PowerShell:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

Do not commit real passwords to a public repository.

## 3. Run in VS Code
Open the `FarmConnect` folder in VS Code. Open Terminal > New Terminal, then run:

```powershell
mvn spring-boot:run
```

If Maven is not installed, install Maven or create/open the project through Spring Initializr and copy these files into it.

Open `http://localhost:8080`.

## Phase 1 functionality
- Responsive landing page
- Demo product listings seeded into MySQL when the products table is empty
- Search by product, farmer, location or description
- Category filtering and price/name sorting
- Product details modal
- Demo save/favourite interaction
- Basic navigation for desktop and mobile

## Important prototype notes
- Seed product listings, farmer names, quantities and prices are sample data.
- Demo listings use sample farmer names and do not invent real contact emails. Only registered farmer listings (or legacy crops matched unambiguously to a registered farmer) expose a contact email.
- The admin-assisted purchase button only shows a prototype notice.
- No authentication, real cart, order creation, payment, messaging or delivery system is implemented in Phase 1.
- Product photos load from Unsplash, so internet access is needed for those images.
- `spring.jpa.hibernate.ddl-auto=update` is for development only. For a production deployment, use database migrations, secure configuration, validation, authentication and authorization.

## Added marketplace features

- Buyer registration at `/buyer/register`; buyers and farmers share `/login` and are redirected to the correct dashboard based on role.
- Contact Farmer uses the farmer email stored on a crop. When the browser has no email app configured, the address is also displayed with a copy-email option. Demo listings without a verified contact email remain clearly marked as unavailable rather than inventing a recipient.
- At startup, legacy crop records are linked to a registered farmer's email only when exactly one registered farmer has the same name; legacy orders inherit that email from their product. Ambiguous names are left unchanged.
- Buyers can place orders from a product detail modal, view order history on the buyer dashboard, and orders reduce available stock. Farmers can see orders for their own listings on the farmer dashboard.
- New tables `buyers` and `crop_orders` are created/updated by JPA because `spring.jpa.hibernate.ddl-auto=update` is enabled.

### Run

Open the `FarmConnect` folder in VS Code and run:

```powershell
& "C:\apache-maven-3.10.0\bin\mvn.cmd" clean compile
& "C:\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
```

Ensure MySQL is running and your local `application.properties` datasource credentials match your machine. Do not publish database passwords.

## Product details modal and buyer actions

The marketplace product modal uses the expanded two-column layout on desktop and a stacked layout on small screens. For newly uploaded crops, the farmer email is saved with the listing. **Contact farmer** opens an email draft and also displays the address with a copy-email option, because a browser may not have a mail app configured. Older demo listings without verified email show a clear unavailable message. **Order now** opens the buyer order form; buyers need to register/login first. Orders reduce the available quantity, and a product is deactivated when its quantity reaches zero.

## Run locally

1. Configure your local MySQL URL, username, and password in `src/main/resources/application.properties`.
2. Run `mvn clean compile` (or use your Maven installation path on Windows).
3. Run `mvn spring-boot:run` and open `http://localhost:8080`.

Keep your local `application.properties` secrets private. The included upload sample image is only a demo listing image. MySQL credentials can be configured with `DB_USERNAME` and `DB_PASSWORD`; defaults are `root`/`root` for local development only. Set secure credentials before any public deployment.
