package util;

import java.util.List;
import java.util.Stack;

import static util.Parser.*;

/**
 *  Класс, предназначенный для подсчета результата
 */
public class Calculator {

    public static void calculate(String expression) throws InvalidDataException, NumberFormatException {
        // Пробуем перевести строку к обратной польской записи
        // Если данные введены некорректно, выбрасывается исключение InvalidDataException
        String expressionToRPN = parseToRPN(expression);
        System.out.println();
        System.out.println("Выражение в виде обратной польской записи:");
//        System.out.println(expressionToRPN);

        // Делаем список лексем и инициализируем стек для дальнейшего подсчета результата
        List<String> tokensRPN = parseToRPNTokens(expressionToRPN);
        Stack<Double> stack = new Stack<>();
        // Делаем проход по списку лексем:
        // - Если элемент является числом - заносим в стек
        // - Если элемент оператор:
        //      - "%" - достаем с вершины стека число, делим на 100 и кладем обратно в стек
        //      - "±" - достаем с вершины стека число, меняем знак на противоположный и кладем обратно в стек
        //      - "+", "-", "*", "/" - достаем с вершины стека два числа и проделываем соответствующую операцию над ними,
        //      начиная со второго (более глубокого) числа. При операторе "/" может выскочить уведомление о попытке деления на 0
        //      - "^" - правоассоциативный оператор. Это значит, что если введено выражение типа "a^b^c", оно должно выполняться, как
        //      "a^(b^c)". Алгоритм подсчета следующий:
        //          1. Создаем счетчик подряд идущих операторов "^"
        //          2. Идем по списку далее:
        //              - если встречаем число, кладем в стек
        //              - если встречаем "^", увеличиваем счетчик на 1
        //              - если встречаем "%", делаем соответствующую операцию (см. строка 25)
        //              - если встречаем другой оператор, либо список закончен:
        //                  1. если после последнего "^" идет число, то достаем его из стека
        //                  2. пока счетчик не равен 0, достаем 2 элемента из стека и делаем операцию возведения в степень, начиная со второго числа
        //                  (более глубокое число возводим в степень, равную верхнему числу) и кладем обратно в стек
        //                  3. заносим обратно в стек число, если доставали его в пункте 1
        //                  4. уменьшаем счетчик списка лексем на 1
        // - В результате в стеке остается одно число - результат вычислений

        for (int i = 0; i < tokensRPN.size(); i++) {
            if (Validator.isNumeric(tokensRPN.get(i))) {
                try {
                    System.out.print(Integer.parseInt(tokensRPN.get(i)) + " ");
                } catch (NumberFormatException e) {
                    System.out.print(Double.parseDouble(tokensRPN.get(i)) + " ");
                }

            } else {
                System.out.print(tokensRPN.get(i) + " ");
            }

            if (getPriority(tokensRPN.get(i)) == 0) {
                stack.push(Double.parseDouble(tokensRPN.get(i)));
                continue;
            }

            double operand;
            switch (tokensRPN.get(i)) {
                case ("%"):
                    operand = stack.pop();
                    stack.push(operand / 100);
                    break;
                case ("^"):
                    int counter = 0;
                    while (getPriority(tokensRPN.get(i)) >= 5 || getPriority(tokensRPN.get(i)) == 0) {
                        if (getPriority(tokensRPN.get(i)) == 0) {
                            stack.push(Double.parseDouble(tokensRPN.get(i)));
                            i++;
                            if (i == tokensRPN.size()) break;
                        } else if (getPriority(tokensRPN.get(i)) == 5) {
                            counter++;
                            i++;
                            if (i == tokensRPN.size()) break;
                        } else if (getPriority(tokensRPN.get(i)) == 6) {
                            operand = stack.pop();
                            stack.push(operand / 100);
                            i++;
                            if (i == tokensRPN.size()) break;
                        }
                    }
                    if (Validator.isNumeric(tokensRPN.get(i - 1))) {
                        double tmp = stack.pop();
                        while (counter != 0) {
                            double exponent = stack.pop();
                            double base = stack.pop();
                            stack.push(Math.pow(base, exponent));
                            counter--;
                        }
                        stack.push(tmp);
                    } else {
                        while (counter != 0) {
                            double exponent = stack.pop();
                            double base = stack.pop();
                            stack.push(Math.pow(base, exponent));
                            counter--;
                        }
                    }
                    i--;

                    break;
                case ("±"):
                    operand = stack.pop();
                    stack.push(-operand);
                    break;
                case ("*"):
                    operand = stack.pop() * stack.pop();
                    stack.push(operand);
                    break;
                case ("/"):
                    if (stack.peek() == 0) throw new ArithmeticException();
                    operand = 1 / (stack.pop() / stack.pop());
                    stack.push(operand);
                    break;
                case ("+"):
                    operand = stack.pop() + stack.pop();
                    stack.push(operand);
                    break;
                case ("-"):
                    operand = -(stack.pop() - stack.pop());
                    stack.push(operand);
                    break;
                default:
                    break;
            }
        }
        System.out.println("\n");
        System.out.println("Результат вычисления:");
        System.out.println(stack.peek());
        stack.pop();
    }

}
