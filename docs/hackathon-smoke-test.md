# FarmConnect quick test before hackathon demo

## Start
1. Start MySQL and ensure the `farmconnect` schema is accessible.
2. In `application.properties`, configure `DB_USERNAME` / `DB_PASSWORD` environment variables if the local MySQL credentials differ from `root` / `root`.
3. From the `FarmConnect` project directory run `mvn clean compile` and `mvn spring-boot:run` (or use your installed Maven path on Windows).
4. Open `http://localhost:8080`.

## Smoke test
- Open the homepage; confirm products render and search/category/sort work.
- Register a farmer using a unique email and password (8+ characters); log in.
- Add a crop with a JPG, PNG, or WEBP image. Confirm it appears in the marketplace and its contact email is visible in product details.
- Edit and delete a test crop from Manage My Crops.
- Register a buyer with another email; log in.
- Place an order for a crop; confirm the buyer dashboard displays it and stock decreases.
- Log in as the farmer who owns that crop; confirm the order appears in farmer orders.
- Use Contact Farmer. If a mail app is not configured, copy the email shown in the product modal.
- Check the terminal for exceptions and browser DevTools for failed requests.

## Useful database checks
```sql
USE farmconnect;
SHOW TABLES;
SELECT id, name, farmer_name, farmer_email, quantity_available, active FROM products;
SELECT id, product_name, buyer_name, buyer_email, farmer_name, farmer_email, quantity, total_price, status, created_at FROM crop_orders ORDER BY created_at DESC;
SELECT id, name, email, phone, created_at FROM buyers;
```

`crop_orders` is the table used by this application for orders. A manually-created `orders` table is not used by the current Java entity.

## Notes
- Demo listings are sample data and do not have fabricated farmer emails. Existing crops are backfilled only if their farmer name matches exactly one registered farmer.
- This is a hackathon prototype. Configure secure database credentials and review security before deploying publicly.
