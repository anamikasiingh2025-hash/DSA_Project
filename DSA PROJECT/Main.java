
import java.util.*;
import java.io.*;

public class Main {

    // 1. Product Class
    static class Product {
        String id;
        String name;
        int price;

        Product(String id, String name, int price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    // 2. Cart Item Class
    static class CartItem {
        Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        int getTotal() {
            return product.price * quantity;
        }
    }

    // 3. Node Class
    static class Node {
        CartItem data;
        Node next;

        Node(CartItem data) {
            this.data = data;
            this.next = null;
        }
    }

    // 4. Custom Linked List
    static class ShoppingCart {

        Node head;

        // Add item to Linked List
        void add(CartItem item) {

            Node newNode = new Node(item);

            if (head == null) {
                head = newNode;
                return;
            }

            Node temp = head;

            while (temp.next != null) {
                temp = temp.next;
            }

            temp.next = newNode;
        }

        // Remove particular item
        boolean remove(CartItem item) {

            if (head == null) {
                return false;
            }

            if (head.data == item) {
                head = head.next;
                return true;
            }

            Node temp = head;

            while (temp.next != null) {

                if (temp.next.data == item) {
                    temp.next = temp.next.next;
                    return true;
                }

                temp = temp.next;
            }

            return false;
        }

        // Display cart
        void display() {

            if (head == null) {
                System.out.println("Your cart is empty. Total: 0");
                return;
            }

            Node temp = head;
            int total = 0;
            int count = 1;

            while (temp != null) {

                CartItem item = temp.data;

                System.out.println(
                    count + ". " + item.product.name +
                    " | " + item.getTotal()
                );

                total += item.getTotal();
                count++;

                temp = temp.next;
            }

            System.out.println("Total: " + total);
        }

        // Bubble Sort: High to Low
        void sort() {

            if (head == null || head.next == null) {
                return;
            }

            boolean swapped;

            do {
                swapped = false;
                Node temp = head;

                while (temp.next != null) {

                    if (temp.data.getTotal() <
                        temp.next.data.getTotal()) {

                        CartItem t = temp.data;

                        temp.data = temp.next.data;
                        temp.next.data = t;

                        swapped = true;
                    }

                    temp = temp.next;
                }

            } while (swapped);
        }

        // Calculate total amount
        int getTotal() {

            int total = 0;
            Node temp = head;

            while (temp != null) {
                total += temp.data.getTotal();
                temp = temp.next;
            }

            return total;
        }

        // Clear cart
        void clear() {
            head = null;
        }

        // Check empty cart
        boolean isEmpty() {
            return head == null;
        }
    }

    // 5. HashMap: Product ID to Product
    static HashMap<String, Product> products = new HashMap<>();

    // Shopping Cart
    static ShoppingCart cart = new ShoppingCart();

    // 6. Stack: Last Added Item
    static Stack<CartItem> undoStack = new Stack<>();

    static Scanner sc = new Scanner(System.in);

    // 7. Load Products from File
    static void loadProducts() {

        File file = new File("products.txt");

        if (!file.exists()) {
            addDefaultProducts();
            return;
        }

        try (BufferedReader br =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 3) {

                    String id = data[0].trim();
                    String name = data[1].trim();
                    int price = Integer.parseInt(data[2].trim());

                    if (!id.isEmpty() &&
                        !name.isEmpty() &&
                        price >= 0) {

                        products.put(
                            id,
                            new Product(id, name, price)
                        );
                    }
                }
            }

            if (products.isEmpty()) {
                addDefaultProducts();
            }

        } catch (IOException | NumberFormatException e) {

            System.out.println("Error loading products.");
            addDefaultProducts();
        }
    }

    // 8. Default Products
    static void addDefaultProducts() {

        products.put("P101",
            new Product("P101", "Notebook", 50));

        products.put("P102",
            new Product("P102", "Pen", 10));

        products.put("P103",
            new Product("P103", "Bag", 800));
    }

    // 9. Save Products to File
    static void saveProducts() {

        try (BufferedWriter bw =
                 new BufferedWriter(new FileWriter("products.txt"))) {

            for (Product p : products.values()) {

                bw.write(
                    p.id + "," + p.name + "," + p.price
                );

                bw.newLine();
            }

            System.out.println("Products saved successfully.");

        } catch (IOException e) {

            System.out.println("Error saving products.");
        }
    }

    // 10. View Products
    static void viewProducts() {

        System.out.println("\n----- AVAILABLE PRODUCTS -----");

        for (Product p : products.values()) {

            System.out.println(
                p.id + " | " + p.name +
                " | " + p.price
            );
        }
    }

    // 11. Read Positive Quantity
    static int readQuantity() {

        while (true) {

            System.out.print("Quantity: ");

            String input = sc.nextLine().trim();

            try {

                int quantity = Integer.parseInt(input);

                if (quantity > 0) {
                    return quantity;
                }

                System.out.println(
                    "Quantity must be a positive whole number."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                    "Enter a valid whole number."
                );
            }
        }
    }

    // 12. Add Product to Cart
    static void addToCart() {

        System.out.print("Product ID: ");

        String id = sc.nextLine().trim().toUpperCase();

        Product p = products.get(id);

        if (p == null) {

            System.out.println("Product " + id + " not found.");
            return;
        }

        int quantity = readQuantity();

        CartItem item = new CartItem(p, quantity);

        cart.add(item);

        undoStack.push(item);

        System.out.println(
            "Added: " + p.name + " x" +
            quantity + " = " + item.getTotal()
        );
    }

    // 13. Remove Last Added Item Using Stack
    static void removeLastItem() {

        if (undoStack.isEmpty()) {

            System.out.println("No item to remove.");
            return;
        }

        CartItem item = undoStack.pop();

        if (cart.remove(item)) {

            System.out.println(
                "Removed: " + item.product.name +
                " x" + item.quantity
            );
        }
    }

    // 14. View Shopping Cart
    static void viewCart() {

        System.out.println("\n----- YOUR SHOPPING CART -----");

        cart.display();
    }

    // 15. Sort Cart
    static void sortCart() {

        cart.sort();

        System.out.println(
            "\n----- SORTED CART (HIGH TO LOW) -----"
        );

        cart.display();
    }

    // 16. Checkout
    static void checkout() {

        if (cart.isEmpty()) {

            System.out.println(
                "Your cart is empty. Total: 0"
            );

            return;
        }

        int total = cart.getTotal();

        System.out.println(
            "Order placed. Total: " + total
        );

        cart.clear();

        undoStack.clear();
    }

    // 17. Main Method
    public static void main(String[] args) {

        loadProducts();

        int choice = 0;

        do {

            System.out.println(
                "\n===== ONLINE SHOPPING CART SYSTEM ====="
            );

            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Last Added Item");
            System.out.println("5. Sort Cart (High to Low)");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();

            try {

                choice = Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println("Invalid menu option!");
                choice = 0;
                continue;
            }

            switch (choice) {

                case 1:
                    viewProducts();
                    break;

                case 2:
                    addToCart();
                    break;

                case 3:
                    viewCart();
                    break;

                case 4:
                    removeLastItem();
                    break;

                case 5:
                    sortCart();
                    break;

                case 6:
                    checkout();
                    break;

                case 7:
                    saveProducts();
                    System.out.println("Thank you for shopping!");
                    break;

                default:
                    System.out.println("Invalid menu option!");
            }

        } while (choice != 7);

        sc.close();
    }
}

