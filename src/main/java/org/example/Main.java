package org.example;

import org.postgresql.util.OSUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class Main {
    // Метод для зчитування конфігурації
    public static Properties readConfiguration(String fileName) {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                System.out.println("Не вдалось знайти файл " + fileName);
                return null;
            }
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    // Метод для управління клієнтами
    private static void manageClients(ClientService clientService, Scanner scanner, Properties prop) {
        boolean exit = false;

        while (!exit) {
            System.out.println("----- Управління клієнтами -----");
            System.out.println("1. Додати клієнта");
            System.out.println("2. Отримати клієнта за ID");
            System.out.println("3. Оновити клієнта");
            System.out.println("4. Видалити клієнта");
            System.out.println("5. Показати всіх клієнтів");
            System.out.println("0. Назад");
            System.out.print("Виберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // очищаємо буфер

            switch (choice) {
                case 1:
                    System.out.print("Введіть ім'я та прізвище клієнта: ");
                    String clientName = scanner.nextLine();
                    System.out.print("Введіть дату народження (yyyy-mm-dd): ");
                    String birthDate = scanner.nextLine();
                    clientService.addClient(clientName, Date.valueOf(birthDate), prop);
                    break;
                case 2:
                    System.out.print("Введіть ID клієнта: ");
                    int clientId = scanner.nextInt();
                    clientService.getClientById(clientId, prop);
                    break;
                case 3:
                    System.out.print("Введіть ID клієнта для оновлення: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // очищаємо буфер
                    System.out.print("Введіть нове ім'я клієнта: ");
                    String newClientName = scanner.nextLine();
                    System.out.print("Введіть нову дату народження (yyyy-mm-dd): ");
                    String newBirthDate = scanner.nextLine();
                    Client client = new Client(updateId, newClientName, LocalDate.parse(newBirthDate));
                    clientService.updateClient(client, prop);
                    break;
                case 4:
                    System.out.print("Введіть ID клієнта для видалення: ");
                    int deleteId = scanner.nextInt();
                    clientService.deleteClient(deleteId, prop);
                    break;
                case 5:
                    clientService.getAllClients(prop);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }

    private static void manageEmployees(EmployeeService employeeService, Scanner scanner, Properties prop) {
        System.out.println("----- Управління працівниками -----");

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Додати працівника");
            System.out.println("2. Отримати працівника за ID");
            System.out.println("3. Оновити працівника");
            System.out.println("4. Видалити працівника");
            System.out.println("5. Показати всіх працівників");
            System.out.println("0. Назад");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // очищає буфер вводу

            switch (choice) {
                case 1:
                    System.out.print("Введіть ім'я працівника: ");
                    String employeeName = scanner.nextLine();
                    System.out.print("Введіть дату народження (yyyy-mm-dd): ");
                    String employeeBirthDate = scanner.nextLine();
                    employeeService.addEmployee(employeeName, Date.valueOf(employeeBirthDate), prop);
                    break;
                case 2:
                    System.out.print("Введіть ID працівника: ");
                    int employeeId = scanner.nextInt();
                    employeeService.getEmployee(employeeId, prop);
                    break;
                case 3:
                    System.out.print("Введіть ID працівника для оновлення: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // очищає буфер вводу
                    System.out.print("Введіть нове ім'я працівника: ");
                    String newEmployeeName = scanner.nextLine();
                    System.out.print("Введіть нову дату народження (yyyy-mm-dd): ");
                    String newEmployeeBirthDate = scanner.nextLine();
                    Employee employee = new Employee(updateId, newEmployeeName, LocalDate.parse(newEmployeeBirthDate));
                    employeeService.updateEmployee(employee, prop);
                    break;
                case 4:
                    System.out.print("Введіть ID працівника для видалення: ");
                    int deleteId = scanner.nextInt();
                    employeeService.deleteEmployee(deleteId, prop);
                    break;
                case 5:
                    employeeService.getAllEmployee(prop);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }

    private static void manageDishes(DishService dishService, Scanner scanner, Properties prop) {
        System.out.println("----- Управління стравами -----");

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Додати страву");
            System.out.println("2. Отримати страву за назвою");
            System.out.println("3. Оновити страву");
            System.out.println("4. Видалити страву");
            System.out.println("5. Показати всі страви");
            System.out.println("0. Назад");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // очищає буфер вводу

            switch (choice) {
                case 1:
                    System.out.print("Введіть тип страви: ");
                    String dishType = scanner.nextLine();
                    System.out.print("Введіть назву страви: ");
                    String dishName = scanner.nextLine();
                    System.out.print("Введіть ціну страви: ");
                    BigDecimal price = scanner.nextBigDecimal();
                    System.out.print("Введіть термін придатності (yyyy-mm-dd): ");
                    String dateInput = scanner.nextLine();
                    dishService.addDish(dishType, dishName, price, Date.valueOf(dateInput), prop);
                    break;
                case 2:
                    System.out.print("Введіть назву страви для отримання: ");
                    String dishNameToGet = scanner.nextLine();
                    dishService.getDishByName(dishNameToGet, prop);
                    break;
                case 3:
                    System.out.print("Введіть ID страви для оновлення: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // очищає буфер вводу
                    System.out.print("Введіть новий тип страви: ");
                    String newDishType = scanner.nextLine();
                    System.out.print("Введіть нову назву страви: ");
                    String newDishName = scanner.nextLine();
                    System.out.print("Введіть нову ціну страви: ");
                    BigDecimal newPrice = scanner.nextBigDecimal();
                    System.out.print("Введіть новий термін придатності (yyyy-mm-dd): ");
                    String newDateInput = scanner.nextLine();
                    Dish dish = new Dish(updateId, newDishType, newDishName, newPrice, LocalDate.parse(newDateInput));
                    dishService.updateDish(dish, prop);
                    break;
                case 4:
                    System.out.print("Введіть ID страви для видалення: ");
                    int deleteId = scanner.nextInt();
                    dishService.deleteDish(deleteId, prop);
                    break;
                case 5:
                    dishService.getAllDishes(prop);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void manageFavorites(FavoriteService favoriteService, Scanner scanner, Properties prop) {
        System.out.println("----- Управління улюбленими стравами -----");

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Додати улюблену страву");
            System.out.println("2. Отримати улюблені страви клієнта");
            System.out.println("3. Оновити улюблену страву");
            System.out.println("4. Видалити улюблену страву");
            System.out.println("5. Отримати всіх клієнтів з улюбленими стравами");
            System.out.println("6. Вийти");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очищення буфера вводу

            switch (choice) {
                case 1:
                    // Додати улюблену страву
                    System.out.print("Введіть ID клієнта: ");
                    int clientIdToAdd = scanner.nextInt();
                    scanner.nextLine(); // Очищення буфера вводу
                    System.out.print("Введіть назву страви: ");
                    String dishNameToAdd = scanner.nextLine();
                    favoriteService.addFavorite(clientIdToAdd, dishNameToAdd, prop);
                    break;

                case 2:
                    // Отримати улюблені страви клієнта
                    System.out.print("Введіть ID клієнта: ");
                    int clientIdToGet = scanner.nextInt();
                    favoriteService.getFavoriteByClientID(clientIdToGet, prop);
                    break;

                case 3:
                    // Оновити улюблену страву
                    System.out.print("Введіть ID клієнта: ");
                    int clientIdToUpdate = scanner.nextInt();
                    scanner.nextLine(); // Очищення буфера вводу
                    System.out.print("Введіть назву старої страви: ");
                    String oldDishName = scanner.nextLine();
                    System.out.print("Введіть назву нової страви: ");
                    String newDishName = scanner.nextLine();
                    favoriteService.updateFavorite(clientIdToUpdate, oldDishName, newDishName, prop);
                    break;

                case 4:
                    // Видалити улюблену страву
                    System.out.print("Введіть ім'я клієнта: ");
                    String clientNameToRemove = scanner.nextLine();
                    System.out.print("Введіть назву страви для видалення: ");
                    String dishNameToRemove = scanner.nextLine();
                    favoriteService.removeFavorite(clientNameToRemove, dishNameToRemove, prop);
                    break;

                case 5:
                    // Отримати всіх клієнтів з улюбленими стравами
                    favoriteService.getAllClientsWithFavorites(prop);
                    break;

                case 6:
                    // Вихід з меню
                    exit = true;
                    break;

                default:
                    System.out.println("Невірний вибір, будь ласка, спробуйте ще раз.");
                    break;
            }
        }
    }

    private static void manageProducts(ProductService productService, Scanner scanner, Properties prop) {
        System.out.println("----- Управління продуктами -----");

        while (true) {
            System.out.println("1. Додати продукт");
            System.out.println("2. Отримати продукт за ID");
            System.out.println("3. Оновити продукт");
            System.out.println("4. Видалити продукт");
            System.out.println("5. Отримати всі продукти");
            System.out.println("6. Вийти");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очищення буфера вводу

            switch (choice) {
                case 1:
                    // Додати продукт
                    System.out.print("Введіть назву продукту: ");
                    String productNameToAdd = scanner.nextLine();
                    System.out.print("Введіть термін придатності продукту (yyyy-mm-dd): ");
                    String expiryDateInput = scanner.nextLine();
                    productService.addProduct(productNameToAdd, Date.valueOf(expiryDateInput), prop);
                    break;

                case 2:
                    // Отримати продукт за ID
                    System.out.print("Введіть ID продукту: ");
                    int productIdToGet = scanner.nextInt();
                    scanner.nextLine(); // Очищення буфера вводу
                    productService.getProduct(productIdToGet, prop);
                    break;

                case 3:
                    // Оновити продукт
                    System.out.print("Введіть ID продукту для оновлення: ");
                    int productIdToUpdate = scanner.nextInt();
                    scanner.nextLine(); // Очищення буфера вводу
                    System.out.print("Введіть нову назву продукту: ");
                    String newProductName = scanner.nextLine();
                    System.out.print("Введіть нову дату терміну придатності продукту (yyyy-mm-dd): ");
                    String newExpiryDateInput = scanner.nextLine();
                    Product product = new Product(productIdToUpdate, newProductName, LocalDate.parse(newExpiryDateInput));
                    productService.updateProduct(product, prop);
                    break;

                case 4:
                    // Видалити продукт
                    System.out.print("Введіть ID продукту для видалення: ");
                    int productIdToRemove = scanner.nextInt();
                    productService.deleteProduct(productIdToRemove, prop);
                    break;

                case 5:
                    // Отримати всі продукти
                    productService.getAllProducts(prop);
                    break;

                case 6:
                    // Вихід з меню
                    System.out.println("Вихід з управління продуктами.");
                    return;

                default:
                    System.out.println("Невірний вибір, будь ласка, спробуйте ще раз.");
                    break;
            }
        }
    }

    private static void manageOrders(OrderService orderService, ClientService clientService, DishService dishService, EmployeeService employeeService, Scanner scanner, Properties prop) {
        System.out.println("----- Управління замовленнями -----");
        boolean exit = false;

        while (!exit) {
            System.out.println("1. Додати замовлення");
            System.out.println("2. Оновити замовлення");
            System.out.println("3. Отримати замовлення за ім'ям клієнта");
            System.out.println("4. Показати історію замовлень");
            System.out.println("5. Видалити замовлення");
            System.out.println("6. Вийти");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Зчитуємо новий рядок після вводу числа

            switch (choice) {
                case 1: // Додати замовлення
                    System.out.print("Введіть ID клієнта: ");
                    int clientId = scanner.nextInt();
                    Client client = clientService.getClientByIdNoOutput(clientId, prop);

                    System.out.print("Введіть ID працівника: ");
                    int employeeId = scanner.nextInt();
                    Employee employee = employeeService.getEmployeeByIdNoOutput(employeeId, prop);

                    System.out.print("Введіть назви страв (через кому): ");
                    String dishNamesInput = scanner.nextLine();
                    List<Dish> selectedDishes = new ArrayList<>();
                    String[] dishNames = dishNamesInput.split(",");

                    for (String dishName : dishNames) {
                        Dish dish = dishService.getDishByName(dishName.trim(), prop);
                        if (dish != null) {
                            selectedDishes.add(dish);
                        } else {
                            System.out.println("Страву " + dishName + " не знайдено.");
                        }
                    }

                    LocalDate orderDate = LocalDate.now();
                    orderService.addOrder(client, selectedDishes, employee, orderDate, prop);
                    System.out.println("Замовлення успішно додано.");
                    break;

                case 2: // Оновити замовлення
                    System.out.print("Введіть ID замовлення для оновлення: ");
                    int orderId = scanner.nextInt();

                    // Отримання нового клієнта
                    System.out.print("Введіть новий ID клієнта: ");
                    int newClientId = scanner.nextInt();
                    Client newClient = clientService.getClientByIdNoOutput(newClientId, prop);

                    // Отримання нового працівника
                    System.out.print("Введіть новий ID працівника: ");
                    int newEmployeeId = scanner.nextInt();
                    Employee newEmployee = employeeService.getEmployeeByIdNoOutput(newEmployeeId, prop);

                    // Отримання нових страв
                    System.out.print("Введіть нові назви страв (через кому): ");
                    scanner.nextLine(); // Скидання нового рядка
                    String newDishNamesInput = scanner.nextLine();
                    List<Dish> newDishes = new ArrayList<>();
                    String[] newDishNames = newDishNamesInput.split(",");

                    for (String dishName : newDishNames) {
                        Dish newDish = dishService.getDishByName(dishName.trim(), prop);
                        if (newDish != null) {
                            newDishes.add(newDish);
                        } else {
                            System.out.println("Страву " + dishName + " не знайдено.");
                        }
                    }

                    // Оновлення замовлення
                    orderService.updateOrder(orderId, newClient, newEmployee, LocalDate.now(), newDishes, prop);
                    System.out.println("Замовлення успішно оновлено.");
                    break;

                case 3: // Отримати замовлення за ім'ям клієнта
                    System.out.print("Введіть ім'я клієнта: ");
                    String clientName = scanner.nextLine();
                    orderService.getOrderByClientName(clientName, prop);
                    break;

                case 4: // Показати історію замовлень
                    orderService.getOrderHistory(prop);
                    break;

                case 5: // Видалити замовлення
                    System.out.print("Введіть ID замовлення для видалення: ");
                    int deleteOrderId = scanner.nextInt();
                    orderService.deleteOrder(deleteOrderId, prop);
                    System.out.println("Замовлення успішно видалено.");
                    break;

                case 6: // Вийти
                    exit = true;
                    break;

                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    public static void sort(Properties prop, Scanner scanner, ClientService clientService, OrderService orderService, FavoriteService favoriteService) {
        System.out.println("----- Меню сортування -----");
        boolean exit = false;

        while (!exit) {
            System.out.println("1. Клієнти за частотою відвідування");
            System.out.println("2. Клієнти за частотою обслуговування одним і тим самим працівником");
            System.out.println("3. Історія замовлень за ціною");
            System.out.println("4. Клієнти за назвою улюбленої страви чи напою");
            System.out.println("5. Вийти");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    clientService.printClientsSortedByVisits(prop);
                    break;

                case 2:
                    clientService.printClientsSortedByEmployeeFrequency(prop);
                    break;

                case 3:
                    orderService.getOrderHistorySort(prop);
                    break;

                case 4:
                    favoriteService.printClientsSortedByFavoriteDishOrDrink(prop);
                    break;

                case 5:
                    exit = true;
                    break;

                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    public static void main(String[] args) {
        // Читання конфігурації з файлу
        Properties prop = readConfiguration("db.properties");

        // Перевірка підключення
        if (prop != null) {
            try (Connection connection = DatabaseConnector.getConnection(prop)) {
                if (connection != null) {
                    System.out.println("Підключення успішне!");
                } else {
                    System.out.println("Не вдалось підключитись до бази даних.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        boolean authorised = false;
        do {
            System.out.println("1. Register");
            System.out.println("2. Login");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter option: ");
            String option = scanner.nextLine();
            if (option.equals("1")) {
                boolean success = false;
                do {
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    UserService userService = new UserService();
                    if (userService.registerUser(username, password)) {
                        authorised = true;
                        success = true;
                    }
                } while (!success);
            } else if (option.equals("2")) {
                boolean success = false;
                do {
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    UserService userService = new UserService();
                    if (userService.authenticateUser(username, password)) {
                        authorised = true;
                        success = true;
                    }
                } while (!success);
            }
        } while (!authorised);

        ClientService clientService = new ClientService();
        EmployeeService employeeService = new EmployeeService();
        ProductService productService = new ProductService();
        DishService dishService = new DishService();
        OrderService orderService = new OrderService();
        FavoriteService favoriteService = new FavoriteService();
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);

        while (!exit) {
            // Головне меню
            System.out.println("----- Меню -----");
            System.out.println("1. Клієнти");
            System.out.println("2. Працівники");
            System.out.println("3. Страви");
            System.out.println("4. Улюблені страви");
            System.out.println("5. Замовлення");
            System.out.println("6. Продукти");
            System.out.println("7. Меню сортування");
            System.out.println("0. Вихід");
            System.out.print("Виберіть опцію: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очищення буферу вводу

            switch (choice) {
                case 1:
                    manageClients(clientService, scanner, prop);
                    break;
                case 2:
                    manageEmployees(employeeService, scanner, prop);
                    break;
                case 3:
                    manageDishes(dishService, scanner, prop);
                    break;
                case 4:
                    manageFavorites(favoriteService, scanner, prop);
                    break;
                case 5:
                    manageOrders(orderService, clientService, dishService, employeeService, scanner, prop);
                    break;
                case 6:
                    manageProducts(productService, scanner, prop);
                    break;
                case 7:
                    sort(prop, scanner, clientService, orderService, favoriteService);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }


//        // CLIENT CRUD
//        clientService.addClient("Порошенко Петро", Date.valueOf(LocalDate.of(1965,9,26)), prop);
//        clientService.getClientById(2, prop);
//        Client client = new Client(2, "Короленко Влад", LocalDate.of(2005,1,1));
//        clientService.updateClient(client, prop);
//        clientService.deleteClient(2, prop);
//        clientService.getAllClients(prop);
//

//        // EMPLOYEE CRUD
//        employeeService.addEmployee("Федорчук Антон Сергійович", Date.valueOf(LocalDate.of(1994,11,17)), prop);
//        employeeService.getEmployee(3, prop);
//        Employee employee = new Employee(5, "Мельник Владислав Вікторович", LocalDate.of(2002,1,20));
//        employeeService.updateEmployee(employee, prop);
//        employeeService.deleteEmployee(2, prop);
//        employeeService.getAllEmployee(prop);
//
//        // DISH CRUD
//        dishService.addDish("Десерт", "Малиновий Круасан", new BigDecimal("70.00"), Date.valueOf(LocalDate.of(2024,10,20)), prop);
//        dishService.getDishByName("Круасан", prop);
//        Dish dish = new Dish(8L,"Кава","Капучино", new BigDecimal("65.00"), LocalDate.of(2024,12,1));
//        dishService.updateDish(dish, prop);
//        dishService.deleteDish(10, prop);
//        dishService.getAllDishes(prop);

        // FAVORITE CRUD
//        favoriteService.addFavorite(4, "Еспресо", prop);
//        favoriteService.getFavoriteByClientID(4, prop);
//        favoriteService.getAllClientsWithFavorites(prop);
//        favoriteService.removeFavorite("Голубчук Антон", "Еспресо", prop);

        // ORDER CRUD
//        Client client = clientService.getClientByIdNoOutput(11, prop);
//        Employee employee = employeeService.getEmployeeByIdNoOutput(4, prop);
//        List<String> dishNames = List.of("Італійське Печиво");
//        List<Dish> selectedDishes = new ArrayList<>();
//        for (String dishName : dishNames) {
//            Dish dish = dishService.getDishByName(dishName, prop);
//            if (dish != null) {
//                selectedDishes.add(dish);
//            } else {
//                System.out.println("Страву " + dishName + " не знайдено.");
//            }
//        }
//        LocalDate orderDate = LocalDate.now();
//        orderService.addOrder(client, selectedDishes, employee, orderDate, prop);

//        orderService.getOrderByClientName("Бренькач Денис", prop);
//        orderService.getOrderHistory(prop);
//        orderService.deleteOrder(7, prop);


        // PRODUCT CRUD
//        productService.addProduct("Фарш Курячий", Date.valueOf(LocalDate.of(2024,10,20)), prop);
//        productService.getAllProducts(prop);
//        productService.getProduct(2L,prop);
//        Product product = new Product(7L,"Базилік свіжий", LocalDate.of(2024,10,17));
//        productService.updateProduct(product,prop);
//        productService.deleteProduct(2,prop);


//        clientService.printClientsSortedByVisits(prop);
//        clientService.printClientsSortedByEmployeeFrequency(prop);
//        orderService.getOrderHistorySort(prop);
//        favoriteService.printClientsSortedByFavoriteDishOrDrink(prop);

 }
