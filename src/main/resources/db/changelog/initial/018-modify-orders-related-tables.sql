-- Migration script for Arannati enhanced functionality
-- PostgreSQL version

-- ============================================
-- 1. Update existing Product table with new pricing fields
-- ============================================

-- Add new price columns to products table
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS cosmetologist_price DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS admin_price DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS weight DECIMAL(8,2),
    ADD COLUMN IF NOT EXISTS manufacturer_code VARCHAR(100);

-- Add comments for new columns
COMMENT ON COLUMN products.cosmetologist_price IS 'Special price for cosmetologists (25% discount)';
COMMENT ON COLUMN products.admin_price IS 'Special price for administrators (40% discount)';
COMMENT ON COLUMN products.weight IS 'Product weight in grams';
COMMENT ON COLUMN products.manufacturer_code IS 'Manufacturer product code';

-- ============================================
-- 2. Create Wishlist table
-- ============================================

CREATE TABLE IF NOT EXISTS wishlist (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        product_id BIGINT NOT NULL,
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                        CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                        CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id)
);

-- Create indexes for wishlist table
CREATE INDEX IF NOT EXISTS idx_wishlist_user_id ON wishlist(user_id);
CREATE INDEX IF NOT EXISTS idx_wishlist_product_id ON wishlist(product_id);
CREATE INDEX IF NOT EXISTS idx_wishlist_created_at ON wishlist(created_at);

COMMENT ON TABLE wishlist IS 'User wishlist/favorites table';

-- ============================================
-- 3. Create Notifications table
-- ============================================

-- Create notification types enum
DO $$ BEGIN
    CREATE TYPE notification_type AS ENUM (
        'ORDER_CREATED',
        'ORDER_STATUS_CHANGED',
        'PRODUCT_REVIEW_ADDED',
        'PRODUCT_PRICE_REDUCED',
        'COSMETOLOGIST_VERIFICATION',
        'SYSTEM_MESSAGE',
        'ADMIN_MESSAGE'
        );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             title VARCHAR(255) NOT NULL,
                                             message TEXT NOT NULL,
                                             type notification_type NOT NULL,
                                             related_entity_id BIGINT,
                                             related_entity_type VARCHAR(50),
                                             is_read BOOLEAN NOT NULL DEFAULT FALSE,
                                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             read_at TIMESTAMP,

                                             CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for notifications table
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = false;

COMMENT ON TABLE notifications IS 'User notifications table';

-- ============================================
-- 4. Create Reviews table
-- ============================================

CREATE TABLE IF NOT EXISTS reviews (
                                       id BIGSERIAL PRIMARY KEY,
                                       product_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
                                       comment TEXT,
                                       is_verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
                                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                       CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       CONSTRAINT uk_reviews_user_product UNIQUE (user_id, product_id)
);

-- Create indexes for reviews table
CREATE INDEX IF NOT EXISTS idx_reviews_product_id ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);
CREATE INDEX IF NOT EXISTS idx_reviews_is_active ON reviews(is_active);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at);
CREATE INDEX IF NOT EXISTS idx_reviews_product_active ON reviews(product_id, is_active) WHERE is_active = true;

COMMENT ON TABLE reviews IS 'Product reviews and ratings table';

-- ============================================
-- 5. Create Discounts table
-- ============================================

CREATE TABLE IF NOT EXISTS discounts (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         description TEXT,
                                         brand_id BIGINT,
                                         category_id BIGINT,
                                         discount_percentage DECIMAL(5,2) CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
                                         discount_amount DECIMAL(10,2),
                                         start_date TIMESTAMP NOT NULL,
                                         end_date TIMESTAMP,
                                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_discounts_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE,
                                         CONSTRAINT fk_discounts_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
                                         CONSTRAINT chk_discount_type CHECK (
                                             (discount_percentage IS NOT NULL AND discount_amount IS NULL) OR
                                             (discount_percentage IS NULL AND discount_amount IS NOT NULL)
                                             )
);

-- Create indexes for discounts table
CREATE INDEX IF NOT EXISTS idx_discounts_brand_id ON discounts(brand_id);
CREATE INDEX IF NOT EXISTS idx_discounts_category_id ON discounts(category_id);
CREATE INDEX IF NOT EXISTS idx_discounts_is_active ON discounts(is_active);
CREATE INDEX IF NOT EXISTS idx_discounts_start_date ON discounts(start_date);
CREATE INDEX IF NOT EXISTS idx_discounts_end_date ON discounts(end_date);
CREATE INDEX IF NOT EXISTS idx_discounts_active_period ON discounts(is_active, start_date, end_date) WHERE is_active = true;

COMMENT ON TABLE discounts IS 'Brand and category discounts table';

-- ============================================
-- 6. Update existing tables structure if needed
-- ============================================

-- Add indexes to existing tables for better performance
CREATE INDEX IF NOT EXISTS idx_cart_user_product ON cart(user_id, product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_products_brand_category ON products(brand_id, category_id);
CREATE INDEX IF NOT EXISTS idx_products_stock_quantity ON products(stock_quantity);
CREATE INDEX IF NOT EXISTS idx_products_active_stock ON products(is_active, stock_quantity) WHERE is_active = true;

-- Add product search index (for full-text search)
CREATE INDEX IF NOT EXISTS idx_products_search ON products USING gin(to_tsvector('russian', name || ' ' || COALESCE(description, '')));

-- ============================================
-- 7. Create triggers for updated_at timestamps
-- ============================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for tables with updated_at column
DO $$
    BEGIN
        -- Reviews table trigger
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_reviews_updated_at') THEN
            CREATE TRIGGER trigger_reviews_updated_at
                BEFORE UPDATE ON reviews
                FOR EACH ROW
            EXECUTE FUNCTION update_updated_at_column();
        END IF;

        -- Discounts table trigger
        IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_discounts_updated_at') THEN
            CREATE TRIGGER trigger_discounts_updated_at
                BEFORE UPDATE ON discounts
                FOR EACH ROW
            EXECUTE FUNCTION update_updated_at_column();
        END IF;
    END $$;

-- ============================================
-- 8. Create views for common queries
-- ============================================

-- View for product statistics
CREATE OR REPLACE VIEW product_stats AS
SELECT
    p.id,
    p.name,
    p.stock_quantity,
    COALESCE(AVG(r.rating), 0) as average_rating,
    COUNT(r.id) as review_count,
    COUNT(w.id) as wishlist_count,
    CASE
        WHEN p.stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN p.stock_quantity <= 10 THEN 'LOW_STOCK'
        ELSE 'IN_STOCK'
        END as stock_status
FROM products p
         LEFT JOIN reviews r ON p.id = r.product_id AND r.is_active = true
         LEFT JOIN wishlist w ON p.id = w.product_id
WHERE p.is_active = true
GROUP BY p.id, p.name, p.stock_quantity;

COMMENT ON VIEW product_stats IS 'Product statistics including ratings, reviews, and wishlist counts';

-- View for user notifications summary
CREATE OR REPLACE VIEW user_notification_summary AS
SELECT
    user_id,
    COUNT(*) as total_notifications,
    COUNT(CASE WHEN is_read = false THEN 1 END) as unread_count,
    MAX(created_at) as latest_notification
FROM notifications
GROUP BY user_id;

COMMENT ON VIEW user_notification_summary IS 'Summary of user notifications';

-- ============================================
-- 9. Insert initial data
-- ============================================

-- Insert sample notifications for admin users (will be handled by DataInitializer)
-- Insert sample discounts (will be handled by DataInitializer)

-- ============================================
-- 10. Update existing data with new pricing
-- ============================================

-- Update existing products with cosmetologist and admin prices
UPDATE products
SET
    cosmetologist_price = regular_price * 0.75,  -- 25% discount
    admin_price = regular_price * 0.60           -- 40% discount
WHERE regular_price IS NOT NULL
  AND (cosmetologist_price IS NULL OR admin_price IS NULL);

-- ============================================
-- 11. Grant permissions (adjust as needed for your setup)
-- ============================================

-- Grant permissions to application user (replace 'app_user' with your actual database user)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ============================================
-- 12. Create backup and maintenance procedures
-- ============================================

-- Function to clean old notifications (older than 3 months)
CREATE OR REPLACE FUNCTION cleanup_old_notifications()
    RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM notifications
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '3 months'
      AND is_read = true;

    GET DIAGNOSTICS deleted_count = ROW_COUNT;

    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION cleanup_old_notifications() IS 'Cleans up read notifications older than 3 months';

-- Function to update product statistics
CREATE OR REPLACE FUNCTION refresh_product_statistics()
    RETURNS VOID AS $$
BEGIN
    -- This function can be used to refresh materialized views or update cached statistics
    -- For now, it's a placeholder for future optimizations
    ANALYZE products;
    ANALYZE reviews;
    ANALYZE wishlist;
    ANALYZE notifications;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION refresh_product_statistics() IS 'Refreshes product-related statistics and analyzes tables';

-- ============================================
-- Migration completed successfully
-- ============================================

-- Insert migration record (if you have a migrations table)
-- INSERT INTO schema_migrations (version, description, executed_at)
-- VALUES ('20241228_001', 'Enhanced Arannati functionality - wishlist, notifications, reviews, discounts', CURRENT_TIMESTAMP);

COMMIT;