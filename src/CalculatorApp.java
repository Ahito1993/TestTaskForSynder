import util.Calculator;
import util.InvalidDataException;

import java.util.Scanner;

/**
 * Основной класс приложения
 * Валидными вводимыми данными являются: [0-9], "*", "/", "+", "-", "%", "^", ".", "(", ")"
 * Пробелы не влияют на работу программы (игнорируются)
 * Запись числа в виде 005 или 5.000 считается валидной(при выводе на экран лишние нули убираются)
 * В случае некорректности данных будет выведено сообщение об ошибке
 */
public class CalculatorApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Введите выражение и нажмите кнопку\"Enter\":");
        String expression = scanner.nextLine();
        try {
            Calculator.calculate(expression);
        } catch (InvalidDataException | NumberFormatException e) {
            System.out.println();
            System.out.println("Некорректно введенные данные");
        } catch (ArithmeticException e) {
            System.out.println();
            System.out.println("Попытка деления на 0");
        }
    }
}
