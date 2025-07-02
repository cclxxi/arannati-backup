package kz.arannati.arannati.config;

import kz.arannati.arannati.entity.*;
import kz.arannati.arannati.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Data initializer for populating the database with high-quality mock data
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    /**
     * Initialize database with mock data if it's empty
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting database initialization with high-quality mock data");

            // Create roles first
            createRoles();

            // Create test users
            createTestUsers();

            // Create categories
            List<Category> categories = createCategories();
            log.info("Created {} categories", categories.size());

            // Create brands
            List<Brand> brands = createBrands();
            log.info("Created {} brands", brands.size());

            // Create products
            List<Product> products = createProducts(categories, brands);
            log.info("Created {} products", products.size());

            // Create product images
            createProductImages(products);
            log.info("Created product images");

            log.info("Database initialization completed successfully");
        };
    }

    /**
     * Create roles
     */
    private void createRoles() {
        if (roleRepository.count() > 0) {
            return;
        }

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        Role cosmetologistRole = new Role();
        cosmetologistRole.setName("COSMETOLOGIST");
        roleRepository.save(cosmetologistRole);

        log.info("Created roles");
    }

    /**
     * Create test users
     */
    private void createTestUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        Role cosmetologistRole = roleRepository.findByName("COSMETOLOGIST")
                .orElseThrow(() -> new RuntimeException("Cosmetologist role not found"));

        // Admin user
        User admin = new User();
        admin.setEmail("admin@arannati.kz");
        admin.setPassword(passwordEncoder.encode("Qwerty1"));
        admin.setFirstName("Админ");
        admin.setLastName("Системы");
        admin.setPhone("+7 701 234 5678");
        admin.setRole(adminRole);
        admin.setVerified(true);
        admin.setActive(true);
        userRepository.save(admin);

        // Test user
        User testUser = new User();
        testUser.setEmail("user@test.kz");
        testUser.setPassword(passwordEncoder.encode("Qwerty1"));
        testUser.setFirstName("Тестовый");
        testUser.setLastName("Пользователь");
        testUser.setPhone("+7 701 234 5679");
        testUser.setRole(userRole);
        testUser.setVerified(true);
        testUser.setActive(true);
        userRepository.save(testUser);

        // Test cosmetologist
        User cosmetologist = new User();
        cosmetologist.setEmail("cosmetologist@test.kz");
        cosmetologist.setPassword(passwordEncoder.encode("Qwerty1"));
        cosmetologist.setFirstName("Айгерим");
        cosmetologist.setLastName("Косметолог");
        cosmetologist.setPhone("+7 701 234 5680");
        cosmetologist.setRole(cosmetologistRole);
        cosmetologist.setVerified(true);
        cosmetologist.setActive(true);
        userRepository.save(cosmetologist);

        log.info("Created test users");
    }

    /**
     * Create mock categories with professional structure
     */
    private List<Category> createCategories() {
        if (categoryRepository.count() > 0) {
            return categoryRepository.findAll();
        }

        List<Category> categories = new ArrayList<>();

        // Main categories
        Category skincare = createCategory("Уход за кожей лица", "Профессиональные средства для комплексного ухода за кожей лица", null, 1, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400");
        categories.add(skincare);

        Category bodycare = createCategory("Уход за телом", "Средства для ухода за кожей тела и рук", null, 2, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400");
        categories.add(bodycare);

        Category haircare = createCategory("Уход за волосами", "Профессиональная косметика для волос", null, 3, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400");
        categories.add(haircare);

        Category professional = createCategory("Профессиональная косметика", "Средства для профессионального применения", null, 4, "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=400");
        categories.add(professional);

        Category supplements = createCategory("БАДы и витамины", "Биологически активные добавки для красоты и здоровья", null, 5, "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400");
        categories.add(supplements);

        // Subcategories for skincare
        categories.add(createCategory("Очищение и демакияж", "Средства для снятия макияжа и очищения кожи", skincare, 1, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"));
        categories.add(createCategory("Тонизирование", "Тоники и лосьоны для подготовки кожи", skincare, 2, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400"));
        categories.add(createCategory("Сыворотки и концентраты", "Активные средства интенсивного действия", skincare, 3, "https://images.unsplash.com/photo-1608248543803-ba4f8c70ae0b?w=400"));
        categories.add(createCategory("Увлажнение и питание", "Кремы и эмульсии для увлажнения", skincare, 4, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400"));
        categories.add(createCategory("Маски для лица", "Профессиональные маски различного действия", skincare, 5, "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=400"));
        categories.add(createCategory("Солнцезащитные средства", "SPF защита для ежедневного применения", skincare, 6, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400"));

        // Subcategories for bodycare
        categories.add(createCategory("Очищение тела", "Гели для душа и очищающие средства", bodycare, 1, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"));
        categories.add(createCategory("Увлажнение тела", "Лосьоны, кремы и масла для тела", bodycare, 2, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400"));
        categories.add(createCategory("Уход за руками", "Кремы и средства для рук и ногтей", bodycare, 3, "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400"));
        categories.add(createCategory("Антицеллюлитные средства", "Специализированные средства для коррекции фигуры", bodycare, 4, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"));

        // Subcategories for haircare
        categories.add(createCategory("Шампуни", "Очищающие средства для различных типов волос", haircare, 1, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400"));
        categories.add(createCategory("Кондиционеры и бальзамы", "Средства для питания и восстановления", haircare, 2, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400"));
        categories.add(createCategory("Маски для волос", "Интенсивный уход и восстановление", haircare, 3, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400"));
        categories.add(createCategory("Средства для укладки", "Стайлинг и фиксация прически", haircare, 4, "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400"));

        return categories;
    }

    private Category createCategory(String name, String description, Category parent, int sortOrder, String imagePath) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        category.setActive(true);
        category.setSortOrder(sortOrder);
        category.setImagePath(imagePath);
        return categoryRepository.save(category);
    }

    /**
     * Create professional cosmetic brands
     */
    private List<Brand> createBrands() {
        if (brandRepository.count() > 0) {
            return brandRepository.findAll();
        }

        List<Brand> brands = new ArrayList<>();

        brands.add(createBrand("Holy Land", "Израильская профессиональная косметика премиум класса", "Израиль", "https://holyland-cosmetics.com", "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=100", 1));
        brands.add(createBrand("Christina", "Инновационная израильская косметика для профессионального ухода", "Израиль", "https://christina-cosmetics.com", "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=100", 2));
        brands.add(createBrand("Janssen Cosmetics", "Немецкая профессиональная косметика с научным подходом", "Германия", "https://janssen-cosmetics.com", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=100", 3));
        brands.add(createBrand("Mesoestetic", "Испанская медицинская косметика", "Испания", "https://mesoestetic.com", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=100", 4));
        brands.add(createBrand("Dermalogica", "Американская профессиональная косметика", "США", "https://dermalogica.com", "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=100", 5));
        brands.add(createBrand("Babor", "Немецкая роскошная косметика", "Германия", "https://babor.com", "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=100", 6));
        brands.add(createBrand("Algologie", "Французская косметика на основе морских водорослей", "Франция", "https://algologie.com", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=100", 7));
        brands.add(createBrand("Phyto", "Французская фитокосметика для волос", "Франция", "https://phyto.com", "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=100", 8));

        return brands;
    }

    private Brand createBrand(String name, String description, String country, String website, String logoPath, int sortOrder) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setDescription(description);
        brand.setCountry(country);
        brand.setWebsiteUrl(website);
        brand.setLogoPath(logoPath);
        brand.setActive(true);
        brand.setSortOrder(sortOrder);
        return brandRepository.save(brand);
    }

    /**
     * Create realistic products with proper pricing
     */
    private List<Product> createProducts(List<Category> categories, List<Brand> brands) {
        if (productRepository.count() > 0) {
            return productRepository.findAll();
        }

        List<Product> products = new ArrayList<>();

        // Professional product names by category
        String[][] productsByCategory = {
                // Cleansers
                {"Мицеллярная вода", "Очищающий гель", "Пенка для умывания", "Молочко для снятия макияжа", "Двухфазное средство для демакияжа"},
                // Toners
                {"Увлажняющий тоник", "Балансирующий лосьон", "Тоник с АНА кислотами", "Успокаивающий тоник", "Тоник для проблемной кожи"},
                // Serums
                {"Сыворотка с витамином С", "Сыворотка с гиалуроновой кислотой", "Сыворотка с ретинолом", "Антиоксидантная сыворотка", "Сыворотка для сияния"},
                // Moisturizers
                {"Увлажняющий крем", "Питательный крем", "Крем для чувствительной кожи", "Антивозрастной крем", "Крем для проблемной кожи"},
                // Masks
                {"Альгинатная маска", "Глиняная маска", "Гидрогелевая маска", "Кислородная маска", "Коллагеновая маска"},
                // Sunscreen
                {"Солнцезащитный крем SPF 30", "Солнцезащитный крем SPF 50", "Флюид с SPF", "Тонирующий крем с SPF"},
                // Body cleansers
                {"Гель для душа", "Скраб для тела", "Мыло для тела", "Очищающее масло"},
                // Body moisturizers
                {"Лосьон для тела", "Крем для тела", "Масло для тела", "Молочко для тела"},
                // Hand care
                {"Крем для рук", "Маска для рук", "Скраб для рук", "Крем для кутикулы"},
                // Anti-cellulite
                {"Антицеллюлитный крем", "Дренажный гель", "Термогель", "Лимфодренажный крем"},
                // Shampoos
                {"Шампунь для сухих волос", "Шампунь для жирных волос", "Шампунь против перхоти", "Шампунь для окрашенных волос"},
                // Conditioners
                {"Кондиционер восстанавливающий", "Бальзам увлажняющий", "Кондиционер для объема", "Кондиционер для блеска"},
                // Hair masks
                {"Маска восстанавливающая", "Маска питательная", "Маска для роста волос", "Маска с кератином"},
                // Styling
                {"Мусс для укладки", "Лак для волос", "Гель для укладки", "Воск для волос"}
        };

        String[] descriptions = {
                "Профессиональное средство с инновационной формулой для интенсивного ухода. Содержит активные компоненты последнего поколения.",
                "Эффективное решение для профессионального применения. Клинически протестировано дерматологами.",
                "Концентрированная формула с высоким содержанием активных ингредиентов для видимого результата.",
                "Роскошное средство премиум-класса с натуральными экстрактами и инновационными пептидами.",
                "Профессиональный продукт для салонного применения с доказанной эффективностью."
        };

        // Get subcategories (not parent categories)
        List<Category> subcategories = categories.stream()
                .filter(cat -> cat.getParent() != null)
                .toList();

        int productIndex = 0;

        for (Category category : subcategories) {
            String[] categoryProducts = getProductNamesForCategory(category.getName(), productsByCategory);

            for (Brand brand : brands) {
                // Create 2-3 products per brand-category combination
                int numProducts = 2 + random.nextInt(2);

                for (int i = 0; i < numProducts && i < categoryProducts.length; i++) {
                    Product product = new Product();

                    // Basic info
                    product.setName(brand.getName() + " " + categoryProducts[i]);
                    product.setDescription(descriptions[random.nextInt(descriptions.length)]);
                    product.setShortDescription(categoryProducts[i] + " от " + brand.getName());
                    product.setSku(generateSku(brand, category, productIndex++));

                    // Relationships
                    product.setCategory(category);
                    product.setBrand(brand);

                    // Pricing with realistic professional cosmetics prices
                    BigDecimal basePrice = generateRealisticPrice(category.getName());
                    product.setRegularPrice(basePrice);

                    // Professional pricing (brands from Israel, Germany, USA - higher prices)
                    if (brand.getCountry().equals("Израиль") || brand.getCountry().equals("Германия") || brand.getCountry().equals("США")) {
                        product.setRegularPrice(basePrice.multiply(BigDecimal.valueOf(1.3)));
                    }

                    // 20% of products have sale price
                    if (random.nextInt(5) == 0) {
                        int discountPercent = 10 + random.nextInt(20);
                        BigDecimal salePrice = product.getRegularPrice()
                                .multiply(BigDecimal.valueOf(1 - discountPercent / 100.0))
                                .setScale(0, RoundingMode.DOWN);
                        product.setSalePrice(salePrice);
                    }

                    // Cosmetologist price (25% discount)
                    BigDecimal cosmetologistPrice = product.getRegularPrice()
                            .multiply(BigDecimal.valueOf(0.75))
                            .setScale(0, RoundingMode.DOWN);
                    product.setCosmetologistPrice(cosmetologistPrice);

                    // Admin price (40% discount) - for future use
                    BigDecimal adminPrice = product.getRegularPrice()
                            .multiply(BigDecimal.valueOf(0.60))
                            .setScale(0, RoundingMode.DOWN);
                    // Not setting this to salePrice as it would override the conditional sale price logic above

                    // Stock and other properties
                    product.setStockQuantity(50 + random.nextInt(200));
                    product.setActive(true);
                    product.setSortOrder(i + 1);
                    product.setWeight(BigDecimal.valueOf(50 + random.nextInt(500)));
                    // SKU is already set above using generateSku method

                    // Set professional flag based on category or parent category
                    boolean isProfessional = category.getName().contains("Профессиональная") || 
                            (category.getParent() != null && category.getParent().getName().contains("Профессиональная"));
                    product.setProfessional(isProfessional);

                    products.add(productRepository.save(product));
                }
            }
        }

        return products;
    }

    private String[] getProductNamesForCategory(String categoryName, String[][] productsByCategory) {
        // Map category names to product arrays
        if (categoryName.contains("Очищение") || categoryName.contains("демакияж")) return productsByCategory[0];
        if (categoryName.contains("Тонизирование")) return productsByCategory[1];
        if (categoryName.contains("Сыворотки")) return productsByCategory[2];
        if (categoryName.contains("Увлажнение") || categoryName.contains("питание")) return productsByCategory[3];
        if (categoryName.contains("Маски для лица")) return productsByCategory[4];
        if (categoryName.contains("Солнцезащитные")) return productsByCategory[5];
        if (categoryName.contains("Очищение тела")) return productsByCategory[6];
        if (categoryName.contains("Увлажнение тела")) return productsByCategory[7];
        if (categoryName.contains("Уход за руками")) return productsByCategory[8];
        if (categoryName.contains("Антицеллюлитные")) return productsByCategory[9];
        if (categoryName.contains("Шампуни")) return productsByCategory[10];
        if (categoryName.contains("Кондиционеры")) return productsByCategory[11];
        if (categoryName.contains("Маски для волос")) return productsByCategory[12];
        if (categoryName.contains("укладки")) return productsByCategory[13];

        return productsByCategory[0]; // Default to cleansers
    }

    private BigDecimal generateRealisticPrice(String categoryName) {
        // Professional cosmetics pricing by category
        if (categoryName.contains("Сыворотки") || categoryName.contains("концентраты")) {
            return BigDecimal.valueOf(8000 + random.nextInt(15000)); // 8,000 - 23,000
        } else if (categoryName.contains("крем") || categoryName.contains("Увлажнение")) {
            return BigDecimal.valueOf(5000 + random.nextInt(12000)); // 5,000 - 17,000
        } else if (categoryName.contains("Маски")) {
            return BigDecimal.valueOf(6000 + random.nextInt(10000)); // 6,000 - 16,000
        } else if (categoryName.contains("Очищение") || categoryName.contains("Тонизирование")) {
            return BigDecimal.valueOf(3000 + random.nextInt(8000)); // 3,000 - 11,000
        } else if (categoryName.contains("Шампуни") || categoryName.contains("Кондиционеры")) {
            return BigDecimal.valueOf(2500 + random.nextInt(6000)); // 2,500 - 8,500
        } else if (categoryName.contains("Солнцезащитные")) {
            return BigDecimal.valueOf(4000 + random.nextInt(8000)); // 4,000 - 12,000
        } else {
            return BigDecimal.valueOf(3500 + random.nextInt(7000)); // 3,500 - 10,500
        }
    }

    /**
     * Create high-quality product images
     */
    private void createProductImages(List<Product> products) {
        if (productImageRepository.count() > 0) {
            return;
        }

        // High-quality cosmetic product images from Unsplash
        String[] cosmeticImages = {
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500",
                "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500",
                "https://images.unsplash.com/photo-1596755389378-c31d21fd1273?w=500",
                "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500",
                "https://images.unsplash.com/photo-1608248543803-ba4f8c70ae0b?w=500",
                "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=500",
                "https://images.unsplash.com/photo-1570194065650-d99fb4bedf0a?w=500",
                "https://images.unsplash.com/photo-1576426863848-c21f53c60b19?w=500",
                "https://images.unsplash.com/photo-1559056961-84c6f8b22965?w=500",
                "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=500"
        };

        for (Product product : products) {
            // Create 1-3 images per product
            int numImages = 1 + random.nextInt(3);

            for (int i = 0; i < numImages; i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImagePath(cosmeticImages[random.nextInt(cosmeticImages.length)]);
                image.setAltText(product.getName());
                image.setMain(i == 0); // First image is main
                image.setSortOrder(i + 1);

                productImageRepository.save(image);
            }
        }
    }

    /**
     * Generate a unique SKU for a product
     */
    private String generateSku(Brand brand, Category category, int index) {
        String brandCode = brand.getName().substring(0, Math.min(3, brand.getName().length())).toUpperCase();
        String categoryCode = category.getName().substring(0, Math.min(3, category.getName().length())).toUpperCase();
        String indexCode = String.format("%04d", index + 1);

        return brandCode + "-" + categoryCode + "-" + indexCode;
    }
}
