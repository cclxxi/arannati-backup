-- Additional migration for updating existing entities
-- PostgreSQL version

-- ============================================
-- 1. Update Product entity with missing fields
-- ============================================

-- Add missing columns to products table if they don't exist
DO $$
    BEGIN
        -- Check and add short_description
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'products' AND column_name = 'short_description') THEN
            ALTER TABLE products ADD COLUMN short_description VARCHAR(500);
        END IF;

        -- Check and add sort_order
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'products' AND column_name = 'sort_order') THEN
            ALTER TABLE products ADD COLUMN sort_order INTEGER DEFAULT 0;
        END IF;

        -- Check and add active column if missing
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'products' AND column_name = 'active') THEN
            ALTER TABLE products ADD COLUMN active BOOLEAN DEFAULT true;
        END IF;
    END $$;

-- ============================================
-- 2. Update Category entity with missing fields
-- ============================================

DO $$
    BEGIN
        -- Check and add parent_id for category hierarchy
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'categories' AND column_name = 'parent_id') THEN
            ALTER TABLE categories ADD COLUMN parent_id BIGINT;
            ALTER TABLE categories ADD CONSTRAINT fk_categories_parent
                FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE;
        END IF;

        -- Check and add image_path
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'categories' AND column_name = 'image_path') THEN
            ALTER TABLE categories ADD COLUMN image_path VARCHAR(500);
        END IF;

        -- Check and add sort_order
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'categories' AND column_name = 'sort_order') THEN
            ALTER TABLE categories ADD COLUMN sort_order INTEGER DEFAULT 0;
        END IF;

        -- Check and add active column
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'categories' AND column_name = 'active') THEN
            ALTER TABLE categories ADD COLUMN active BOOLEAN DEFAULT true;
        END IF;
    END $$;

-- ============================================
-- 3. Update Brand entity with missing fields
-- ============================================

DO $$
    BEGIN
        -- Check and add country
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'brands' AND column_name = 'country') THEN
            ALTER TABLE brands ADD COLUMN country VARCHAR(100);
        END IF;

        -- Check and add website_url
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'brands' AND column_name = 'website_url') THEN
            ALTER TABLE brands ADD COLUMN website_url VARCHAR(500);
        END IF;

        -- Check and add logo_path
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'brands' AND column_name = 'logo_path') THEN
            ALTER TABLE brands ADD COLUMN logo_path VARCHAR(500);
        END IF;

        -- Check and add sort_order
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'brands' AND column_name = 'sort_order') THEN
            ALTER TABLE brands ADD COLUMN sort_order INTEGER DEFAULT 0;
        END IF;

        -- Check and add active column
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'brands' AND column_name = 'active') THEN
            ALTER TABLE brands ADD COLUMN active BOOLEAN DEFAULT true;
        END IF;
    END $$;

-- ============================================
-- 4. Create or update Product Images table
-- ============================================

CREATE TABLE IF NOT EXISTS product_images (
                                              id BIGSERIAL PRIMARY KEY,
                                              product_id BIGINT NOT NULL,
                                              image_path VARCHAR(500) NOT NULL,
                                              alt_text VARCHAR(255),
                                              is_main BOOLEAN DEFAULT false,
                                              sort_order INTEGER DEFAULT 0,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                              CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create indexes for product_images
CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_product_images_is_main ON product_images(is_main);
CREATE INDEX IF NOT EXISTS idx_product_images_sort_order ON product_images(sort_order);

-- ============================================
-- 5. Update Orders table with missing fields
-- ============================================

DO $$
    BEGIN
        -- Check and add order_number if missing
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'order_number') THEN
            ALTER TABLE orders ADD COLUMN order_number VARCHAR(50) UNIQUE;
        END IF;

        -- Check and add delivery_method
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'delivery_method') THEN
            ALTER TABLE orders ADD COLUMN delivery_method VARCHAR(50) DEFAULT 'courier';
        END IF;

        -- Check and add payment_method
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'payment_method') THEN
            ALTER TABLE orders ADD COLUMN payment_method VARCHAR(50) DEFAULT 'cash';
        END IF;

        -- Check and add payment_status
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'payment_status') THEN
            ALTER TABLE orders ADD COLUMN payment_status VARCHAR(50) DEFAULT 'pending';
        END IF;

        -- Check and add notes
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'notes') THEN
            ALTER TABLE orders ADD COLUMN notes TEXT;
        END IF;

        -- Check and add shipping_amount
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'shipping_amount') THEN
            ALTER TABLE orders ADD COLUMN shipping_amount DECIMAL(10,2) DEFAULT 0;
        END IF;

        -- Check and add tax_amount
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'orders' AND column_name = 'tax_amount') THEN
            ALTER TABLE orders ADD COLUMN tax_amount DECIMAL(10,2) DEFAULT 0;
        END IF;
    END $$;

-- ============================================
-- 6. Update Order Items table with missing fields
-- ============================================

DO $$
    BEGIN
        -- Check and add product_name (for historical data)
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'order_items' AND column_name = 'product_name') THEN
            ALTER TABLE order_items ADD COLUMN product_name VARCHAR(500);
        END IF;

        -- Check and add product_sku (for historical data)
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'order_items' AND column_name = 'product_sku') THEN
            ALTER TABLE order_items ADD COLUMN product_sku VARCHAR(100);
        END IF;

        -- Check and add discount_amount
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'order_items' AND column_name = 'discount_amount') THEN
            ALTER TABLE order_items ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0;
        END IF;
    END $$;

-- ============================================
-- 7. Update Users table with additional fields if needed
-- ============================================

DO $$
    BEGIN
        -- Check and add phone if missing
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'phone') THEN
            ALTER TABLE users ADD COLUMN phone VARCHAR(20);
        END IF;

        -- Check and add is_verified if missing
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'is_verified') THEN
            ALTER TABLE users ADD COLUMN is_verified BOOLEAN DEFAULT false;
        END IF;

        -- Check and add is_active if missing (different from active)
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'is_active') THEN
            ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT true;
        END IF;

        -- Check and add reset_token fields
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'reset_token') THEN
            ALTER TABLE users ADD COLUMN reset_token VARCHAR(255);
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'reset_token_expiry') THEN
            ALTER TABLE users ADD COLUMN reset_token_expiry TIMESTAMP;
        END IF;
    END $$;

-- ============================================
-- 8. Create Roles table if it doesn't exist
-- ============================================

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default roles
INSERT INTO roles (name) VALUES
                             ('USER'),
                             ('COSMETOLOGIST'),
                             ('ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Add role_id to users table if it doesn't exist
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name = 'users' AND column_name = 'role_id') THEN
            ALTER TABLE users ADD COLUMN role_id BIGINT;
            ALTER TABLE users ADD CONSTRAINT fk_users_role
                FOREIGN KEY (role_id) REFERENCES roles(id);

            -- Set default role for existing users
            UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'USER') WHERE role_id IS NULL;

            -- Make role_id NOT NULL
            ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;
        END IF;
    END $$;

-- ============================================
-- 9. Update existing data
-- ============================================

-- Update existing products with missing data
UPDATE products SET
    is_active = true
WHERE is_active IS NULL;

UPDATE products SET
    sort_order = 0
WHERE sort_order IS NULL;

-- Update existing categories
UPDATE categories SET
    is_active = true
WHERE is_active IS NULL;

UPDATE categories SET
    sort_order = 0
WHERE sort_order IS NULL;

-- Update existing brands
UPDATE brands SET
    is_active = true
WHERE is_active IS NULL;

UPDATE brands SET
    sort_order = 0
WHERE sort_order IS NULL;

-- Generate order numbers for existing orders without them
UPDATE orders SET
    order_number = 'ORD-' || LPAD(id::text, 8, '0')
WHERE order_number IS NULL;

-- Update order items with product information for historical data
UPDATE order_items SET
                       product_name = p.name,
                       product_sku = p.sku
FROM products p
WHERE order_items.product_id = p.id
  AND (order_items.product_name IS NULL OR order_items.product_sku IS NULL);

-- ============================================
-- 10. Add constraints and indexes for new structure
-- ============================================

-- Add unique constraint for order_number if it doesn't exist
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_orders_order_number') THEN
            ALTER TABLE orders ADD CONSTRAINT uk_orders_order_number UNIQUE (order_number);
        END IF;
    END $$;

-- Create additional indexes for performance
CREATE INDEX IF NOT EXISTS idx_products_sort_order ON products(sort_order);
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_categories_sort_order ON categories(sort_order);
CREATE INDEX IF NOT EXISTS idx_brands_sort_order ON brands(sort_order);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

-- ============================================
-- 11. Create materialized view for better performance
-- ============================================

-- Materialized view for product catalog with all related data
CREATE MATERIALIZED VIEW IF NOT EXISTS product_catalog AS
SELECT
    p.id,
    p.name,
    p.description,
    p.short_description,
    p.sku,
    p.regular_price,
    p.sale_price,
    p.cosmetologist_price,
    p.admin_price,
    p.stock_quantity,
    p.weight,
    p.manufacturer_code,
    p.active,
    p.sort_order,
    p.created_at,
    p.updated_at,

    -- Category information
    c.id as category_id,
    c.name as category_name,
    c.description as category_description,
    pc.id as parent_category_id,
    pc.name as parent_category_name,

    -- Brand information
    b.id as brand_id,
    b.name as brand_name,
    b.description as brand_description,
    b.country as brand_country,

    -- Aggregated review data
    COALESCE(AVG(r.rating), 0) as average_rating,
    COUNT(r.id) as review_count,

    -- Stock status
    CASE
        WHEN p.stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN p.stock_quantity <= 10 THEN 'LOW_STOCK'
        ELSE 'IN_STOCK'
        END as stock_status,

    -- Price status
    CASE
        WHEN p.sale_price IS NOT NULL AND p.sale_price < p.regular_price THEN true
        ELSE false
        END as has_discount

FROM products p
         LEFT JOIN categories c ON p.category_id = c.id
         LEFT JOIN categories pc ON c.parent_id = pc.id
         LEFT JOIN brands b ON p.brand_id = b.id
         LEFT JOIN reviews r ON p.id = r.product_id AND r.is_active = true
WHERE p.active = true
GROUP BY p.id, c.id, pc.id, b.id;

-- Create index on materialized view
CREATE INDEX IF NOT EXISTS idx_product_catalog_category_id ON product_catalog(category_id);
CREATE INDEX IF NOT EXISTS idx_product_catalog_brand_id ON product_catalog(brand_id);
CREATE INDEX IF NOT EXISTS idx_product_catalog_stock_status ON product_catalog(stock_status);
CREATE INDEX IF NOT EXISTS idx_product_catalog_has_discount ON product_catalog(has_discount);

-- Function to refresh the materialized view
CREATE OR REPLACE FUNCTION refresh_product_catalog()
    RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW product_catalog;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 12. Create function for automatic order number generation
-- ============================================

CREATE OR REPLACE FUNCTION generate_order_number()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.order_number IS NULL THEN
        NEW.order_number := 'ORD-' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || '-' || LPAD(NEW.id::text, 6, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for automatic order number generation
DROP TRIGGER IF EXISTS trigger_generate_order_number ON orders;
CREATE TRIGGER trigger_generate_order_number
    BEFORE INSERT ON orders
    FOR EACH ROW
EXECUTE FUNCTION generate_order_number();

COMMIT;