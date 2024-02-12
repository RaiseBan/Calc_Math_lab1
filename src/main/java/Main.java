import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создаем объект Scanner для чтения данных с клавиатуры
        Scanner input = new Scanner(System.in);

        System.out.println("Введите '1' для ввода данных с клавиатуры или '2' для ввода данных из файла:");
        int method = input.nextInt();

        int n = 0;
        double[][] a = null;
        double[] b = null;
        double[] x = null;
        double epsilon = 0;
        int maxIterations = 0;

        // Ввод данных с клавиатуры
        if (method == 1) {
            System.out.println("Введите размер матрицы (n <= 20):");
            n = input.nextInt();

            a = new double[n][n];
            b = new double[n];
            x = new double[n]; // Начальные приближения

            System.out.println("Введите элементы матрицы A:");
            for (int i = 0; i < n; i++) {
                System.out.print("|");
                for (int j = 0; j < n; j++) {
                    a[i][j] = input.nextDouble();
                }
            }

            System.out.println("Введите элементы вектора b:");
            System.out.print("|");
            for (int i = 0; i < n; i++) {
                b[i] = input.nextDouble();
            }

            System.out.println("Введите начальные приближения:");
            System.out.print("|");
            for (int i = 0; i < n; i++) {
                x[i] = input.nextDouble();
            }

            System.out.println("Введите точность (epsilon):");
            epsilon = input.nextDouble();

            System.out.println("Введите максимальное число итераций:");
            maxIterations = input.nextInt();
        }
        // Ввод данных из файла
        else if (method == 2) {
            System.out.println("Введите путь к файлу:");
            String filePath = input.next();

            try {
                File file = new File(filePath);
                Scanner fileScanner = new Scanner(file);

                n = fileScanner.nextInt();
                a = new double[n][n];
                b = new double[n];
                x = new double[n]; // Начальные приближения

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        a[i][j] = fileScanner.nextDouble();
                    }
                }

                for (int i = 0; i < n; i++) {
                    b[i] = fileScanner.nextDouble();
                }

                for (int i = 0; i < n; i++) {
                    x[i] = fileScanner.nextDouble();
                }

                epsilon = fileScanner.nextDouble();
                maxIterations = fileScanner.nextInt();

                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден. Проверьте путь и повторите попытку.");
                return;
            }
        }
        else {
            System.out.println("Некорректный ввод. Пожалуйста, введите '1' или '2'.");
            input.close();
            return;
        }

        System.out.println("Исходная матрица:");
        printMatrix(a);

        if (!hasDiagonalDominance(a, b)) {
            System.out.println("Не удалось достичь диагонального преобладания.");
            System.out.println("Преобразованная матрица:");
            printMatrix(a);
        } else {
            System.out.println("Диагональное преобладание достигнуто.");
            System.out.println("Преобразованная матрица:");
            printMatrix(a);
        }

        // Проверка на диагональное преобладание


        // Вызов метода Гаусса-Зейделя

        if (gaussSeidel(a, b, x, epsilon, maxIterations)) {
            printErrors(a, b, x);
        }


        // Закрываем Scanner, если он больше не нужен
        input.close();
    }




        public static boolean gaussSeidel(double[][] a, double[] b, double[] x, double epsilon, int maxIterations) {
            int n = x.length;
            double[] prev = new double[n];
            double norm;
            boolean isSolved = true;
            int count = 0;

            for (int k = 1; k <= maxIterations; k++) {
                count += 1;
                for (int i = 0; i < n; i++) {
                    double sum = 0;
                    for (int j = 0; j < n; j++) {
                        if (j != i) {
                            sum += a[i][j] * x[j];
                        }
                    }
                    prev[i] = x[i];
                    x[i] = (b[i] - sum) / a[i][i];
                }

                norm = 0;
                for (int i = 0; i < n; i++) {
                    norm = Math.max(Math.abs(x[i] - prev[i]), norm);
                }

                if (norm < epsilon) {
                    break; // Сходимость достигнута
                }
                if (k == maxIterations){
                    isSolved = false;
                }
            }
            if (isSolved){
                printSolution(x);
                System.out.println("Решение было найдено за " + count + " итераций");
                return true;
            }else{
                System.out.println("Не хватило итерций, чтобы решить СЛАУ (сходимость не достигнута)");
                return false;
            }

        }


        public static void printSolution(double[] x) {
            System.out.println("Решение:");
            for (int i = 0; i < x.length; i++) {
                System.out.printf("x%d = %.4f\n", i+1, x[i]);
            }
        }




    public static boolean hasDiagonalDominance(double[][] a, double[] b) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < a.length; i++) {
                double sum = 0;
                for (int j = 0; j < a.length; j++) {
                    if (i != j) sum += Math.abs(a[i][j]);
                }
                if (Math.abs(a[i][i]) <= sum) {
                    // Пытаемся найти строку ниже, чтобы поменять местами
                    for (int k = i + 1; k < a.length; k++) {
                        double rowSum = 0;
                        for (int j = 0; j < a.length; j++) {
                            if (i != j) rowSum += Math.abs(a[k][j]);
                        }
                        if (Math.abs(a[k][i]) > rowSum) {
                            // Перестановка строк
                            double[] tempA = a[i];
                            double tempB = b[i];
                            a[i] = a[k];
                            b[i] = b[k];
                            a[k] = tempA;
                            b[k] = tempB;

                            changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);

        // Проверяем, достигнуто ли преобладание после перестановок
        for (int i = 0; i < a.length; i++) {
            double sum = 0;
            for (int j = 0; j < a.length; j++) {
                if (i != j) sum += Math.abs(a[i][j]);
            }
            if (Math.abs(a[i][i]) <= sum) {
                return false; // Невозможно достичь диагонального преобладания
            }
        }
        return true; // Диагональное преобладание достигнуто
    }

    public static void printMatrix(double[][] a){
        System.out.println();
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a.length; j++){
                System.out.print(a[i][j] + "  ");
            }
            System.out.println();
        }
    }

    public static void printErrors(double[][] a, double[] b, double[] x) {
        double[] error = new double[b.length];
        for (int i = 0; i < a.length; i++) {
            double ax = 0;
            for (int j = 0; j < a[0].length; j++) {
                ax += a[i][j] * x[j];
            }
            error[i] = ax - b[i];
        }

        System.out.println("Вектор погрешностей:");
        for (int i = 0; i < error.length; i++) {
            System.out.printf("e%d = %.4f\n", i + 1, error[i]);
        }
    }



}
