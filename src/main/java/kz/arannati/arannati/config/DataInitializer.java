package kz.arannati.arannati.config;

import kz.arannati.arannati.entity.*;
import kz.arannati.arannati.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data initializer for populating the database with mock data
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final Random random = new Random();

    /**
     * Initialize database with mock data if it's empty
     */
    @Bean
    @Profile("!test") // Don't run in test profile
    public CommandLineRunner initDatabase() {
        return args -> {
            // Check if database is already populated
            if (productRepository.count() > 0) {
                log.info("Database already contains products, skipping initialization");
                return;
            }

            log.info("Starting database initialization with mock data");

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
     * Create mock categories
     */
    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();

        // Main categories
        Category skincare = new Category();
        skincare.setName("Уход за кожей");
        skincare.setDescription("Средства для ухода за кожей лица и тела");
        skincare.setActive(true);
        skincare.setSortOrder(1);
        skincare.setImagePath("/static/images/categories/skincare.jpg");
        categories.add(categoryRepository.save(skincare));

        Category haircare = new Category();
        haircare.setName("Уход за волосами");
        haircare.setDescription("Средства для ухода за волосами");
        haircare.setActive(true);
        haircare.setSortOrder(2);
        haircare.setImagePath("/static/images/categories/haircare.jpg");
        categories.add(categoryRepository.save(haircare));

        Category bodycare = new Category();
        bodycare.setName("Уход за телом");
        bodycare.setDescription("Средства для ухода за телом");
        bodycare.setActive(true);
        bodycare.setSortOrder(3);
        bodycare.setImagePath("/static/images/categories/bodycare.jpg");
        categories.add(categoryRepository.save(bodycare));

        Category supplements = new Category();
        supplements.setName("БАДы");
        supplements.setDescription("Биологически активные добавки");
        supplements.setActive(true);
        supplements.setSortOrder(4);
        supplements.setImagePath("/static/images/categories/supplements.jpg");
        categories.add(categoryRepository.save(supplements));

        // Subcategories for skincare
        Category cleansers = new Category();
        cleansers.setName("Очищение");
        cleansers.setDescription("Средства для очищения кожи");
        cleansers.setActive(true);
        cleansers.setSortOrder(1);
        cleansers.setParent(skincare);
        cleansers.setImagePath("/static/images/categories/cleansers.jpg");
        categories.add(categoryRepository.save(cleansers));

        Category serums = new Category();
        serums.setName("Сыворотки");
        serums.setDescription("Концентрированные средства для интенсивного ухода");
        serums.setActive(true);
        serums.setSortOrder(2);
        serums.setParent(skincare);
        serums.setImagePath("/static/images/categories/serums.jpg");
        categories.add(categoryRepository.save(serums));

        Category moisturizers = new Category();
        moisturizers.setName("Увлажнение");
        moisturizers.setDescription("Средства для увлажнения кожи");
        moisturizers.setActive(true);
        moisturizers.setSortOrder(3);
        moisturizers.setParent(skincare);
        moisturizers.setImagePath("/static/images/categories/moisturizers.jpg");
        categories.add(categoryRepository.save(moisturizers));

        Category masks = new Category();
        masks.setName("Маски");
        masks.setDescription("Маски для интенсивного ухода за кожей");
        masks.setActive(true);
        masks.setSortOrder(4);
        masks.setParent(skincare);
        masks.setImagePath("/static/images/categories/masks.jpg");
        categories.add(categoryRepository.save(masks));

        // Subcategories for haircare
        Category shampoos = new Category();
        shampoos.setName("Шампуни");
        shampoos.setDescription("Средства для мытья волос");
        shampoos.setActive(true);
        shampoos.setSortOrder(1);
        shampoos.setParent(haircare);
        shampoos.setImagePath("/static/images/categories/shampoos.jpg");
        categories.add(categoryRepository.save(shampoos));

        Category conditioners = new Category();
        conditioners.setName("Кондиционеры");
        conditioners.setDescription("Средства для ухода за волосами после мытья");
        conditioners.setActive(true);
        conditioners.setSortOrder(2);
        conditioners.setParent(haircare);
        conditioners.setImagePath("/static/images/categories/conditioners.jpg");
        categories.add(categoryRepository.save(conditioners));

        // Subcategories for bodycare
        Category lotions = new Category();
        lotions.setName("Лосьоны");
        lotions.setDescription("Средства для увлажнения тела");
        lotions.setActive(true);
        lotions.setSortOrder(1);
        lotions.setParent(bodycare);
        lotions.setImagePath("/static/images/categories/lotions.jpg");
        categories.add(categoryRepository.save(lotions));

        Category scrubs = new Category();
        scrubs.setName("Скрабы");
        scrubs.setDescription("Средства для отшелушивания кожи");
        scrubs.setActive(true);
        scrubs.setSortOrder(2);
        scrubs.setParent(bodycare);
        scrubs.setImagePath("/static/images/categories/scrubs.jpg");
        categories.add(categoryRepository.save(scrubs));

        // Subcategories for supplements
        Category vitamins = new Category();
        vitamins.setName("Витамины");
        vitamins.setDescription("Витаминные комплексы");
        vitamins.setActive(true);
        vitamins.setSortOrder(1);
        vitamins.setParent(supplements);
        vitamins.setImagePath("/static/images/categories/vitamins.jpg");
        categories.add(categoryRepository.save(vitamins));

        Category minerals = new Category();
        minerals.setName("Минералы");
        minerals.setDescription("Минеральные комплексы");
        minerals.setActive(true);
        minerals.setSortOrder(2);
        minerals.setParent(supplements);
        minerals.setImagePath("/static/images/categories/minerals.jpg");
        categories.add(categoryRepository.save(minerals));

        return categories;
    }

    /**
     * Create mock brands
     */
    private List<Brand> createBrands() {
        List<Brand> brands = new ArrayList<>();

        Brand brand1 = new Brand();
        brand1.setName("NatureCare");
        brand1.setDescription("Натуральная косметика для всех типов кожи");
        brand1.setCountry("Франция");
        brand1.setWebsiteUrl("https://naturecare.com");
        brand1.setLogoPath("/static/images/brands/naturecare.jpg");
        brand1.setActive(true);
        brand1.setSortOrder(1);
        brands.add(brandRepository.save(brand1));

        Brand brand2 = new Brand();
        brand2.setName("PureSkin");
        brand2.setDescription("Профессиональная косметика для проблемной кожи");
        brand2.setCountry("Швейцария");
        brand2.setWebsiteUrl("https://pureskin.com");
        brand2.setLogoPath("/static/images/brands/pureskin.jpg");
        brand2.setActive(true);
        brand2.setSortOrder(2);
        brands.add(brandRepository.save(brand2));

        Brand brand3 = new Brand();
        brand3.setName("VitaPlus");
        brand3.setDescription("Витаминные комплексы для красоты и здоровья");
        brand3.setCountry("США");
        brand3.setWebsiteUrl("https://vitaplus.com");
        brand3.setLogoPath("/static/images/brands/vitaplus.jpg");
        brand3.setActive(true);
        brand3.setSortOrder(3);
        brands.add(brandRepository.save(brand3));

        Brand brand4 = new Brand();
        brand4.setName("HairLuxe");
        brand4.setDescription("Премиальные средства для ухода за волосами");
        brand4.setCountry("Италия");
        brand4.setWebsiteUrl("https://hairluxe.com");
        brand4.setLogoPath("/static/images/brands/hairluxe.jpg");
        brand4.setActive(true);
        brand4.setSortOrder(4);
        brands.add(brandRepository.save(brand4));

        Brand brand5 = new Brand();
        brand5.setName("BodyGlow");
        brand5.setDescription("Средства для ухода за телом");
        brand5.setCountry("Германия");
        brand5.setWebsiteUrl("https://bodyglow.com");
        brand5.setLogoPath("/static/images/brands/bodyglow.jpg");
        brand5.setActive(true);
        brand5.setSortOrder(5);
        brands.add(brandRepository.save(brand5));

        return brands;
    }

    /**
     * Create mock products
     */
    private List<Product> createProducts(List<Category> categories, List<Brand> brands) {
        List<Product> products = new ArrayList<>();
        
        // Product names
        String[] productNames = {
            "Очищающий гель", "Мицеллярная вода", "Тоник для лица", "Пенка для умывания",
            "Сыворотка с витамином C", "Сыворотка с гиалуроновой кислотой", "Антивозрастная сыворотка", "Сыворотка для проблемной кожи",
            "Увлажняющий крем", "Ночной крем", "Крем для глаз", "Солнцезащитный крем",
            "Маска для лица", "Глиняная маска", "Тканевая маска", "Ночная маска",
            "Шампунь для объема", "Шампунь для окрашенных волос", "Увлажняющий шампунь", "Шампунь против перхоти",
            "Кондиционер для волос", "Маска для волос", "Спрей для волос", "Масло для волос",
            "Лосьон для тела", "Масло для тела", "Скраб для тела", "Гель для душа",
            "Мультивитамины", "Витамин D3", "Омега-3", "Коллаген",
            "Магний", "Цинк", "Кальций", "Железо"
        };
        
        // Product descriptions
        String[] productDescriptions = {
            "Мягко очищает кожу, не нарушая ее естественный баланс. Подходит для ежедневного использования.",
            "Интенсивно увлажняет и питает кожу, восстанавливая ее естественный барьер. Содержит натуральные масла и экстракты.",
            "Обогащен витаминами и антиоксидантами, которые защищают кожу от негативного воздействия окружающей среды.",
            "Профессиональное средство для интенсивного ухода. Видимый результат уже после первого применения.",
            "Содержит комплекс активных ингредиентов, которые эффективно борются с признаками старения.",
            "Разработано с использованием передовых технологий для достижения максимального эффекта.",
            "Натуральный состав без парабенов, сульфатов и искусственных красителей. Подходит для чувствительной кожи.",
            "Клинически доказанная эффективность. Рекомендовано ведущими дерматологами."
        };
        
        // Ingredients
        String[] ingredients = {
            "Aqua, Glycerin, Sodium Laureth Sulfate, Cocamidopropyl Betaine, Sodium Chloride, Citric Acid, Parfum, Sodium Benzoate, Potassium Sorbate",
            "Aqua, Glycerin, Niacinamide, Panthenol, Sodium Hyaluronate, Tocopheryl Acetate, Allantoin, Xanthan Gum, Phenoxyethanol, Ethylhexylglycerin",
            "Aqua, Butylene Glycol, Glycerin, Niacinamide, Sodium Hyaluronate, Adenosine, Hydrolyzed Collagen, Centella Asiatica Extract, Phenoxyethanol",
            "Aqua, Caprylic/Capric Triglyceride, Glycerin, Cetearyl Alcohol, Ceteareth-20, Dimethicone, Tocopheryl Acetate, Phenoxyethanol, Parfum",
            "Vitamin C (Ascorbic Acid), Vitamin E (Tocopherol), Hyaluronic Acid, Collagen, Coenzyme Q10, Retinol, Green Tea Extract, Aloe Vera Extract",
            "Calcium, Magnesium, Zinc, Iron, Vitamin D3, Vitamin B12, Folic Acid, Biotin, Selenium, Copper, Manganese, Chromium"
        };
        
        // Usage instructions
        String[] usageInstructions = {
            "Нанесите небольшое количество средства на влажную кожу, массируйте круговыми движениями, затем смойте теплой водой.",
            "Нанесите 2-3 капли средства на очищенную кожу лица, шеи и декольте. Используйте утром и вечером для достижения наилучших результатов.",
            "Нанесите тонким слоем на очищенную кожу лица и шеи. Используйте 1-2 раза в день.",
            "Нанесите на влажные волосы, массируйте, затем тщательно смойте. При необходимости повторите.",
            "Принимайте по 1 капсуле в день во время еды. Запивайте достаточным количеством воды.",
            "Нанесите на очищенную кожу лица, избегая области вокруг глаз. Оставьте на 15-20 минут, затем смойте теплой водой."
        };

        // Create products for each category and brand
        for (Category category : categories) {
            // Skip parent categories
            if (category.getParent() == null) {
                continue;
            }
            
            for (Brand brand : brands) {
                // Create 2-4 products for each category-brand combination
                int numProducts = 2 + random.nextInt(3);
                
                for (int i = 0; i < numProducts; i++) {
                    Product product = new Product();
                    
                    // Basic info
                    String productName = brand.getName() + " " + productNames[random.nextInt(productNames.length)];
                    product.setName(productName);
                    product.setDescription(productDescriptions[random.nextInt(productDescriptions.length)]);
                    product.setShortDescription(productName + " - " + category.getName());
                    product.setSku(generateSku(brand, category, i));
                    
                    // Relationships
                    product.setCategory(category);
                    product.setBrand(brand);
                    
                    // Pricing
                    BigDecimal regularPrice = new BigDecimal(1000 + random.nextInt(9000));
                    product.setRegularPrice(regularPrice);
                    
                    // 30% of products have sale price
                    if (random.nextInt(10) < 3) {
                        int discountPercent = 10 + random.nextInt(30);
                        BigDecimal salePrice = regularPrice.multiply(BigDecimal.valueOf(1 - discountPercent / 100.0))
                                .setScale(0, BigDecimal.ROUND_DOWN);
                        product.setSalePrice(salePrice);
                    }
                    
                    // Cosmetologist price (20% off regular price)
                    BigDecimal cosmetologistPrice = regularPrice.multiply(BigDecimal.valueOf(0.8))
                            .setScale(0, BigDecimal.ROUND_DOWN);
                    product.setCosmetologistPrice(cosmetologistPrice);
                    
                    // Other attributes
                    product.setProfessional(random.nextInt(10) < 3); // 30% are professional
                    product.setActive(true);
                    product.setStockQuantity(10 + random.nextInt(90));
                    
                    // Physical attributes
                    product.setWeight(BigDecimal.valueOf(50 + random.nextInt(450)));
                    product.setDimensions((5 + random.nextInt(15)) + "x" + (5 + random.nextInt(15)) + "x" + (5 + random.nextInt(15)) + " см");
                    
                    // Product details
                    product.setIngredients(ingredients[random.nextInt(ingredients.length)]);
                    product.setUsageInstructions(usageInstructions[random.nextInt(usageInstructions.length)]);
                    
                    // Metadata
                    product.setSortOrder(i + 1);
                    
                    products.add(productRepository.save(product));
                }
            }
        }
        
        return products;
    }

    /**
     * Create mock product images
     */
    private void createProductImages(List<Product> products) {
        // Image paths for different categories
        String[][] categoryImages = {
            // Cleansers
            {
                "/static/images/products/cleanser1.jpg",
                "/static/images/products/cleanser2.jpg",
                "/static/images/products/cleanser3.jpg"
            },
            // Serums
            {
                "/static/images/products/serum1.jpg",
                "/static/images/products/serum2.jpg",
                "/static/images/products/serum3.jpg"
            },
            // Moisturizers
            {
                "/static/images/products/moisturizer1.jpg",
                "/static/images/products/moisturizer2.jpg",
                "/static/images/products/moisturizer3.jpg"
            },
            // Masks
            {
                "/static/images/products/mask1.jpg",
                "/static/images/products/mask2.jpg",
                "/static/images/products/mask3.jpg"
            },
            // Shampoos
            {
                "/static/images/products/shampoo1.jpg",
                "/static/images/products/shampoo2.jpg",
                "/static/images/products/shampoo3.jpg"
            },
            // Conditioners
            {
                "/static/images/products/conditioner1.jpg",
                "/static/images/products/conditioner2.jpg",
                "/static/images/products/conditioner3.jpg"
            },
            // Lotions
            {
                "/static/images/products/lotion1.jpg",
                "/static/images/products/lotion2.jpg",
                "/static/images/products/lotion3.jpg"
            },
            // Scrubs
            {
                "/static/images/products/scrub1.jpg",
                "/static/images/products/scrub2.jpg",
                "/static/images/products/scrub3.jpg"
            },
            // Vitamins
            {
                "/static/images/products/vitamin1.jpg",
                "/static/images/products/vitamin2.jpg",
                "/static/images/products/vitamin3.jpg"
            },
            // Minerals
            {
                "/static/images/products/mineral1.jpg",
                "/static/images/products/mineral2.jpg",
                "/static/images/products/mineral3.jpg"
            }
        };
        
        for (Product product : products) {
            // Determine which category images to use
            String[] images;
            String categoryName = product.getCategory().getName();
            
            if (categoryName.contains("Очищение")) {
                images = categoryImages[0];
            } else if (categoryName.contains("Сыворотки")) {
                images = categoryImages[1];
            } else if (categoryName.contains("Увлажнение")) {
                images = categoryImages[2];
            } else if (categoryName.contains("Маски") && product.getCategory().getParent().getName().contains("кожей")) {
                images = categoryImages[3];
            } else if (categoryName.contains("Шампуни")) {
                images = categoryImages[4];
            } else if (categoryName.contains("Кондиционеры")) {
                images = categoryImages[5];
            } else if (categoryName.contains("Лосьоны")) {
                images = categoryImages[6];
            } else if (categoryName.contains("Скрабы")) {
                images = categoryImages[7];
            } else if (categoryName.contains("Витамины")) {
                images = categoryImages[8];
            } else if (categoryName.contains("Минералы")) {
                images = categoryImages[9];
            } else {
                // Default to first category
                images = categoryImages[0];
            }
            
            // Create 1-3 images for each product
            int numImages = 1 + random.nextInt(3);
            for (int i = 0; i < numImages; i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImagePath(images[i % images.length]);
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
        String brandCode = brand.getName().substring(0, 3).toUpperCase();
        String categoryCode = category.getName().substring(0, 3).toUpperCase();
        String indexCode = String.format("%03d", index + 1);
        
        return brandCode + "-" + categoryCode + "-" + indexCode;
    }
}